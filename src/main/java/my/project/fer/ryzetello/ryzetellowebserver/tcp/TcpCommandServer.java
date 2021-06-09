package my.project.fer.ryzetello.ryzetellowebserver.tcp;

import my.project.fer.ryzetello.ryzetellowebserver.config.TcpConfig;
import my.project.fer.ryzetello.ryzetellowebserver.constants.MessageConstants;
import my.project.fer.ryzetello.ryzetellowebserver.model.Drone;
import my.project.fer.ryzetello.ryzetellowebserver.service.DroneService;
import my.project.fer.ryzetello.ryzetellowebserver.service.WebSocketService;
import my.project.fer.ryzetello.ryzetellowebserver.state.HealthyDrones;
import my.project.fer.ryzetello.ryzetellowebserver.state.VideoClientExecutorsState;
import my.project.fer.ryzetello.ryzetellowebserver.state.VideoClientsState;
import my.project.fer.ryzetello.ryzetellowebserver.udp.UdpVideoMultiServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    TCP Server that sends commands to drone and receives state
 */
@Component
public class TcpCommandServer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpCommandServer.class);

    private static final int ALLOWED_CLIENTS_COUNT = 10;

    private ServerSocket serverSocket;

    private TcpConfig tcpConfig;

    private final DroneService droneService;

    private final WebSocketService webSocketService;

    private final HealthyDrones healthyDrones;

    private final VideoClientsState videoClientsState;

    private final VideoClientExecutorsState videoClientExecutorsState;

    private UdpVideoMultiServer udpVideoMultiServer;

    private ExecutorService executorService;

    private ExecutorService serverExecutorService;

    private Map<String, Socket> clientSocketsMap;

    public TcpCommandServer(TcpConfig tcpConfig,
        DroneService droneService,
        WebSocketService webSocketService,
        HealthyDrones healthyDrones,
        VideoClientsState videoClientsState,
        VideoClientExecutorsState videoClientExecutorsState,
        UdpVideoMultiServer udpVideoMultiServer) throws IOException {
        this.tcpConfig = tcpConfig;
        this.droneService = droneService;
        this.webSocketService = webSocketService;
        this.healthyDrones = healthyDrones;
        this.videoClientsState = videoClientsState;
        this.videoClientExecutorsState = videoClientExecutorsState;
        this.udpVideoMultiServer = udpVideoMultiServer;
        this.executorService = Executors.newFixedThreadPool(ALLOWED_CLIENTS_COUNT);
        this.serverExecutorService = Executors.newFixedThreadPool(1);
        this.serverSocket = new ServerSocket(tcpConfig.getPort());
        this.clientSocketsMap = new LinkedHashMap<>();

        serverExecutorService.submit(this);
    }

    public void run() {
        while (true) {
            Socket acceptedClientSocket;
            try {
                acceptedClientSocket = serverSocket.accept();
                String clientSocketKey =
                    acceptedClientSocket.getInetAddress().getHostAddress() + ":" + acceptedClientSocket.getPort();
                clientSocketsMap.put(clientSocketKey, acceptedClientSocket);

                executorService.submit(new TcpCommandClientHandler(acceptedClientSocket));
            } catch (IOException e) {
                LOGGER.error("Socket error.", e);
            }
        }
    }

    private class TcpCommandClientHandler implements Runnable {

        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        private UUID droneId;
        private String host;
        private int port;

        public TcpCommandClientHandler(Socket socket) {
            this.clientSocket = socket;

            String receivedDroneHost = clientSocket.getInetAddress().getHostAddress();
            int receivedDronePort = clientSocket.getPort();

            if (!droneService.existsByHostAndPort(receivedDroneHost, receivedDronePort)) {
                // Drone registration phase
                final Drone newDrone = new Drone();
                newDrone.setHost(receivedDroneHost);
                newDrone.setPort(receivedDronePort);

                final Drone savedDrone = droneService.addDrone(newDrone);
                healthyDrones.addDrone(savedDrone.getId());

                // WS
                List<UUID> dronesAdded = new ArrayList<>();
                dronesAdded.add(savedDrone.getId());
                webSocketService.notifyDronesAdded(dronesAdded);
                //

                // Create UDP video and ffmpeg port for drone
                videoClientsState.setVideoAndFffmpegPortsForDroneId(savedDrone.getId());

                // Save info
                this.droneId = savedDrone.getId();
                this.host = savedDrone.getHost();
                this.port = savedDrone.getPort();

                LOGGER.info("Registered drone: {}:{}, ID: {}.", receivedDroneHost, receivedDronePort, savedDrone.getId());
            }
        }

        @Override
        public void run() {
            UdpVideoMultiServer.UdpVideoClientHandler udpVideoClientHandler = null;

            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Send UDP video port to drone
                Integer droneVideoPort = videoClientsState.getVideoPort(droneId);

                if (droneVideoPort != null) {
                    out.println("ASSIGNED_VIDEO_PORT:" + videoClientsState.getVideoPort(droneId));

                    ExecutorService videoClientExecutorService = Executors.newFixedThreadPool(1);
                    videoClientExecutorsState.add(droneId, videoClientExecutorService);
                    udpVideoClientHandler = udpVideoMultiServer.getVideoClientHandlerInstance(droneId, host, port);
                    videoClientExecutorService.submit(udpVideoClientHandler);
                }
                //

                String receivedMessage;
                while ((receivedMessage = in.readLine()) != null) {

                    String receivedDroneHost = clientSocket.getInetAddress().getHostAddress();
                    int receivedDronePort = clientSocket.getPort();

                    LOGGER.info("Server received message: '{}' from {}:{}.", receivedMessage, receivedDroneHost,
                        receivedDronePort);
                    if (receivedMessage.startsWith(MessageConstants.HEALTH_CHECK)) {
                        // Health check case 1: RaspPi and Drone ok
                        // Add to healthy list
                        if (receivedMessage.startsWith(MessageConstants.HEALTH_CHECK_ALL_OK)) {
                            final Drone droneByHostAndPort =
                                droneService.getByHostAndPort(receivedDroneHost, receivedDronePort);
                            healthyDrones.addDrone(droneByHostAndPort.getId());
                        }

                        // Health check case 2: RaspPi ok, Drone down
                        if (receivedMessage.startsWith(MessageConstants.HEALTH_CHECK_DRONE_OFFLINE)) {
                            removeDrone(clientSocket);
                            clientSocket.close();

                            if (udpVideoClientHandler != null) {
                                udpVideoClientHandler.getFfmpegProcess().destroyForcibly();
                                udpVideoClientHandler.getWebSocketRelayProcess().destroyForcibly();
                            }

                            videoClientsState.removeAllPortsForDroneId(droneId);
                            videoClientExecutorsState.getForId(droneId).shutdown();
                        }

                    } else {
                        // TODO other cases if needed
                    }
                }

                removeDrone(clientSocket);
                clientSocket.close();

                if (udpVideoClientHandler != null) {
                    udpVideoClientHandler.getFfmpegProcess().destroyForcibly();
                    udpVideoClientHandler.getWebSocketRelayProcess().destroyForcibly();
                }

            } catch (Exception e) {
                LOGGER.error("Socket error.", e);

                removeDrone(clientSocket);

                if (udpVideoClientHandler != null) {
                    udpVideoClientHandler.getFfmpegProcess().destroyForcibly();
                    udpVideoClientHandler.getWebSocketRelayProcess().destroyForcibly();
                }

                videoClientsState.removeAllPortsForDroneId(droneId);
                videoClientExecutorsState.getForId(droneId).shutdown();
            }
        }

    }

    public Socket getClientSocketForHostAndPort(String host, int port) {
        String key = host + ":" + port;
        return clientSocketsMap.get(key);
    }

    private void removeDrone(Socket clientSocket) {
        String clientSocketKey = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
        clientSocketsMap.remove(clientSocketKey);

        UUID droneToRemove = droneService.getByHostAndPort(clientSocket.getInetAddress().getHostAddress(),
            clientSocket.getPort()).getId();

        droneService.deleteDrone(droneToRemove);

        List<UUID> deletedDrones = new ArrayList<>();
        deletedDrones.add(droneToRemove);

        // WS
        webSocketService.notifyDronesRemoved(deletedDrones);
    }
}

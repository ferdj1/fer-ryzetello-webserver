package my.project.fer.ryzetello.ryzetellowebserver.udp;

import my.project.fer.ryzetello.ryzetellowebserver.config.FfmpegConfig;
import my.project.fer.ryzetello.ryzetellowebserver.config.JsmpegConfig;
import my.project.fer.ryzetello.ryzetellowebserver.config.UdpConfig;
import my.project.fer.ryzetello.ryzetellowebserver.service.DroneService;
import my.project.fer.ryzetello.ryzetellowebserver.state.VideoClientExecutorsState;
import my.project.fer.ryzetello.ryzetellowebserver.state.VideoClientsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class UdpVideoMultiServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpVideoMultiServer.class);

    private static final int ALLOWED_CLIENTS_COUNT = 10;

    private UdpConfig udpConfig;
    private FfmpegConfig ffmpegConfig;
    private JsmpegConfig jsmpegConfig;
    private VideoClientsState videoClientsState;
    private VideoClientExecutorsState videoClientExecutorsState;
    private DroneService droneService;

    @Autowired
    public UdpVideoMultiServer(UdpConfig udpConfig,
        FfmpegConfig ffmpegConfig,
        JsmpegConfig jsmpegConfig,
        VideoClientsState videoClientsState,
        VideoClientExecutorsState videoClientExecutorsState,
        DroneService droneService) {
        this.udpConfig = udpConfig;
        this.ffmpegConfig = ffmpegConfig;
        this.jsmpegConfig = jsmpegConfig;
        this.videoClientsState = videoClientsState;
        this.videoClientExecutorsState = videoClientExecutorsState;
        this.droneService = droneService;
    }

    public class UdpVideoClientHandler implements Runnable {

        private DatagramSocket videoSocket;
        private String host;
        private int port;
        private UUID droneId;

        private byte[] receiveVideoBuffer = new byte[1460];
        private byte[] sendVideoBuffer = new byte[1460];

        // Ffmpeg and websocket-relay processes
        private Process ffmpegProcess;
        private Process webSocketRelayProcess;

        public UdpVideoClientHandler(UUID droneId, String host, int port) throws SocketException {
            this.droneId = droneId;
            this.host = host;
            this.port = port;
            this.videoSocket = videoSocket();
        }

        @Override
        public void run() {
            try {
                // FFMPEG
                ExecutorService ffmpegExecutorService = Executors.newFixedThreadPool(1);
                ffmpegExecutorService.submit(() -> {
                    try {
                        String command = ffmpegConfig.getFfmpegPath();
                        LOGGER.info("Executing ffmpeg command.");
                        ProcessBuilder processBuilder = new ProcessBuilder(command, "-framerate", "25", "-i", "udp://0.0.0.0:" + videoClientsState.getFfmpegPort(droneId), "-f", "mpegts", "-codec:v", "mpeg1video", "-s", "640x480", "-b:v", "1000k", "-bf", "0", "http://" + udpConfig.getHost() + ":" + videoClientsState.getHttpStreamPort(droneId) + "/supersecret");
                        processBuilder.inheritIO();
                        ffmpegProcess = processBuilder.start();
                        ffmpegProcess.waitFor();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                // WS relay
                ExecutorService webSocketRelayExecutorService = Executors.newFixedThreadPool(1);
                webSocketRelayExecutorService.submit(() -> {
                    try {
                        String command = jsmpegConfig.getNodePath();
                        LOGGER.info("Executing webSocketRelay command.");
                        ProcessBuilder processBuilder = new ProcessBuilder(command, jsmpegConfig.getWebSocketRelayPath(), "supersecret", String.valueOf(videoClientsState.getHttpStreamPort(droneId)), String.valueOf(videoClientsState.getWebSocketPort(droneId)));
                        processBuilder.inheritIO();
                        webSocketRelayProcess = processBuilder.start();
                        webSocketRelayProcess.waitFor();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveVideoBuffer, receiveVideoBuffer.length);
                    videoSocket.receive(receivePacket);

                    byte[] receivedVideoData = receivePacket.getData();

                    final String receivedDroneHost = receivePacket.getAddress().getHostAddress();
                    final Integer receivedDronePort = receivePacket.getPort();

                    LOGGER.info("Server received video data with size {} from {}:{}.", receivedVideoData.length,
                        receivedDroneHost, receivedDronePort);

                    // To FFMPEG
                    sendVideoBuffer = receivedVideoData;
                    DatagramPacket sendPacket = new DatagramPacket(sendVideoBuffer, sendVideoBuffer.length,
                        InetAddress.getByName(udpConfig.getHost()), videoClientsState.getFfmpegPort(droneId));
                    udpConfig.videoServerSocket().send(sendPacket);

                    sendVideoBuffer = new byte[1460];
                    //

                    // Buffer cleanup
                    receiveVideoBuffer = new byte[1460];
                }
            } catch (Exception e) {
                LOGGER.error("Socket error.", e);
            }
        }

        private DatagramSocket videoSocket() throws SocketException {
            if (videoSocket == null) {
                videoSocket =
                    new DatagramSocket(videoClientsState.getVideoPort(droneService.getIdByHostAndPort(host, port)));
                videoSocket.setSendBufferSize(1460);
                videoSocket.setReceiveBufferSize(1460);
                LOGGER.info("Created new video socket instance.");
            }

            return videoSocket;
        }

        public Process getFfmpegProcess() {
            return ffmpegProcess;
        }

        public Process getWebSocketRelayProcess() {
            return webSocketRelayProcess;
        }

    }

    public UdpVideoClientHandler getVideoClientHandlerInstance(UUID droneId, String host, int port) throws SocketException {
        return new UdpVideoClientHandler(droneId, host, port);
    }

}

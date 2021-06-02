package my.project.fer.ryzetello.ryzetellowebserver.udp;

import my.project.fer.ryzetello.ryzetellowebserver.config.UdpConfig;
import my.project.fer.ryzetello.ryzetellowebserver.constants.MessageConstants;
import my.project.fer.ryzetello.ryzetellowebserver.model.Drone;
import my.project.fer.ryzetello.ryzetellowebserver.service.DroneService;
import my.project.fer.ryzetello.ryzetellowebserver.service.WebSocketService;
import my.project.fer.ryzetello.ryzetellowebserver.state.HealthyDrones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UdpVideoServerRunnable implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpVideoServerRunnable.class);

    private byte[] receiveVideoBuffer = new byte[1024];
    private byte[] sendVideoBuffer = new byte[1024];

    private final UdpConfig udpConfig;
    private final DroneService droneService;
    private final WebSocketService webSocketService;
    private final HealthyDrones healthyDrones;

    @Autowired
    public UdpVideoServerRunnable(UdpConfig udpConfig, DroneService droneService, WebSocketService webSocketService, HealthyDrones healthyDrones) {
        this.udpConfig = udpConfig;
        this.droneService = droneService;
        this.webSocketService = webSocketService;
        this.healthyDrones = healthyDrones;
    }

    @Override
    public void run() {
        try {
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveVideoBuffer, receiveVideoBuffer.length);
                udpConfig.getVideoServerSocket().receive(receivePacket);

                byte[] receivedVideoData = receivePacket.getData();

                final String receivedDroneHost = receivePacket.getAddress().getHostAddress();
                final Integer receivedDronePort = receivePacket.getPort();

                LOGGER.info("Server received video data with size {} from {}:{}.", receivedVideoData.length, receivedDroneHost, receivedDronePort);

                // TODO remove temp stream
                sendVideoBuffer = receivedVideoData;
                DatagramPacket sendPacket = new DatagramPacket(sendVideoBuffer, sendVideoBuffer.length, InetAddress.getByName(udpConfig.getHost()),
                    41234);
                udpConfig.videoServerSocket().send(sendPacket);

                sendVideoBuffer = new byte[1460];
                // TODO END

                // Buffer cleanup
                receiveVideoBuffer = new byte[1460];
            }
        } catch (Exception e) {
            LOGGER.error("Socket error.", e);
        }

    }

}

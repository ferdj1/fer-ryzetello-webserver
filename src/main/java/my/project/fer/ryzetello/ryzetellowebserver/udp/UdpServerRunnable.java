package my.project.fer.ryzetello.ryzetellowebserver.udp;

import my.project.fer.ryzetello.ryzetellowebserver.config.UdpConfig;
import my.project.fer.ryzetello.ryzetellowebserver.constants.MessageConstants;
import my.project.fer.ryzetello.ryzetellowebserver.model.Drone;
import my.project.fer.ryzetello.ryzetellowebserver.service.DroneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;

@Component
public class UdpServerRunnable implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpServerRunnable.class);

    private byte[] receiveBuffer = new byte[1024];
    private byte[] sendBuffer = new byte[1024];

    private final UdpConfig udpConfig;
    private final DroneService droneService;

    @Autowired
    public UdpServerRunnable(UdpConfig udpConfig, DroneService droneService) {
        this.udpConfig = udpConfig;
        this.droneService = droneService;
    }

    @Override
    public void run() {
        try {
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                udpConfig.getServerSocket().receive(receivePacket);

                String receivedMessage = new String(receivePacket.getData()).replaceAll("\0", "");
                LOGGER.info("Server received message: " + receivedMessage);

                final String receivedDroneHost = receivePacket.getAddress().getHostAddress();
                final Integer receivedDronePort = receivePacket.getPort();

                if (receivedMessage.startsWith(MessageConstants.REGISTER)
                    && !droneService.existsByHostAndPort(receivedDroneHost, receivedDronePort)) {
                    // Drone registration phase
                    final Drone newDrone = new Drone();
                    newDrone.setHost(receivedDroneHost);
                    newDrone.setPort(receivedDronePort);

                    final Drone savedDrone = droneService.addDrone(newDrone);

                    LOGGER.info("Registered drone: {}:{}, ID: {}.", receivedDroneHost, receivedDronePort, savedDrone.getId());
                } else {
                    // TODO other cases if needed
                }

                // Buffer cleanup
                receiveBuffer = new byte[1024];
            }
        } catch (Exception e) {
            LOGGER.error("Socket error.", e);
        }

    }

}

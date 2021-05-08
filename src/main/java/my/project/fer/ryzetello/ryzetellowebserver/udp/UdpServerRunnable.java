package my.project.fer.ryzetello.ryzetellowebserver.udp;

import my.project.fer.ryzetello.ryzetellowebserver.config.UdpConfig;
import my.project.fer.ryzetello.ryzetellowebserver.constants.MessageConstants;
import my.project.fer.ryzetello.ryzetellowebserver.model.Drone;
import my.project.fer.ryzetello.ryzetellowebserver.service.DroneService;
import my.project.fer.ryzetello.ryzetellowebserver.state.HealthyDrones;
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
    private final HealthyDrones healthyDrones;

    @Autowired
    public UdpServerRunnable(UdpConfig udpConfig, DroneService droneService, HealthyDrones healthyDrones) {
        this.udpConfig = udpConfig;
        this.droneService = droneService;
        this.healthyDrones = healthyDrones;
    }

    @Override
    public void run() {
        try {
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                udpConfig.getServerSocket().receive(receivePacket);

                String receivedMessage = new String(receivePacket.getData()).replaceAll("\0", "");

                final String receivedDroneHost = receivePacket.getAddress().getHostAddress();
                final Integer receivedDronePort = receivePacket.getPort();

                LOGGER.info("Server received message: '{}' from {}:{}.", receivedMessage, receivedDroneHost, receivedDronePort);

                if (receivedMessage.startsWith(MessageConstants.REGISTER)
                    && !droneService.existsByHostAndPort(receivedDroneHost, receivedDronePort)) {
                    // Drone registration phase
                    final Drone newDrone = new Drone();
                    newDrone.setHost(receivedDroneHost);
                    newDrone.setPort(receivedDronePort);

                    final Drone savedDrone = droneService.addDrone(newDrone);
                    healthyDrones.addDrone(savedDrone.getId());

                    LOGGER.info("Registered drone: {}:{}, ID: {}.", receivedDroneHost, receivedDronePort, savedDrone.getId());
                } else if (receivedMessage.startsWith(MessageConstants.HEALTH_CHECK)){
                    // Health check case 1: RaspPi and Drone ok
                    // Add to healthy list
                    if (receivedMessage.startsWith(MessageConstants.HEALTH_CHECK_ALL_OK)) {
                        final Drone droneByHostAndPort = droneService.getByHostAndPort(receivedDroneHost, receivedDronePort);
                        healthyDrones.addDrone(droneByHostAndPort.getId());
                    }

                    // Health check case 2: RaspPi ok, Drone down
                    if (receivedMessage.startsWith(MessageConstants.HEALTH_CHECK_DRONE_OFFLINE)) {
                        // TODO: Add some flag? Keep it in healthy for now.
                        final Drone droneByHostAndPort = droneService.getByHostAndPort(receivedDroneHost, receivedDronePort);
                        healthyDrones.addDrone(droneByHostAndPort.getId());
                    }

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

package my.project.fer.ryzetello.ryzetellowebserver.udp;

import my.project.fer.ryzetello.ryzetellowebserver.endpoint.EndpointData;
import my.project.fer.ryzetello.ryzetellowebserver.config.UdpConfig;
import my.project.fer.ryzetello.ryzetellowebserver.constants.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.util.UUID;

@Component
public class UdpServerRunnable implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpServerRunnable.class);

    private byte[] receiveBuffer = new byte[1024];
    private byte[] sendBuffer = new byte[1024];

    private final UdpConfig udpConfig;
    private final EndpointData endpointData;

    @Autowired
    public UdpServerRunnable(UdpConfig udpConfig) {
        this.udpConfig = udpConfig;
        this.endpointData = EndpointData.getInstance();
    }

    @Override
    public void run() {
        try {
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                udpConfig.getServerSocket().receive(receivePacket);

                String receivedMessage = new String(receivePacket.getData()).replaceAll("\0", "");
                LOGGER.info("Server received message: " + receivedMessage);

                if (receivedMessage.startsWith(MessageConstants.REGISTER)) {
                    // Drone registration phase
                    endpointData.setId(UUID.randomUUID().toString());
                    endpointData.setIp(receivePacket.getAddress().getHostAddress());
                    endpointData.setPort(receivePacket.getPort());
                } else {
                    // TODO other cases if needed
                }

                // Buffer cleanup
                receiveBuffer = new byte[1024];
            }
        } catch (Exception e) {
            LOGGER.error("Socket error.");
        }

    }

}

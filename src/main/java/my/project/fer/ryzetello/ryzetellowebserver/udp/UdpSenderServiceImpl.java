package my.project.fer.ryzetello.ryzetellowebserver.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class UdpSenderServiceImpl implements UdpSenderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpSenderServiceImpl.class);

    private static final String IP_ADDRESS = "ip_address";
    private static final String PORT = "ip_port";

    private final UdpServer udpServer;

    @Autowired
    public UdpSenderServiceImpl(UdpServer udpServer) {
        this.udpServer = udpServer;
    }

    @Override
    public void sendMessage(String host, int port, String message) {
        InetAddress hostAddress;
        try {
            hostAddress = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            LOGGER.error("Couldn't send message to destination {}.", host);
            return;
        }

        byte[] messageByteData = message.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(messageByteData, messageByteData.length, hostAddress, port);

        try {
            DatagramSocket serverSocket = udpServer.getServerSocket();
            serverSocket.send(sendPacket);
            LOGGER.info("Sent message '{}' to {}:{}\n", message, host, port);
        } catch (IOException e) {
            LOGGER.error("Error while sending message.");
        }
    }

}

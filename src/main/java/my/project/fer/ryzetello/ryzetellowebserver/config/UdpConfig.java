package my.project.fer.ryzetello.ryzetellowebserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@Configuration
public class UdpConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpConfig.class);

    @Value("${udp.host}")
    private String host;

    @Value("${udp.port}")
    private Integer port;

    private DatagramSocket serverSocket;

    @Autowired
    public UdpConfig() throws SocketException, UnknownHostException {
        // TODO FIX
        this.host = "localhost";
        this.port = 5432;

        this.serverSocket = serverSocket();
    }

    public DatagramSocket serverSocket() throws UnknownHostException, SocketException {
        if (serverSocket == null) {
            serverSocket = new DatagramSocket(port, InetAddress.getByName(host));
            LOGGER.info("Created new server socket instance.");
        }

        return serverSocket;
    }

    public DatagramSocket getServerSocket() {
        return serverSocket;
    }

}

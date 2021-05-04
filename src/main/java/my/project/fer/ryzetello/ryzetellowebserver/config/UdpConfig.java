package my.project.fer.ryzetello.ryzetellowebserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

@Configuration
public class UdpConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpConfig.class);

    private String host;

    private Integer port;

    private DatagramSocket serverSocket;

    @Autowired
    public UdpConfig(@Value("${udp.host}") final String host, @Value("${udp.port}") final Integer port) throws SocketException, UnknownHostException {
        this.host = host;
        this.port = port;

        this.serverSocket = serverSocket();
    }

    public DatagramSocket serverSocket() throws SocketException {
        if (serverSocket == null) {
            serverSocket = new DatagramSocket(port);
            LOGGER.info("Created new server socket instance.");
        }

        return serverSocket;
    }

    public DatagramSocket getServerSocket() {
        return serverSocket;
    }

}

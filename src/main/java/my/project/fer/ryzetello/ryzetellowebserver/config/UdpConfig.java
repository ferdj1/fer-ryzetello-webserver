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

    private Integer videoPort;

    private DatagramSocket serverSocket;

    private DatagramSocket videoServerSocket;

    @Autowired
    public UdpConfig(@Value("${udp.host}") final String host, @Value("${udp.port}") final Integer port,
        @Value("${udp.videoPort}") final Integer videoPort) throws SocketException, UnknownHostException {
        this.host = host;
        this.port = port;
        this.videoPort = videoPort;

        this.serverSocket = serverSocket();
    }

    public DatagramSocket serverSocket() throws SocketException {
        if (serverSocket == null) {
            serverSocket = new DatagramSocket(port);
            LOGGER.info("Created new server socket instance.");
        }

        return serverSocket;
    }

    public DatagramSocket videoServerSocket() throws SocketException {
        if (videoServerSocket == null) {
            videoServerSocket = new DatagramSocket(videoPort);
            LOGGER.info("Created new video server socket instance.");
        }

        return videoServerSocket;
    }

    public DatagramSocket getServerSocket() {
        return serverSocket;
    }

    public DatagramSocket getVideoServerSocket() {
        return videoServerSocket;
    }

    public void setVideoServerSocket(DatagramSocket videoServerSocket) {
        this.videoServerSocket = videoServerSocket;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getVideoPort() {
        return videoPort;
    }

    public void setVideoPort(Integer videoPort) {
        this.videoPort = videoPort;
    }

}

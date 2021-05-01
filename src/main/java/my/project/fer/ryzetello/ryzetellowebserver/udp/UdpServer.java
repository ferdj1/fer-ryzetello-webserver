package my.project.fer.ryzetello.ryzetellowebserver.udp;

import my.project.fer.ryzetello.ryzetellowebserver.config.UdpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class UdpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpServer.class);

    private UdpConfig udpConfig;
    private final UdpServerRunnable udpServerRunnable;
    private ExecutorService executorService;

    private DatagramSocket serverSocket;

    @Autowired
    public UdpServer(UdpConfig udpConfig,
        UdpServerRunnable udpServerRunnable) throws SocketException, UnknownHostException {
        this.udpConfig = udpConfig;
        this.serverSocket = udpConfig.serverSocket();
        this.udpServerRunnable = udpServerRunnable;
        this.executorService = Executors.newFixedThreadPool(1);
    }

    @PostConstruct
    public void init() {
        executorService.submit(udpServerRunnable);
    }

    @PreDestroy
    public void clean() {
        udpConfig.getServerSocket().close();
        executorService.shutdown();
    }

    public DatagramSocket getServerSocket() {
        return serverSocket;
    }

}

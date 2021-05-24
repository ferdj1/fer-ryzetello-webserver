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
public class UdpVideoServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpVideoServer.class);

    private UdpConfig udpConfig;
    private final UdpVideoServerRunnable udpVideoServerRunnable;
    private ExecutorService executorService;

    private DatagramSocket videoServerSocket;

    @Autowired
    public UdpVideoServer(UdpConfig udpConfig, UdpVideoServerRunnable udpVideoServerRunnable) throws SocketException, UnknownHostException {
        this.udpConfig = udpConfig;
        this.videoServerSocket = udpConfig.videoServerSocket();
        this.udpVideoServerRunnable = udpVideoServerRunnable;
        this.executorService = Executors.newFixedThreadPool(1);
    }

    @PostConstruct
    public void init() {
        executorService.submit(udpVideoServerRunnable);
    }

    @PreDestroy
    public void clean() {
        udpConfig.getServerSocket().close();
        executorService.shutdown();
    }

    public DatagramSocket getVideoServerSocket() {
        return videoServerSocket;
    }

}

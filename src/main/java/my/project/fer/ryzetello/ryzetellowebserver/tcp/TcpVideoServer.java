package my.project.fer.ryzetello.ryzetellowebserver.tcp;

import my.project.fer.ryzetello.ryzetellowebserver.config.TcpConfig;
import my.project.fer.ryzetello.ryzetellowebserver.config.UdpConfig;
import my.project.fer.ryzetello.ryzetellowebserver.constants.MessageConstants;
import my.project.fer.ryzetello.ryzetellowebserver.model.Drone;
import my.project.fer.ryzetello.ryzetellowebserver.udp.UdpVideoServerRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TcpVideoServer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpVideoServer.class);

    private static final int ALLOWED_CLIENTS_COUNT = 10;

    private TcpConfig tcpConfig;
    private ServerSocket videoServerSocket;
    private ExecutorService videoExecutorService;
    private ExecutorService videoServerExecutorService;


    @Autowired
    public TcpVideoServer(TcpConfig tcpConfig) throws IOException {
        this.tcpConfig = tcpConfig;
        this.videoServerSocket = new ServerSocket(tcpConfig.getVideoPort());
        this.videoExecutorService = Executors.newFixedThreadPool(ALLOWED_CLIENTS_COUNT);
        this.videoServerExecutorService = Executors.newFixedThreadPool(1);

        videoServerExecutorService.submit(this);
    }

    @Override
    public void run() {
        while (true) {
            Socket acceptedVideoClientSocket;
            try {
                acceptedVideoClientSocket = videoServerSocket.accept();

                videoExecutorService.submit(new TcpVideoServer.TcpVideoClientHandler(acceptedVideoClientSocket));
            } catch (IOException e) {
                LOGGER.error("Socket error.", e);
            }
        }

    }

    private class TcpVideoClientHandler implements Runnable {

        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public TcpVideoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String receivedMessage;
                while ((receivedMessage = in.readLine()) != null) {
                }

                clientSocket.close();

            } catch (Exception e) {
                LOGGER.error("Socket error.", e);
            }
        }

    }

}

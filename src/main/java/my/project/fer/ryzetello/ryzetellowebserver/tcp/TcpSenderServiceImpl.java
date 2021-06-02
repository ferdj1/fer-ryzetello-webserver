package my.project.fer.ryzetello.ryzetellowebserver.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

@Service
public class TcpSenderServiceImpl implements TcpSenderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpSenderServiceImpl.class);

    private final TcpCommandServer tcpCommandServer;

    @Autowired
    public TcpSenderServiceImpl(TcpCommandServer tcpCommandServer) {
        this.tcpCommandServer = tcpCommandServer;
    }

    @Override
    public void sendMessage(String host, int port, String message) {
        Socket clientSocket = tcpCommandServer.getClientSocketForHostAndPort(host, port);

        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(message);

            LOGGER.info("Sent message '{}' to {}:{}\n", message, host, port);
        } catch (IOException e) {
            LOGGER.error("Error while sending message.");
        }
    }

}

package my.project.fer.ryzetello.ryzetellowebserver.tcp;

public interface TcpSenderService {

    void sendMessage(String host, int port, String message);

}

package my.project.fer.ryzetello.ryzetellowebserver.udp;

public interface UdpSenderService {

    void sendMessage(String host, int port, String message);

}

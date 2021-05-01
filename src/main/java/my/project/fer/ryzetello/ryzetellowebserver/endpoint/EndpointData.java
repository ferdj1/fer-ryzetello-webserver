package my.project.fer.ryzetello.ryzetellowebserver.endpoint;

import org.springframework.stereotype.Component;

import java.util.UUID;

public class EndpointData {

    private static EndpointData INSTANCE;

    private String id;
    private String ip;
    private int port;

    public static EndpointData getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EndpointData();
        }

        return INSTANCE;
    }

    private EndpointData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}

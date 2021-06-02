package my.project.fer.ryzetello.ryzetellowebserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TcpConfig {

    private String host;

    private Integer port;

    private Integer videoPort;

    @Autowired
    public TcpConfig(@Value("${tcp.host}") final String host, @Value("${tcp.port}") final Integer port,
        @Value("${tcp.videoPort}") final Integer videoPort) {
        this.host = host;
        this.port = port;
        this.videoPort = videoPort;
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

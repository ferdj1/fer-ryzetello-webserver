package my.project.fer.ryzetello.ryzetellowebserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsmpegConfig {

    private String nodePath;

    private String webSocketRelayPath;

    @Autowired
    public JsmpegConfig(@Value("${node.nodePath}") final String nodePath, @Value("${jsmpeg.webSocketRelayPath}") final String webSocketRelayPath) {
        this.nodePath = nodePath;
        this.webSocketRelayPath = webSocketRelayPath;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public String getWebSocketRelayPath() {
        return webSocketRelayPath;
    }

    public void setWebSocketRelayPath(String webSocketRelayPath) {
        this.webSocketRelayPath = webSocketRelayPath;
    }

}

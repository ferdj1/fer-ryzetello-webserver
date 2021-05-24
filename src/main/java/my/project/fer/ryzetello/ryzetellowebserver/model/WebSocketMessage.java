package my.project.fer.ryzetello.ryzetellowebserver.model;

public class WebSocketMessage<T> {

    private WebSocketMessageType type;
    private T data;

    public WebSocketMessage() {
    }

    public WebSocketMessage(WebSocketMessageType type, T data) {
        this.type = type;
        this.data = data;
    }

    public WebSocketMessageType getType() {
        return type;
    }

    public void setType(WebSocketMessageType type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}

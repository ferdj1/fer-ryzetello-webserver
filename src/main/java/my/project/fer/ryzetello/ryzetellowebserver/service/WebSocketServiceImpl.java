package my.project.fer.ryzetello.ryzetellowebserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.fer.ryzetello.ryzetellowebserver.model.WebSocketMessage;
import my.project.fer.ryzetello.ryzetellowebserver.model.WebSocketMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    private SimpMessagingTemplate template;
    private ObjectMapper objectMapper;

    @Autowired
    public WebSocketServiceImpl(final SimpMessagingTemplate template, final ObjectMapper objectMapper) {
        this.template = template;
        this.objectMapper = objectMapper;
    }

    @Override
    public void notifyDronesAdded(List<UUID> addedDroneIds) {
        try {
            final WebSocketMessage<List<UUID>> webSocketMessage = new WebSocketMessage<>();
            webSocketMessage.setType(WebSocketMessageType.DRONES_ADDED);
            webSocketMessage.setData(addedDroneIds);

            String webSocketMessageJson = objectMapper.writeValueAsString(webSocketMessage);
            template.convertAndSend("/queue/drones", webSocketMessageJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyDronesRemoved(List<UUID> removedDroneIds) {
        try {
            final WebSocketMessage<List<UUID>> webSocketMessage = new WebSocketMessage<>();
            webSocketMessage.setType(WebSocketMessageType.DRONES_REMOVED);
            webSocketMessage.setData(removedDroneIds);

            String webSocketMessageJson = objectMapper.writeValueAsString(webSocketMessage);
            template.convertAndSend("/queue/drones", webSocketMessageJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}

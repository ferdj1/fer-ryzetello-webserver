package my.project.fer.ryzetello.ryzetellowebserver.service;

import java.util.List;
import java.util.UUID;

public interface WebSocketService {

    void notifyDronesAdded(List<UUID> addedDroneIds);

    void notifyDronesRemoved(List<UUID> removedDroneIds);

}

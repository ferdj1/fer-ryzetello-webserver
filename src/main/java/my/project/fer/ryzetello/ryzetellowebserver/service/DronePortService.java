package my.project.fer.ryzetello.ryzetellowebserver.service;

import java.util.UUID;

public interface DronePortService {

    Integer getVideoPort(UUID droneId);

    Integer getFfmpegPort(UUID droneId);

    Integer getHttpStreamPort(UUID droneId);

    Integer getWebSocketPort(UUID droneId);

}

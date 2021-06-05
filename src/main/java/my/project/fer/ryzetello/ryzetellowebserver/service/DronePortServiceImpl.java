package my.project.fer.ryzetello.ryzetellowebserver.service;

import my.project.fer.ryzetello.ryzetellowebserver.state.VideoClientsState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DronePortServiceImpl implements DronePortService {

    private VideoClientsState videoClientsState;

    @Autowired
    public DronePortServiceImpl(VideoClientsState videoClientsState) {
        this.videoClientsState = videoClientsState;
    }

    @Override
    public Integer getVideoPort(UUID droneId) {
        return videoClientsState.getVideoPort(droneId);
    }

    @Override
    public Integer getFfmpegPort(UUID droneId) {
        return videoClientsState.getFfmpegPort(droneId);
    }

    @Override
    public Integer getHttpStreamPort(UUID droneId) {
        return videoClientsState.getHttpStreamPort(droneId);
    }

    @Override
    public Integer getWebSocketPort(UUID droneId) {
        return videoClientsState.getWebSocketPort(droneId);
    }

}

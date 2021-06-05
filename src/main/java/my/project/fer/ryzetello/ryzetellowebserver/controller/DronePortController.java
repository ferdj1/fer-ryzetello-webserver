package my.project.fer.ryzetello.ryzetellowebserver.controller;

import my.project.fer.ryzetello.ryzetellowebserver.service.DronePortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/dronePorts")
public class DronePortController {

    private final DronePortService dronePortService;

    @Autowired
    public DronePortController(DronePortService dronePortService) {
        this.dronePortService = dronePortService;
    }

    @GetMapping(value = "/videoPort/{droneId}")
    public Integer getVideoPort(@PathVariable String droneId) {
        return dronePortService.getVideoPort(UUID.fromString(droneId));
    }

    @GetMapping(value = "/ffmpegPort/{droneId}")
    public Integer getFfmpegPort(@PathVariable String droneId) {
        return dronePortService.getFfmpegPort(UUID.fromString(droneId));
    }

    @GetMapping(value = "/httpStreamPort/{droneId}")
    public Integer getHttpStreamPort(@PathVariable String droneId) {
        return dronePortService.getHttpStreamPort(UUID.fromString(droneId));
    }

    @GetMapping(value = "/webSocketPort/{droneId}")
    public Integer getWebSocketPort(@PathVariable String droneId) {
        return dronePortService.getWebSocketPort(UUID.fromString(droneId));
    }

}

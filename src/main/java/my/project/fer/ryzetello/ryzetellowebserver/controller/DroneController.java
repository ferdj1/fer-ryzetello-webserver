package my.project.fer.ryzetello.ryzetellowebserver.controller;

import my.project.fer.ryzetello.ryzetellowebserver.model.Drone;
import my.project.fer.ryzetello.ryzetellowebserver.service.DroneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/drones")
public class DroneController {

    private final DroneService droneService;

    @Autowired
    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    @GetMapping
    public List<Drone> getAllDrones() {
        return droneService.getAll();
    }

    @GetMapping("/ids")
    public List<UUID> getAllDroneIds() {
        return droneService.getAll().stream().map(Drone::getId).collect(Collectors.toList());
    }

}

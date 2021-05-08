package my.project.fer.ryzetello.ryzetellowebserver.service;

import my.project.fer.ryzetello.ryzetellowebserver.model.Drone;

import java.util.List;
import java.util.UUID;

public interface DroneService {

    List<Drone> getAll();

    Drone getDrone(UUID id);

    Drone getByHostAndPort(String host, Integer port);

    boolean existsById(UUID id);

    boolean existsByHostAndPort(String host, Integer port);

    Drone addDrone(Drone drone);

    Drone updateDrone(Drone drone);

    void deleteDrone(UUID id);

}

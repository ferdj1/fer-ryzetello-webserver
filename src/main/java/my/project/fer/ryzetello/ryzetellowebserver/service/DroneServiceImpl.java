package my.project.fer.ryzetello.ryzetellowebserver.service;

import my.project.fer.ryzetello.ryzetellowebserver.exception.EntryNotFoundException;
import my.project.fer.ryzetello.ryzetellowebserver.model.Drone;
import my.project.fer.ryzetello.ryzetellowebserver.repository.DroneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DroneServiceImpl implements DroneService {
    private final DroneRepository droneRepository;

    @Autowired
    public DroneServiceImpl(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    @Override
    public List<Drone> getAll() {
        return droneRepository.findAll();
    }

    @Override
    public Drone getDrone(UUID id) {
        final Optional<Drone> droneOptional = droneRepository.findById(id);

        if (!droneOptional.isPresent()) {
            throw new EntryNotFoundException("Drone not found.");
        }

        return droneOptional.get();
    }

    @Override
    public Drone getByHostAndPort(String host, Integer port) {
        final Optional<Drone> droneOptional = droneRepository.findFirstByHostAndPort(host, port);

        if (!droneOptional.isPresent()) {
            throw new EntryNotFoundException("Drone not found.");
        }

        return droneOptional.get();
    }

    @Override
    public boolean existsById(UUID id) {
        return droneRepository.existsById(id);
    }

    @Override
    public boolean existsByHostAndPort(String host, Integer port) {
        return droneRepository.existsByHostAndPort(host, port);
    }

    @Override
    public Drone addDrone(Drone drone) {
        return droneRepository.save(drone);
    }

    @Override
    public Drone updateDrone(Drone drone) {
        final UUID id = drone.getId();

        if (!droneRepository.existsById(id)) {
            throw new EntryNotFoundException("Drone not found.");
        }

        return droneRepository.save(drone);
    }

    @Override
    public void deleteDrone(UUID id) {
        droneRepository.deleteById(id);
    }

}

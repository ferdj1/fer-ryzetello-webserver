package my.project.fer.ryzetello.ryzetellowebserver.repository;

import my.project.fer.ryzetello.ryzetellowebserver.model.Drone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DroneRepository extends JpaRepository<Drone, UUID> {

    Optional<Drone> findFirstByHostAndPort(String host, Integer port);

    boolean existsByHostAndPort(String host, Integer port);

}

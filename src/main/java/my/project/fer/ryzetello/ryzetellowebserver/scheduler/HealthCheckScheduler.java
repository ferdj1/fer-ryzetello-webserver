package my.project.fer.ryzetello.ryzetellowebserver.scheduler;

import my.project.fer.ryzetello.ryzetellowebserver.constants.MessageConstants;
import my.project.fer.ryzetello.ryzetellowebserver.model.Drone;
import my.project.fer.ryzetello.ryzetellowebserver.service.DroneService;
import my.project.fer.ryzetello.ryzetellowebserver.service.WebSocketService;
import my.project.fer.ryzetello.ryzetellowebserver.state.HealthyDrones;
import my.project.fer.ryzetello.ryzetellowebserver.udp.UdpSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class HealthCheckScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckScheduler.class);

    private final DroneService droneService;
    private final UdpSenderService udpSenderService;
    private WebSocketService webSocketService;
    private final HealthyDrones healthyDrones;

    @Autowired
    public HealthCheckScheduler(DroneService droneService, UdpSenderService udpSenderService, WebSocketService webSocketService, HealthyDrones healthyDrones) {
        this.droneService = droneService;
        this.udpSenderService = udpSenderService;
        this.webSocketService = webSocketService;
        this.healthyDrones = healthyDrones;
    }

    @Scheduled(fixedRate = 5000)
    public void healthCheck() {
        cleanup();

        LOGGER.info("Health check:: Reseting healthy drones.");
        healthyDrones.reset();

        final List<Drone> drones = droneService.getAll();

        drones.forEach(drone -> {
            udpSenderService.sendMessage(drone.getHost(), drone.getPort(), MessageConstants.HEALTH_CHECK);
        });
    }

    // Remove dead clients
    private void cleanup() {
        LOGGER.info("Health check:: Cleanup");
        final List<UUID> allDroneIds = droneService.getAll().stream().map(Drone::getId).collect(Collectors.toList());

        int cleanedUpCount = 0;

        List<UUID> deletedDrones = new ArrayList<>();
        for (UUID id : allDroneIds) {
            if (!healthyDrones.getHealthyDroneIds().contains(id)) {
                droneService.deleteDrone(id);
                cleanedUpCount++;
                deletedDrones.add(id);
            }
        }

        // WS
        if (!deletedDrones.isEmpty()) {
            webSocketService.notifyDronesRemoved(deletedDrones);
        }
        //

        LOGGER.info("Health check:: Cleaned up {} dead drones.", cleanedUpCount);
    }
}

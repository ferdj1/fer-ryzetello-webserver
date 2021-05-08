package my.project.fer.ryzetello.ryzetellowebserver.state;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class HealthyDrones {

    private List<UUID> healthyDroneIds;

    public HealthyDrones() {
        healthyDroneIds = new ArrayList<>();
    }

    public List<UUID> getHealthyDroneIds() {
        return healthyDroneIds;
    }

    public void addDrone(UUID id) {
        this.healthyDroneIds.add(id);
    }

    public void removeDrone(UUID id) {
        this.healthyDroneIds.remove(id);
    }

    public void reset() {
        healthyDroneIds = new ArrayList<>();
    }

}

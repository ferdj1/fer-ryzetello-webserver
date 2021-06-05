package my.project.fer.ryzetello.ryzetellowebserver.state;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Component
public class VideoClientExecutorsState {

    private Map<UUID, ExecutorService> videoExecutorServiceMap;

    public VideoClientExecutorsState() {
        this.videoExecutorServiceMap = new LinkedHashMap<>();
    }


    public ExecutorService getForId(UUID droneId) {
        return videoExecutorServiceMap.get(droneId);
    }

    public void add(UUID droneId, ExecutorService executorService) {
        videoExecutorServiceMap.put(droneId, executorService);
    }

}

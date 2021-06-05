package my.project.fer.ryzetello.ryzetellowebserver.state;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class VideoClientsState {

    private static final int MIN_PORT = 20000;
    private static final int MAX_PORT = 21000;
    private static final int FFMPEG_PORT_SHIFT = 1001;
    private static final int HTTP_STREAM_PORT_SHIFT = 2001;
    private static final int WEB_SOCKET_PORT_SHIFT = 3001;

    private Map<UUID, Integer> droneVideoPortMap;
    private Map<UUID, Integer> droneVideoFfmpegPortMap;
    private Map<UUID, Integer> droneHttpStreamPortMap;
    private Map<UUID, Integer> droneWebSocketPortMap;

    public VideoClientsState() {
        this.droneVideoPortMap = new LinkedHashMap<>();
        this.droneVideoFfmpegPortMap = new LinkedHashMap<>();
        this.droneHttpStreamPortMap = new LinkedHashMap<>();
        this.droneWebSocketPortMap = new LinkedHashMap<>();
    }


    public void setVideoAndFffmpegPortsForDroneId(UUID droneId) {
        setVideoPort(droneId);
        setFfmpegPort(droneId);
        setHttpStreamPort(droneId);
        setWebSocketPort(droneId);
    }

    public void removeAllPortsForDroneId(UUID droneId) {
        removeDroneVideoPort(droneId);
        removeFfmpegPort(droneId);
        removeHttpStreamPort(droneId);
        removeWebSocketPort(droneId);
    }

    public Integer getVideoPort(UUID droneId) {
        return droneVideoPortMap.get(droneId);
    }

    public Integer getFfmpegPort(UUID droneId) {
        return droneVideoFfmpegPortMap.get(droneId);
    }

    public Integer getHttpStreamPort(UUID droneId) {
        return droneHttpStreamPortMap.get(droneId);
    }

    public Integer getWebSocketPort(UUID droneId) {
        return droneWebSocketPortMap.get(droneId);
    }

    private int setVideoPort(UUID droneId) {
        int randomPort = ThreadLocalRandom.current().nextInt(MIN_PORT, MAX_PORT + 1);

        while (droneVideoPortMap.containsValue(randomPort)) {
            randomPort = ThreadLocalRandom.current().nextInt(MIN_PORT, MAX_PORT + 1);
        }

        droneVideoPortMap.put(droneId, randomPort);

        return randomPort;
    }

    private void removeDroneVideoPort(UUID droneId) {
        droneVideoPortMap.remove(droneId);
    }

    private int setFfmpegPort(UUID droneId) {
        if (!droneVideoPortMap.containsKey(droneId)) {
            // Needs to generate drone video port before ffmpeg port
            return -1;
        }

        int videoPort = getVideoPort(droneId);
        int ffmpegPort = videoPort + FFMPEG_PORT_SHIFT;
        droneVideoFfmpegPortMap.put(droneId, ffmpegPort);

        return ffmpegPort;
    }

    private void removeFfmpegPort(UUID droneId) {
        droneVideoFfmpegPortMap.remove(droneId);
    }


    private int setHttpStreamPort(UUID droneId) {
        if (!droneVideoPortMap.containsKey(droneId)) {
            // Needs to generate drone video port before HttpStream port
            return -1;
        }

        int videoPort = getVideoPort(droneId);
        int httpStreamPort = videoPort + HTTP_STREAM_PORT_SHIFT;
        droneHttpStreamPortMap.put(droneId, httpStreamPort);

        return httpStreamPort;
    }

    private void removeHttpStreamPort(UUID droneId) {
        droneHttpStreamPortMap.remove(droneId);
    }

    private int setWebSocketPort(UUID droneId) {
        if (!droneVideoPortMap.containsKey(droneId)) {
            // Needs to generate drone video port before WebSocket port
            return -1;
        }

        int videoPort = getVideoPort(droneId);
        int webSocketPort = videoPort + WEB_SOCKET_PORT_SHIFT;
        droneWebSocketPortMap.put(droneId, webSocketPort);

        return webSocketPort;
    }

    private void removeWebSocketPort(UUID droneId) {
        droneWebSocketPortMap.remove(droneId);
    }

}

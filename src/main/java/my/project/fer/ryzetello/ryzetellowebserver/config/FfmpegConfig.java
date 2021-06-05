package my.project.fer.ryzetello.ryzetellowebserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FfmpegConfig {

    private String ffmpegPath;

    private String ffprobePath;

    @Autowired
    public FfmpegConfig(@Value("${ffmpeg.ffmpegPath}") final String ffmpegPath,
        @Value("${ffmpeg.ffprobePath}") final String ffprobePath) {
        this.ffmpegPath = ffmpegPath;
        this.ffprobePath = ffprobePath;
    }

    public String getFfmpegPath() {
        return ffmpegPath;
    }

    public void setFfmpegPath(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    public String getFfprobePath() {
        return ffprobePath;
    }

    public void setFfprobePath(String ffprobePath) {
        this.ffprobePath = ffprobePath;
    }

}

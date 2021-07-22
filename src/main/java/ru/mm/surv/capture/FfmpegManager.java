package ru.mm.surv.capture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.mm.surv.capture.config.FfmpegConfig;
import ru.mm.surv.config.Users;

import javax.annotation.PreDestroy;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public class FfmpegManager {
    
    private final Map<String, Ffmpeg> recorders = new HashMap<>();

    @Autowired
    public FfmpegManager(
            @Value("${ffmpeg.hls.folder}") Path folder,
            @Value("${ffmpeg.publisher}") String publisher,
            Users users,
            FfmpegConfig config) {
        var publishUser = users.getUsers().get(publisher);
        config.getRecorder().forEach((k, v) -> {
            recorders.put(k, new Ffmpeg(k, v, folder, publishUser));
        });
    }

    public void start() {
        recorders.forEach((s, ffmpeg) -> ffmpeg.start());
    }

    @PreDestroy
    public void stop() {
        recorders.forEach((s, ffmpeg) -> ffmpeg.stop());
    }
}

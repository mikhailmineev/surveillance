package ru.mm.surv.capture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.mm.surv.capture.config.FfmpegConfig;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public class FfmpegManager {
    
    private final Map<String, Ffmpeg> recorders = new HashMap<>();

    @Autowired
    public FfmpegManager(@Value("${ffmpeg.hls.folder}") Path folder, FfmpegConfig config) {
        config.getRecorder().forEach((k, v) -> {
            recorders.put(k, new Ffmpeg(k, v.getSelector(), folder));
        });
    }

    public void start() {
        recorders.forEach((s, ffmpeg) -> ffmpeg.start());
    }
}

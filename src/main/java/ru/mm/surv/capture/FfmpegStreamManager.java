package ru.mm.surv.capture;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.mm.surv.capture.config.Platform;
import ru.mm.surv.capture.repository.WebcamRepository;
import ru.mm.surv.capture.service.FfmpegInstaller;
import ru.mm.surv.config.Users;

import javax.annotation.PreDestroy;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FfmpegStreamManager {

    private final Map<String, FfmpegStream> recorders = new HashMap<>();

    @Autowired
    public FfmpegStreamManager(
            @Value("${ffmpeg.hls.folder}") Path hlsStreamFolder,
            @Value("${ffmpeg.publisher}") String publisher,
            Users users,
            WebcamRepository webcamRepository,
            FfmpegInstaller ffmpegInstaller) {
        var ffmpeg = ffmpegInstaller.getPath();
        var publishUser = users.getUsers().get(publisher);
        webcamRepository.getAll().forEach((v) -> {
            recorders.put(v.getName(), new FfmpegStream(Platform.getCurrent(), ffmpeg, v, hlsStreamFolder, publishUser));
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

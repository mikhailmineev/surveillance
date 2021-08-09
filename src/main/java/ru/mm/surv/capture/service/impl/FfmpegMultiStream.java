package ru.mm.surv.capture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.mm.surv.capture.config.Platform;
import ru.mm.surv.capture.repository.WebcamRepository;
import ru.mm.surv.capture.service.FfmpegInstaller;
import ru.mm.surv.capture.service.FfmpegStream;
import ru.mm.surv.config.Users;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FfmpegMultiStream implements FfmpegStream {

    private final Map<String, FfmpegSingleStream> recorders = new HashMap<>();

    private boolean isActive = false;

    @Autowired
    public FfmpegMultiStream(
            @Value("${ffmpeg.hls.folder}") Path hlsStreamFolder,
            @Value("${ffmpeg.mp4.folder}") Path recordsFolder,
            @Value("${ffmpeg.publisher}") String publisher,
            Users users,
            WebcamRepository webcamRepository,
            FfmpegInstaller ffmpegInstaller) {
        var publishUser = users.getUsers().get(publisher);
        webcamRepository.getAll().forEach((v) -> {
            recorders.put(v.getName(), new FfmpegSingleStream(Platform.getCurrent(), ffmpegInstaller, v, hlsStreamFolder, recordsFolder, publishUser));
        });
    }

    @Override
    public synchronized void start() {
        recorders.forEach((s, ffmpeg) -> ffmpeg.start());
        isActive = true;
    }

    @Override
    public synchronized void stop() {
        recorders.forEach((s, ffmpeg) -> ffmpeg.stop());
        isActive = false;
    }

    @Override
    public synchronized boolean isActive() {
        return isActive;
    }

    @Override
    public synchronized Collection<String> getStreamNames() {
        return recorders.keySet();
    }
}

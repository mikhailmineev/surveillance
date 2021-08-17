package ru.mm.surv.capture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.mm.surv.capture.config.FolderConfig;
import ru.mm.surv.capture.config.Platform;
import ru.mm.surv.capture.repository.WebcamRepository;
import ru.mm.surv.capture.service.FfmpegInstaller;
import ru.mm.surv.capture.service.FfmpegStream;
import ru.mm.surv.config.Users;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FfmpegMultiStream implements FfmpegStream {

    private final Map<String, FfmpegSingleStream> recorders = new HashMap<>();

    @Autowired
    public FfmpegMultiStream(
            FolderConfig folders,
            @Value("${ffmpeg.publisher}") String publisher,
            Users users,
            WebcamRepository webcamRepository,
            FfmpegInstaller ffmpegInstaller) {
        var publishUser = users.getUsers().get(publisher);
        webcamRepository.getAll().forEach((v) -> {
            recorders.put(v.getName(), new FfmpegSingleStream(Platform.getCurrent(), ffmpegInstaller, v, folders, publishUser, this::remove));
        });
    }

    private synchronized void remove(FfmpegSingleStream stream) {
        recorders.remove(stream.getName());
    }

    @Override
    public synchronized void start() {
        recorders.forEach((s, ffmpeg) -> ffmpeg.start());
    }

    @Override
    public synchronized void stop() {
        recorders.forEach((s, ffmpeg) -> ffmpeg.stop());
    }

    @Override
    public synchronized boolean isActive() {
        return recorders.values().stream().anyMatch(FfmpegSingleStream::isActive);
    }

    @Override
    public synchronized Collection<String> getStreamNames() {
        return recorders.keySet();
    }

    @Override
    public synchronized Optional<File> getThumb(String stream) {
        return Optional.ofNullable(recorders.get(stream)).flatMap(FfmpegSingleStream::getThumb);
    }
}

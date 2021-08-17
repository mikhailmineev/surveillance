package ru.mm.surv.capture.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.mm.surv.capture.service.FfmpegStream;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@Primary
@Slf4j
public class FfmpegRestartableStream implements FfmpegStream {

    private final ObjectFactory<FfmpegMultiStream> ffmpegMultiStreamFactory;
    private FfmpegMultiStream ffmpegMultiStream;

    @Autowired
    public FfmpegRestartableStream(ObjectFactory<FfmpegMultiStream> ffmpegMultiStreamFactory) {
        this.ffmpegMultiStreamFactory = ffmpegMultiStreamFactory;
    }

    @Override
    public synchronized void start() {
        if (!isActive()) {
            ffmpegMultiStream = ffmpegMultiStreamFactory.getObject();
        }
        ffmpegMultiStream.start();
    }

    @Override
    @PreDestroy
    public synchronized void stop() {
        if (isActive()) {
            ffmpegMultiStream.stop();
            ffmpegMultiStream = null;
        }
    }

    @Override
    public boolean isActive() {
        if (ffmpegMultiStream != null && !ffmpegMultiStream.isActive()) {
            ffmpegMultiStream = null;
        }
        return ffmpegMultiStream != null;
    }

    @Override
    public Collection<String> getStreamNames() {
        return Optional
                .ofNullable(ffmpegMultiStream)
                .map(FfmpegStream::getStreamNames)
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<File> getThumb(String stream) {
        return Optional
                .ofNullable(ffmpegMultiStream)
                .flatMap(e -> e.getThumb(stream));
    }
}

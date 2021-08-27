package ru.mm.surv.capture.service;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

public interface FfmpegStream {

    void start();

    void stop();

    boolean isActive();

    Collection<String> getStreamNames();

    Optional<File> getThumb(String stream);
}

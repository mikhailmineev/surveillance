package ru.mm.surv.capture.service;

import java.util.Collection;

public interface FfmpegStream {

    void start();

    void stop();

    boolean isActive();

    Collection<String> getStreamNames();
}

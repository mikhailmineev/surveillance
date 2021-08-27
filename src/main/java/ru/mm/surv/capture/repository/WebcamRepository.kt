package ru.mm.surv.capture.repository;

import ru.mm.surv.capture.config.CameraConfig;

import java.util.Collection;

public interface WebcamRepository {

    Collection<CameraConfig> getAll();
}

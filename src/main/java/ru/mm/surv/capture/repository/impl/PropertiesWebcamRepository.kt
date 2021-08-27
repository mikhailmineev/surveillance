package ru.mm.surv.capture.repository.impl;

import org.springframework.stereotype.Repository;
import ru.mm.surv.capture.config.CameraConfig;
import ru.mm.surv.capture.config.FfmpegConfig;
import ru.mm.surv.capture.repository.WebcamRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Repository
public class PropertiesWebcamRepository implements WebcamRepository {
    private FfmpegConfig config;

    public PropertiesWebcamRepository(FfmpegConfig config) {
        this.config = config;
    }

    @Override
    public Collection<CameraConfig> getAll() {
        return Optional
                .ofNullable(config)
                .map(FfmpegConfig::getRecorder)
                .map(Map::values)
                .orElse(Collections.emptyList());
    }
}

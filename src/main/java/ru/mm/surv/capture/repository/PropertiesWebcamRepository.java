package ru.mm.surv.capture.repository;

import org.springframework.stereotype.Repository;
import ru.mm.surv.capture.config.CameraConfig;
import ru.mm.surv.capture.config.FfmpegConfig;

import java.util.Collection;

@Repository
public class PropertiesWebcamRepository implements WebcamRepository {
    private FfmpegConfig config;

    public PropertiesWebcamRepository(FfmpegConfig config) {
        this.config = config;
    }

    @Override
    public Collection<CameraConfig> getAll() {
        return config.getRecorder().values();
    }
}

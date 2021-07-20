package ru.mm.surv.capture.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "ffmpeg")
public class FfmpegConfig {

    private Map<String, CameraConfig> recorder;
}

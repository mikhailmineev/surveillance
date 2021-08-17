package ru.mm.surv.capture.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Data
@Configuration
@ConfigurationProperties(prefix = "ffmpeg.folder")
public class FolderConfig {

    private Path StreamThumb;

    private Path hls;

    private Path mp4;

    private Path mp4Thumb;
}

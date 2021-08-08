package ru.mm.surv.capture.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import ru.mm.surv.capture.config.Platform;
import ru.mm.surv.capture.service.FfmpegInstaller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class FfmpegInstallerImpl implements FfmpegInstaller {

    public static final String FFMPEG_RESOURCES_PATH = "classpath:/bin/ffmpeg";

    private final Path executableFolder;

    private final Path ffmpeg;

    @Autowired
    public FfmpegInstallerImpl(@Value("${ffmpeg.folder}") Path executableFolder) {
        this.executableFolder = executableFolder;
        this.ffmpeg = installExecutable();
    }

    @Override
    public Path getPath() {
        return ffmpeg;
    }

    private Path installExecutable() {
        Platform platform = Platform.getCurrent();

        Resource ffmpegResource;
        try {
            log.info("Ffmpeg not installed, installing new");
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources( FFMPEG_RESOURCES_PATH + "/" + platform.getName() + "/*");
            if (resources.length != 1) {
                throw new RuntimeException("Failed to locate ffmpeg in distribution path");
            }
            ffmpegResource = resources[0];
        } catch (IOException e) {
            throw new RuntimeException("Failed to locate ffmpeg in distribution path", e);
        }

        String ffmpegBinName = ffmpegResource.getFilename();
        Path target = executableFolder.resolve(ffmpegBinName);
        if (Files.exists(target)) {
            return target;
        }

        try {
            Files.createDirectories(executableFolder);
            try (InputStream inputStream = ffmpegResource.getInputStream();
                OutputStream outputStream = new FileOutputStream(target.toFile())) {
                inputStream.transferTo(outputStream);
            }
            platform.getFfmpegPostInstall().accept(target);
        } catch (IOException e) {
            throw new RuntimeException("Failed to install ffmpeg", e);
        }

        return target;
    }
}

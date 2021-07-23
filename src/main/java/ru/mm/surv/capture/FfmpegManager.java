package ru.mm.surv.capture;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.mm.surv.capture.config.FfmpegConfig;
import ru.mm.surv.config.Users;

import javax.annotation.PreDestroy;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FfmpegManager {

    public static final String FFMPEG_RESOURCES_PATH = "bin";
    public static final String FFMPEG_EXE = "ffmpeg.exe";
    public static final String FFMPEG_WIN = "/" + FFMPEG_RESOURCES_PATH + "/" + FFMPEG_EXE;
    private final Map<String, Ffmpeg> recorders = new HashMap<>();

    @Autowired
    public FfmpegManager(
            @Value("${ffmpeg.folder}") Path executableFolder,
            @Value("${ffmpeg.hls.folder}") Path hlsStreamFolder,
            @Value("${ffmpeg.publisher}") String publisher,
            Users users,
            FfmpegConfig config) {
        Path ffmpeg = installExecutable(executableFolder);
        var publishUser = users.getUsers().get(publisher);
        config.getRecorder().forEach((k, v) -> {
            recorders.put(k, new Ffmpeg(ffmpeg, k, v, hlsStreamFolder, publishUser));
        });
    }

    private Path installExecutable(Path executableFolder) {
        if (!SystemUtils.IS_OS_WINDOWS) {
            throw new RuntimeException("Only Windows OS supported");
        }
        Path target = executableFolder.resolve(FFMPEG_EXE);
        if (Files.exists(target)) {
            return target;
        }
        log.info("Ffmpeg not installed, installing new");
        URL binFile =  getClass().getResource(FFMPEG_WIN);
        if (binFile == null) {
            throw new RuntimeException("Failed to locate ffmpeg in distribution");
        }
        try {
            Files.createDirectories(executableFolder);
            try (InputStream inputStream = binFile.openStream();
                OutputStream outputStream = new FileOutputStream(target.toFile())) {
                inputStream.transferTo(outputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to install ffmpeg", e);
        }
        return target;
    }

    public void start() {
        recorders.forEach((s, ffmpeg) -> ffmpeg.start());
    }

    @PreDestroy
    public void stop() {
        recorders.forEach((s, ffmpeg) -> ffmpeg.stop());
    }
}

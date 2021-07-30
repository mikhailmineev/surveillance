package ru.mm.surv.capture;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WebcamDiscovery {

    private final Path ffmpeg;

    private final String captureFunction;

    private Process process;

    @Autowired
    public WebcamDiscovery(FfmpegInstaller ffmpegInstaller) {
        this.ffmpeg = ffmpegInstaller.getPath();
        this.captureFunction = getOsCaptureFunction();
        start();
    }

    private String getOsCaptureFunction() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return "dshow";
        } else if (SystemUtils.IS_OS_MAC) {
            return "avfoundation";
        } else {
            throw new RuntimeException("Only Windows, MacOS supported");
        }
    }

    @SneakyThrows
    public void start() {
        String[] args = new String[]{
                ffmpeg.toString(),
                "-f", captureFunction,
                "-list_devices", "true",
                "-i", ""
        };
        process = new ProcessBuilder(args).redirectErrorStream(true).start();
        process.waitFor();
        var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = reader.lines()
                .collect(Collectors.joining());
        log.warn(result);
    }

    @PreDestroy
    @SneakyThrows
    public void stop() {
        if (process != null) {
            process.destroy();
        }
    }
}

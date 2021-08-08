package ru.mm.surv.capture;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mm.surv.capture.config.InputSource;
import ru.mm.surv.capture.config.InputType;
import ru.mm.surv.capture.config.Platform;
import ru.mm.surv.capture.service.FfmpegInstaller;

import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WebcamDiscovery {

    private final Path ffmpeg;

    private Process process;

    private Platform platform;

    @Autowired
    public WebcamDiscovery(FfmpegInstaller ffmpegInstaller) {
        this.ffmpeg = ffmpegInstaller.getPath();
        this.platform = Platform.getCurrent();
    }

    @SneakyThrows
    public List<InputSource> getInputSources() {
        String[] args = new String[]{
                ffmpeg.toString(),
                "-f", platform.getOsCaptureFunction(),
                "-list_devices", "true",
                "-i", "dummy"
        };
        process = new ProcessBuilder(args).redirectErrorStream(true).start();
        process.waitFor();
        var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = reader.lines()
                .collect(Collectors.joining("\n"));
        return parse(platform, result);
    }

    @PreDestroy
    @SneakyThrows
    public void stop() {
        if (process != null) {
            process.destroy();
        }
    }

    protected List<InputSource> parse(Platform platform, String command) {
        var lines = command.split("\n");
        List<InputSource> sources = new ArrayList<>();
        InputType inputType = null;
        for (String line : lines) {
            if (line.contains(platform.getOsCaptureName() + " video devices")) {
                inputType = InputType.VIDEO;
                continue;
            }
            if (line.contains(platform.getOsCaptureName() + " audio devices")) {
                inputType = InputType.AUDIO;
                continue;
            }
            if (line.contains("Alternative name")) {
                continue;
            }
            if (! line.startsWith("[")) {
                continue;
            }
            if (inputType != null) {
                sources.add(platform.getInputSourceBuilder().apply(inputType, line));
            }
        }
        return sources;
    }
}

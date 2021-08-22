package ru.mm.surv.capture;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mm.surv.capture.config.InputFormat;
import ru.mm.surv.capture.config.InputSource;
import ru.mm.surv.capture.config.InputType;
import ru.mm.surv.capture.config.Platform;
import ru.mm.surv.capture.service.FfmpegInstaller;

import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class WebcamDiscovery {

    private final Path ffmpeg;

    private final Platform platform;

    private Process process;

    @Autowired
    public WebcamDiscovery(FfmpegInstaller ffmpegInstaller) {
        this.ffmpeg = ffmpegInstaller.getPath();
        this.platform = Platform.getCurrent();
    }

    @SneakyThrows
    public List<InputSource> getInputSources() {
        List<InputSource> sources = getDeviceList();
        var result = new ArrayList<InputSource>(sources.size());
        for (InputSource source : sources) {
            List<InputFormat> formats = getInputFormats(source);
            var sourceWithFormats = new InputSource(source.getType(), source.getId(), source.getName(), formats);
            result.add(sourceWithFormats);
        }
        return result;
    }

    private List<InputSource> getDeviceList() throws IOException {
        process = createListDevicesProcess();
        var getDeviceListRaw = getProcessResult(process);
        return parse(platform, getDeviceListRaw);
    }

    private Process createListDevicesProcess() throws IOException {
        String[] args = new String[]{
                ffmpeg.toString(),
                "-f", platform.getOsCaptureFunction(),
                "-list_devices", "true",
                "-i", "dummy"
        };
        return new ProcessBuilder(args).redirectErrorStream(true).start();
    }

    private List<InputFormat> getInputFormats(InputSource source) throws IOException {
        process = createListDeviceInfoProcess(source);
        Stream<String> getDeviceInfoListRaw = getProcessResult(process);
        return parseFormats(platform, getDeviceInfoListRaw);
    }

    private Process createListDeviceInfoProcess(InputSource source) throws IOException {
        String[] args = new String[]{
                ffmpeg.toString(),
                "-list_options", "true",
                "-f", platform.getOsCaptureFunction(),
                "-i", "video=" + source.getId()
        };
        return new ProcessBuilder(args).redirectErrorStream(true).start();
    }

    private Stream<String> getProcessResult(Process process) {
        var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        return reader.lines();
    }

    @PreDestroy
    @SneakyThrows
    public void stop() {
        if (process != null) {
            process.destroy();
        }
    }

    protected List<InputSource> parse(Platform platform, String command) {
        var linesArray = command.split("\n");
        var lines = Arrays.asList(linesArray);
        return parse(platform, lines);
    }

    protected List<InputSource> parse(Platform platform, Stream<String> lines) {
        var list = lines.collect(Collectors.toList());
        return parse(platform, list);
    }

    protected List<InputSource> parse(Platform platform, List<String> lines) {
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

    protected List<InputFormat> parseFormats(Platform platform, String command) {
        var linesArray = command.split("\n");
        var lines = Arrays.asList(linesArray);
        return parseFormats(platform, lines);
    }

    protected List<InputFormat> parseFormats(Platform platform, Stream<String> lines) {
        var list = lines.collect(Collectors.toList());
        return parseFormats(platform, list);
    }

    public List<InputFormat> parseFormats(Platform platform, List<String> lines) {
        List<InputFormat> sources = new ArrayList<>();
        for (String line : lines) {
            if (line.contains(platform.getOsCaptureName() + " video device")) {
                continue;
            }
            if (! line.startsWith("[")) {
                continue;
            }
            if (! line.contains("   vcodec")) {
                continue;
            }
            sources.add(InputFormat.fromWinLine(line));
        }
        return sources;
    }
}

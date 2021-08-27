package ru.mm.surv.capture.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import ru.mm.surv.capture.config.CameraConfig;
import ru.mm.surv.capture.config.FolderConfig;
import ru.mm.surv.capture.config.Platform;
import ru.mm.surv.capture.service.FfmpegInstaller;
import ru.mm.surv.config.User;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Slf4j
public class FfmpegSingleStream {

    public static final int LOG_WAIT_TIME = 100;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    private final Platform platform;
    private final Path ffmpeg;
    private final ScheduledExecutorService executor;
    private final String streamName;
    private final String webmAuthorization;
    private final String webmPublishUrl;
    private final String hlsFile;
    private final String mp4File;
    private final String mp4Thumb;
    private final Path streamThumb;
    private final CameraConfig captureConfig;
    private final Consumer<FfmpegSingleStream> shutdownListener;

    private Process process;

    @SneakyThrows
    public FfmpegSingleStream(Platform platform, FfmpegInstaller ffmpegInstaller, CameraConfig captureConfig, FolderConfig folders, User user, Consumer<FfmpegSingleStream> shutdownListener) {
        this.platform = platform;
        this.ffmpeg = ffmpegInstaller.path();
        this.executor = new ScheduledThreadPoolExecutor(1);
        this.streamName = captureConfig.getName();
        this.captureConfig = captureConfig;
        this.shutdownListener = shutdownListener;
        var basicCredentials = user.getUsername() + ":" + user.getPassword();
        var basicCredentialBytes = basicCredentials.getBytes(Charset.defaultCharset());
        this.webmAuthorization = HttpHeaders.AUTHORIZATION + ": Basic " + Base64.getEncoder().encodeToString(basicCredentialBytes);
        this.webmPublishUrl = "https://127.0.0.1:8443/stream/webm/publish/" + streamName;

        Path streamFolder = folders.getHls().resolve(streamName);
        Files.createDirectories(streamFolder);
        Files.walk(streamFolder)
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(File::delete);
        this.hlsFile = streamFolder.resolve("stream.m3u8").toString();

        Path mp4RecordsFolder = folders.getMp4();
        Files.createDirectories(mp4RecordsFolder);
        String currentDate = LocalDateTime.now().format(FORMATTER);
        String mp4FileName = streamName + "-" + currentDate + ".mp4";
        this.mp4File = mp4RecordsFolder.resolve(mp4FileName).toString();

        Path mp4ThumbFolder = folders.getMp4Thumb();
        Files.createDirectories(mp4ThumbFolder);
        String mp4ThumbName = streamName + "-" + currentDate + ".jpg";
        this.mp4Thumb = mp4ThumbFolder.resolve(mp4ThumbName).toString();

        Path streamThumbFolder = folders.getStreamThumb();
        Files.createDirectories(streamThumbFolder);
        Files.walk(streamThumbFolder)
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(File::delete);
        String streamThumbName = streamName + ".jpg";
        this.streamThumb = streamThumbFolder.resolve(streamThumbName);
    }

    public String getName() {
        return streamName;
    }

    public Optional<File> getThumb() {
         return Optional
                 .of(streamThumb)
                 .filter(Files::exists)
                 .map(Path::toFile);
    }

    public boolean isActive() {
        return process != null && process.isAlive();
    }

    @SneakyThrows
    public void start() {
        String[] args = new String[]{
                ffmpeg.toString(),
                "-f", platform.getOsCaptureFunction(),
                "-s", captureConfig.getInputResolution(),
                "-framerate", captureConfig.getInputFramerate(),
                "-i", platform.getSelector(captureConfig),
                "-r", "16",
                "-async", "1",
                "-vsync", "1",
                "-g", "16",
                "-vcodec", "libvpx",
                "-vb", "448k",
                "-acodec", "libvorbis",
                "-ab", "64k",
                "-f", "webm",
                "-headers", webmAuthorization + "\r\n",
                webmPublishUrl,
                "-c:v", "libx264",
                "-pix_fmt", "yuv420p",
                "-crf", "21",
                "-preset", "veryfast",
                "-g", "16",
                "-sc_threshold", "0",
                "-c:a", "aac",
                "-b:a", "128k",
                "-ac", "1",
                "-f", "hls",
                "-segment_list_type", "hls",
                "-hls_time", "1",
                "-hls_list_size", "10",
                "-hls_flags", "delete_segments",
                hlsFile,
                "-codec:a", "aac",
                "-b:a", "128k",
                "-codec:v", "libx264",
                "-pix_fmt", "yuv420p",
                "-b:v", "750k",
                "-minrate", "400k",
                "-maxrate", "1000k",
                "-bufsize", "1500k",
                "-vf", "scale=-1:360",
                mp4File,
                "-ss", "00:00:01",
                "-vframes", "1",
                mp4Thumb,
                "-ss", "00:00:01",
                "-vframes", "1",
                streamThumb.toString()

        };
        log.debug("{} arguments: {}", streamName, Arrays.toString(args));
        process = new ProcessBuilder(args).redirectErrorStream(true).start();
        executor.scheduleWithFixedDelay(new FfmpegLogger(streamName, process.getInputStream()), 0, LOG_WAIT_TIME, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(new LivenessChecker(process, this::shutdown), 0, 100, TimeUnit.MILLISECONDS);
    }

    @SneakyThrows
    public void stop() {
        log.info("Stopping stream {}", streamName);
        if (process != null) {
            try (PrintStream printStream = new PrintStream(process.getOutputStream())) {
                printStream.print("q");
            }
            if (!process.waitFor(10, TimeUnit.SECONDS)) {
                log.error("Waiting too long for stream " + streamName + " to stop");
                process.destroyForcibly();
            }
        }
        boolean result = executor.awaitTermination(10, TimeUnit.SECONDS);
        if (!result) {
            log.error("{} failed to stop log writers", streamName);
        }
    }

    @SneakyThrows
    private void shutdown(Process process) {
        int errorCode = process.exitValue();
        if (errorCode != 0) {
            log.error("Stream stopped with error code " + errorCode);
        }
        executor.shutdown();
        shutdownListener.accept(this);
    }
}

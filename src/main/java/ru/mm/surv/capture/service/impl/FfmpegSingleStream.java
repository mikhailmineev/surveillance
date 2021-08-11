package ru.mm.surv.capture.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import ru.mm.surv.capture.config.CameraConfig;
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
import java.util.concurrent.*;

@Slf4j
public class FfmpegSingleStream {

    public static final int LOG_WAIT_TIME = 100;

    private final Platform platform;
    private final Path ffmpeg;
    private final ScheduledExecutorService loggingExecutor;
    private final String streamName;
    private final String webmAuthorization;
    private final String webmPublishUrl;
    private final String hlsFile;
    private final String mp4File;
    private final CameraConfig captureConfig;

    private Process process;

    @SneakyThrows
    public FfmpegSingleStream(Platform platform, FfmpegInstaller ffmpegInstaller, CameraConfig captureConfig, Path hlsStreamsFolder, Path mp4RecordsFolder, User user) {
        this.platform = platform;
        this.ffmpeg = ffmpegInstaller.getPath();
        this.loggingExecutor = new ScheduledThreadPoolExecutor(1);
        this.streamName = captureConfig.getName();
        this.captureConfig = captureConfig;
        var basicCredentials = user.getUsername() + ":" + user.getPassword();
        var basicCredentialBytes = basicCredentials.getBytes(Charset.defaultCharset());
        this.webmAuthorization = HttpHeaders.AUTHORIZATION + ": Basic " + Base64.getEncoder().encodeToString(basicCredentialBytes);
        this.webmPublishUrl = "https://127.0.0.1:8443/stream/webm/publish/" + streamName;

        Path streamFolder = hlsStreamsFolder.resolve(streamName);
        Files.createDirectories(streamFolder);
        Files.walk(streamFolder)
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(File::delete);
        this.hlsFile = streamFolder.resolve("stream.m3u8").toString();

        Files.createDirectories(mp4RecordsFolder);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String mp4FileName = streamName + "-" + LocalDateTime.now().format(formatter) + ".mp4";
        this.mp4File = mp4RecordsFolder.resolve(mp4FileName).toString();
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
                "-codec:a", "libvorbis",
                "-b:a", "128k",
                "-codec:v", "libx264",
                "-pix_fmt", "yuv420p",
                "-b:v", "750k",
                "-minrate", "400k",
                "-maxrate", "1000k",
                "-bufsize", "1500k",
                "-vf", "scale=-1:360",
                mp4File
        };
        log.debug("{} arguments: {}", streamName, Arrays.toString(args));
        process = new ProcessBuilder(args).redirectErrorStream(true).start();
        loggingExecutor.schedule(new Logger(streamName, process.getInputStream()), LOG_WAIT_TIME, TimeUnit.MILLISECONDS);
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
            }
            int errorCode = process.exitValue();
            if (errorCode != 0) {
                log.error("Stream stopped with error code " + errorCode);
            }
        }
        loggingExecutor.shutdownNow();
        boolean result = loggingExecutor.awaitTermination(10, TimeUnit.SECONDS);
        if (!result) {
            log.error("{} failed to stop log writers", streamName);
        }
    }

    private static class Logger implements Runnable {

        private final String streamName;
        private final BufferedReader reader;

        public Logger(String streamName, InputStream stream) {
            this.streamName = streamName;
            this.reader = new BufferedReader(new InputStreamReader(stream));
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("{} {}", streamName, line);
                }
            } catch (IOException e) {
                log.error(streamName + " " + e.getMessage(), e);
            }
        }
    }
}

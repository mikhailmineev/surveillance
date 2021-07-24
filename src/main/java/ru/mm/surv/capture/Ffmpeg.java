package ru.mm.surv.capture;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import ru.mm.surv.capture.config.CameraConfig;
import ru.mm.surv.config.User;

import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.*;

@Slf4j
public class Ffmpeg {

    public static final int LOG_WAIT_TIME = 100;

    private final Path ffmpeg;
    private final String captureFunction;
    private final ScheduledExecutorService loggingExecutor;
    private final String streamName;
    private final String webmAuthorization;
    private final String webmPublishUrl;
    private final String hlsFile;
    private final CameraConfig captureConfig;

    private Process process;

    @SneakyThrows
    public Ffmpeg(Path ffmpeg, String captureFunction, String streamName, CameraConfig captureConfig, Path folder, User user) {
        this.ffmpeg = ffmpeg;
        this.captureFunction = captureFunction;
        this.loggingExecutor = new ScheduledThreadPoolExecutor(1);
        this.streamName = streamName;
        this.captureConfig = captureConfig;
        var basicCredentials = user.getUsername() + ":" + user.getPassword();
        var basicCredentialBytes = basicCredentials.getBytes(Charset.defaultCharset());
        this.webmAuthorization = HttpHeaders.AUTHORIZATION + ": Basic " + Base64.getEncoder().encodeToString(basicCredentialBytes);
        this.webmPublishUrl = "https://127.0.0.1:8443/stream/webm/publish/" + streamName;
        Path streamFolder = folder.resolve(streamName);
        Files.createDirectories(streamFolder);
        Files.walk(streamFolder)
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .forEach(File::delete);
        this.hlsFile = streamFolder.resolve("stream.m3u8").toString();
    }

    @SneakyThrows
    public void start() {
        String[] args = new String[]{
                ffmpeg.toString(),
                "-f", captureFunction,
                "-s", captureConfig.getInputResolution(),
                "-framerate", captureConfig.getInputFramerate(),
                "-i", captureConfig.getSelector(),
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
                "-c:v", "h264",
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
                hlsFile
        };
        log.debug("{} arguments: {}", streamName, Arrays.toString(args));
        process = new ProcessBuilder(args).redirectErrorStream(true).start();
        loggingExecutor.schedule(new Logger(streamName, process.getInputStream()), LOG_WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    @SneakyThrows
    public void stop() {
        loggingExecutor.shutdownNow();
        boolean result = loggingExecutor.awaitTermination(10, TimeUnit.SECONDS);
        if (!result) {
            log.error("{} failed to stop log writers", streamName);
        }
        if (process != null) {
            process.destroy();
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

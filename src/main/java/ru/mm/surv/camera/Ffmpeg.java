package ru.mm.surv.camera;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.*;

@Slf4j
@Component
public class Ffmpeg {

    public static final String FFMPEG_BINARY = "C:\\Users\\mikev\\Downloads\\ffmpeg-4.4-full_build\\bin\\ffmpeg.exe";
    public static final int LOG_WAIT_TIME = 100;

    private final ScheduledExecutorService loggingExecutor;
    private final String streamName;
    private final String hlsFile;

    private Process process;

    @Autowired
    public Ffmpeg(@Value("${ffmpeg.hls.folder}") Path folder) throws IOException {
        this.loggingExecutor = new ScheduledThreadPoolExecutor(2);
        this.streamName = "first";
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
                FFMPEG_BINARY,
                "-f", "dshow",
                "-s", "320x240",
                "-r", "16",
                "-i", "video=USB Video Device:audio=Microphone (USB Audio Device)",
                "-g", "25",
                "-acodec", "libvorbis",
                "-ab", "64k",
                "-vcodec", "libvpx",
                "-vb", "448k",
                "-headers", "Authorization: Basic cHVibGlzaGVyOmdkc2ZnZXJ0Z2RmZ3M=\r\n",
                "-f", "webm",
                "https://127.0.0.1:8443/stream/webm/publish/" + streamName,
                "-g", "25",
                "-sc_threshold", "0",
                "-f", "hls",
                "-segment_list_type", "hls",
                "-hls_time", "4",
                "-hls_list_size", "10",
                "-hls_flags", "delete_segments",
                hlsFile
        };
        log.debug("Ffmpeg arguments: {}", Arrays.toString(args));
        process = new ProcessBuilder(args).start();
        loggingExecutor.schedule(new Logger(process.getInputStream()), LOG_WAIT_TIME, TimeUnit.MILLISECONDS);
        loggingExecutor.schedule(new Logger(process.getErrorStream()), LOG_WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        loggingExecutor.shutdownNow();
        loggingExecutor.awaitTermination(10, TimeUnit.SECONDS);
        if (process != null) {
            process.destroy();
        }
    }

    private static class Logger implements Runnable {

        private final BufferedReader reader;

        public Logger(InputStream stream) {
            this.reader = new BufferedReader(new InputStreamReader(stream));
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}

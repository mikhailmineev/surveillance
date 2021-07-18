package ru.mm.surv.camera;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Component
public class Ffmpeg {

    private Process process;

    @SneakyThrows
    public void start() {
        process = new ProcessBuilder(
                "C:\\Users\\mikev\\Downloads\\ffmpeg-4.4-full_build\\bin\\ffmpeg.exe",
                "-f",
                "dshow",
                "-s",
                "320x240",
                "-r",
                "16",
                "-i",
                "video=USB Video Device:audio=Microphone (USB Audio Device)",
                "-g",
                "52",
                "-acodec",
                "libvorbis",
                "-ab",
                "64k",
                "-vcodec",
                "libvpx",
                "-vb",
                "448k",
                "-headers",
                "Authorization: Basic cHVibGlzaGVyOmdkc2ZnZXJ0Z2RmZ3M=\r\n",
                "-f",
                "webm",
                "http://127.0.0.1:8080/publish/first"
    ).start();
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));){
                while (process.isAlive()) {
                    String line = reader.readLine();
                    if (line == null) {
                        Thread.sleep(100);
                        continue;
                    }
                    log.info(reader.readLine());
                }
            } catch (IOException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }).start();
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));){
                while (process.isAlive()) {
                    String line = reader.readLine();
                    if (line == null) {
                        Thread.sleep(100);
                        continue;
                    }
                    log.info(reader.readLine());
                }
            } catch (IOException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }).start();    }

    @PreDestroy
    public void stop() {
        if (process != null) {
            process.destroy();
        }
    }
}

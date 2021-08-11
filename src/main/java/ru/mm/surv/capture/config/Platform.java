package ru.mm.surv.capture.config;

import lombok.Getter;
import org.apache.commons.lang3.SystemUtils;
import ru.mm.surv.capture.ConsumerIO;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.text.MessageFormat;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@Getter
public enum Platform {
    MACOS(
            "mac",
            "{0}:{1}",
            "avfoundation",
            "AVFoundation",
            (path) -> {
                Set<PosixFilePermission> perms = Files.getPosixFilePermissions(path);
                perms.add(PosixFilePermission.OWNER_EXECUTE);
                Files.setPosixFilePermissions(path, perms);
            },
            InputSource::fromMacLine),
    WIN(
            "win",
            "video={0}:audio={1}",
            "dshow",
            "DirectShow",
            (path) -> {},
            InputSource::fromWinLine);

    private final String name;
    private final String selectorFormat;
    private final String osCaptureFunction;
    private final String osCaptureName;
    private final ConsumerIO<Path> ffmpegPostInstall;
    private final BiFunction<InputType, String, InputSource> inputSourceBuilder;

    Platform(String name, String selectorFormat, String osCaptureFunction, String osCaptureName, ConsumerIO<Path> ffmpegPostInstall, BiFunction<InputType, String, InputSource> inputSourceBuilder) {
        this.name = name;
        this.selectorFormat = selectorFormat;
        this.osCaptureFunction = osCaptureFunction;
        this.osCaptureName = osCaptureName;
        this.ffmpegPostInstall = ffmpegPostInstall;
        this.inputSourceBuilder = inputSourceBuilder;
    }

    public String getSelector(CameraConfig captureConfig) {
        return MessageFormat.format(selectorFormat, captureConfig.getVideo(), captureConfig.getAudio());
    }

    public static Platform getCurrent() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return WIN;
        } else if (SystemUtils.IS_OS_MAC) {
            return MACOS;
        } else {
            throw new RuntimeException("Only Windows, MacOS supported");
        }
    }
}

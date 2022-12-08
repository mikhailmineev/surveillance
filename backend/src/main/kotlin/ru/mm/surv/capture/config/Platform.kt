package ru.mm.surv.capture.config

import lombok.Getter
import java.util.function.BiFunction
import java.nio.file.attribute.PosixFilePermission
import java.text.MessageFormat
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer

@Getter
enum class Platform(
    val osName: String,
    private val selectorFormat: String,
    val osCaptureFunction: String,
    val osCaptureName: String,
    val ffmpegPostInstall: Consumer<Path>,
    val inputSourceBuilder: BiFunction<InputType, String, InputSource>
) {
    MACOS(
        "mac",
        "{0}:{1}",
        "avfoundation",
        "AVFoundation",
        {
            val perms = Files.getPosixFilePermissions(it)
            perms.add(PosixFilePermission.OWNER_EXECUTE)
            Files.setPosixFilePermissions(it, perms)
        },
        InputSourceFactory::fromMacLine
    ),
    WIN(
        "win",
        "video={0}:audio={1}",
        "dshow",
        "DirectShow",
        { },
        InputSourceFactory::fromWinLine
    );

    fun getSelector(captureConfig: CameraConfig): String {
        return MessageFormat.format(selectorFormat, captureConfig.video, captureConfig.audio)
    }
}

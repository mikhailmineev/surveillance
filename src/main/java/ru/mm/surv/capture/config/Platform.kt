package ru.mm.surv.capture.config

import lombok.Getter
import java.util.function.BiFunction
import java.nio.file.attribute.PosixFilePermission
import java.io.IOException
import java.text.MessageFormat
import java.io.UncheckedIOException
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
        Consumer { path: Path? ->
            try {
                val perms = Files.getPosixFilePermissions(path)
                perms.add(PosixFilePermission.OWNER_EXECUTE)
                Files.setPosixFilePermissions(path, perms)
            } catch (e: IOException) {
                throw UncheckedIOException(e)
            }
        },
        InputSourceFactory::fromMacLine
    ),
    WIN(
        "win",
        "video={0}:audio={1}",
        "dshow",
        "DirectShow",
        Consumer { path: Path? -> },
        InputSourceFactory::fromWinLine
    );

    fun getSelector(captureConfig: CameraConfig): String {
        return MessageFormat.format(selectorFormat, captureConfig.video, captureConfig.audio)
    }
}

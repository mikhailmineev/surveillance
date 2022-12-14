package ru.mm.surv.capture.ffmpeg.feature.impl

import ru.mm.surv.capture.config.CameraConfig
import ru.mm.surv.capture.config.FolderConfig
import ru.mm.surv.capture.ffmpeg.feature.Feature
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class HlsStreamOutput(
    private val captureConfig: CameraConfig,
    private val folders: FolderConfig
    ): Feature {

    private fun createHlsFile(): String {
        val streamFolder = folders.hls.resolve(captureConfig.name)
        Files.createDirectories(streamFolder)
        Files.walk(streamFolder)
            .filter { Files.isRegularFile(it) }
            .map(Path::toFile)
            .forEach(File::delete)
        return streamFolder.resolve("stream.m3u8").toString()
    }

    override fun args(): Array<String> {
        val hlsFile = createHlsFile()
        return arrayOf(
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
            hlsFile
        )
    }

}

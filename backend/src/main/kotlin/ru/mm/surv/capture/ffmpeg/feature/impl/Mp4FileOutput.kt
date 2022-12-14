package ru.mm.surv.capture.ffmpeg.feature.impl

import ru.mm.surv.capture.config.CameraConfig
import ru.mm.surv.capture.config.FolderConfig
import ru.mm.surv.capture.ffmpeg.feature.Feature
import java.nio.file.Files

class Mp4FileOutput(
    private val captureConfig: CameraConfig,
    private val folders: FolderConfig,
    private val currentDate: String
    ): Feature {

    private fun createMp4File(): String {
        val mp4RecordsFolder = folders.mp4
        Files.createDirectories(mp4RecordsFolder)
        val mp4FileName = captureConfig.name + "-" + currentDate + ".mp4"
        return mp4RecordsFolder.resolve(mp4FileName).toString()
    }

    override fun args(): Array<String> {
        val mp4File = createMp4File()
        return arrayOf(
            "-codec:a", "aac",
            "-b:a", "128k",
            "-codec:v", "libx264",
            "-pix_fmt", "yuv420p",
            "-b:v", "750k",
            "-minrate", "400k",
            "-maxrate", "1000k",
            "-bufsize", "1500k",
            "-vf", "scale=-1:360",
            mp4File
        )
    }

}

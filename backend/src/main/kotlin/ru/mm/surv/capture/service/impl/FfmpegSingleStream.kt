package ru.mm.surv.capture.service.impl

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import ru.mm.surv.capture.service.FfmpegInstaller
import ru.mm.surv.capture.config.CameraConfig
import ru.mm.surv.capture.config.FolderConfig
import ru.mm.surv.capture.config.Platform
import java.util.concurrent.ScheduledExecutorService
import java.lang.Process
import java.io.PrintStream
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.lang.ProcessBuilder
import ru.mm.surv.config.User
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

private const val LOG_WAIT_TIME = 100
private val FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")

class FfmpegSingleStream(
    platform: Platform,
    ffmpegInstaller: FfmpegInstaller,
    private val captureConfig: CameraConfig,
    folders: FolderConfig,
    user: User,
    private val shutdownListener: Consumer<FfmpegSingleStream>
) {

    private val log = LoggerFactory.getLogger(FfmpegSingleStream::class.java)

    private val executor: ScheduledExecutorService = ScheduledThreadPoolExecutor(1)
    private val streamThumb: Path
    private val process: Process

    private fun createMp4File(captureConfig: CameraConfig, folders: FolderConfig, currentDate: String): String {
        val mp4RecordsFolder = folders.mp4
        Files.createDirectories(mp4RecordsFolder)
        val mp4FileName = captureConfig.name + "-" + currentDate + ".mp4"
        return mp4RecordsFolder.resolve(mp4FileName).toString()
    }

    private fun createStreamThumb(captureConfig: CameraConfig, folders: FolderConfig): Path {
        val streamThumbFolder = folders.streamThumb
        Files.createDirectories(streamThumbFolder)
        Files.walk(streamThumbFolder)
            .filter { Files.isRegularFile(it) }
            .map(Path::toFile)
            .forEach(File::delete)
        val streamThumbName = captureConfig.name + ".jpg"
        return streamThumbFolder.resolve(streamThumbName)
    }

    private fun createMp4Thumb(captureConfig: CameraConfig, folders: FolderConfig, currentDate: String): String {
        val mp4ThumbFolder = folders.mp4Thumb
        Files.createDirectories(mp4ThumbFolder)
        val mp4ThumbName = captureConfig.name + "-" + currentDate + ".jpg"
        return mp4ThumbFolder.resolve(mp4ThumbName).toString()
    }

    private fun createHlsFile(captureConfig: CameraConfig, folders: FolderConfig): String {
        val streamFolder = folders.hls.resolve(captureConfig.name)
        Files.createDirectories(streamFolder)
        Files.walk(streamFolder)
            .filter { Files.isRegularFile(it) }
            .map(Path::toFile)
            .forEach(File::delete)
        return streamFolder.resolve("stream.m3u8").toString()
    }

    private fun createWebmAuthorization(user: User): String {
        val basicCredentials = user.username + ":" + user.password
        val basicCredentialBytes = basicCredentials.toByteArray(Charset.defaultCharset())
        val base64EncodedCredentials = Base64.getEncoder().encodeToString(basicCredentialBytes)
        return "${HttpHeaders.AUTHORIZATION}: Basic $base64EncodedCredentials\r\n"
    }

    fun getName(): String {
        return captureConfig.name
    }

    fun getThumb(): File? {
        return streamThumb.takeIf { Files.exists(it) }?.toFile()
    }

    fun isActive(): Boolean {
        return process.isAlive
    }

    fun stop() {
        log.info("Stopping stream {}", captureConfig.name)
        PrintStream(process.outputStream).use { printStream -> printStream.print("q") }
        if (!process.waitFor(10, TimeUnit.SECONDS)) {
            log.error("Waiting too long for stream " + captureConfig.name + " to stop")
            process.destroyForcibly()
        }
        val result = executor.awaitTermination(10, TimeUnit.SECONDS)
        if (!result) {
            log.error("{} failed to stop log writers", captureConfig.name)
        }
    }

    private fun shutdown(process: Process) {
        val errorCode = process.exitValue()
        if (errorCode != 0) {
            log.error("Stream stopped with error code $errorCode")
        }
        executor.shutdown()
        shutdownListener.accept(this)
    }

    init {
        val webmAuthorization = createWebmAuthorization(user)
        val webmPublishUrl = "https://127.0.0.1:8443/stream/webm/publish/" + captureConfig.name
        val hlsFile = createHlsFile(captureConfig, folders)
        val currentDate = LocalDateTime.now().format(FORMATTER)
        val mp4File = createMp4File(captureConfig, folders, currentDate)
        val mp4Thumb = createMp4Thumb(captureConfig, folders, currentDate)
        streamThumb = createStreamThumb(captureConfig, folders)
        val args = arrayOf(
            ffmpegInstaller.path().toString(),
            "-f", platform.osCaptureFunction,
            "-s", captureConfig.inputResolution,
            "-framerate", captureConfig.inputFramerate,
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
            "-headers", webmAuthorization,
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
        )
        log.debug("{} arguments: {}", captureConfig.name, args.contentToString())
        process = ProcessBuilder(*args).redirectErrorStream(true).start()
        executor.scheduleWithFixedDelay(
            FfmpegLogger(captureConfig.name, process.inputStream),
            0,
            LOG_WAIT_TIME.toLong(),
            TimeUnit.MILLISECONDS
        )
        executor.scheduleWithFixedDelay(
            LivenessChecker(process) { shutdown(it) },
            0,
            100,
            TimeUnit.MILLISECONDS
        )
    }
}

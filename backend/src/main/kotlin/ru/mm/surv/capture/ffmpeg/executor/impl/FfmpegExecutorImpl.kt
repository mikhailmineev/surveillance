package ru.mm.surv.capture.ffmpeg.executor.impl

import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import ru.mm.surv.capture.ffmpeg.installer.FfmpegInstaller
import ru.mm.surv.capture.config.CameraConfig
import ru.mm.surv.capture.ffmpeg.executor.FfmpegExecutor
import ru.mm.surv.capture.ffmpeg.executor.FfmpegLogger
import ru.mm.surv.capture.ffmpeg.executor.LivenessChecker
import ru.mm.surv.capture.ffmpeg.feature.Feature
import ru.mm.surv.capture.ffmpeg.feature.impl.StreamThumbOutput
import java.lang.Process
import java.io.PrintStream
import java.lang.ProcessBuilder
import ru.mm.surv.dto.StreamStatus
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer

private const val LOG_WAIT_MILLIS = 100L
private const val HEALTH_CHECK_MILLIS = 100L
private const val STOP_FORCIBLY_SECONDS = 10L

class FfmpegExecutorImpl(
    private val ffmpegInstaller: FfmpegInstaller,
    private val captureConfig: CameraConfig,
    private val scheduler: TaskScheduler,
    private val ffmpegFeatures: List<Feature>,
    private val statusListener: List<BiConsumer<StreamStatus, FfmpegExecutor>>,
) : FfmpegExecutor {

    private val log = LoggerFactory.getLogger(FfmpegExecutor::class.java)
    private val streamThumb: Path?
    private lateinit var process: Process
    private lateinit var logger: ScheduledFuture<*>
    private lateinit var livenessChecker: ScheduledFuture<*>

    private var status: StreamStatus = StreamStatus.STOPPED

    override fun getName(): String {
        return captureConfig.name
    }

    override fun getThumb(): File? {
        return streamThumb?.takeIf { Files.exists(it) }?.toFile()
    }

    override fun getStatus(): StreamStatus {
        return status
    }

    private fun setStatus(status: StreamStatus) {
        this.status = status
        statusListener.forEach { it.accept(status, this) }
        log.info("Stream status updated $status")
    }

    private fun teardownDeadProcess(process: Process) {
        val errorCode = process.exitValue()
        if (errorCode != 0) {
            log.error("Stream stopped with error code $errorCode")
        } else {
            log.info("Stream stopped gracefully")
        }
        logger.cancel(false)
        livenessChecker.cancel(false)
        setStatus(StreamStatus.STOPPED)
    }

    init {
        streamThumb = ffmpegFeatures.filterIsInstance<StreamThumbOutput>().firstOrNull()?.path
    }

    override fun start() {
        setStatus(StreamStatus.STARTING)
        var args = arrayOf(
            ffmpegInstaller.path().toString(),
        )
        ffmpegFeatures.map { it.args() }.forEach { args = args.plus(it) }

        log.debug("${captureConfig.name} arguments: ${args.contentToString()}")
        process = ProcessBuilder(*args).redirectErrorStream(true).start()
        logger = scheduler.scheduleWithFixedDelay(
            FfmpegLogger(captureConfig.name, process.inputStream),
            Duration.ofMillis(LOG_WAIT_MILLIS)
        )
        livenessChecker = scheduler.scheduleWithFixedDelay(
            LivenessChecker(process) { teardownDeadProcess(it) },
            Duration.ofMillis(HEALTH_CHECK_MILLIS)
        )
        setStatus(StreamStatus.RUNNING)
    }

    override fun stop() {
        if (status == StreamStatus.STOPPED || status == StreamStatus.STOPPING) {
            log.info("Stream ${captureConfig.name} already in status $status")
            return
        }
        log.info("Stopping stream {}", captureConfig.name)
        setStatus(StreamStatus.STOPPING)
        PrintStream(process.outputStream).use { printStream -> printStream.print("q"); printStream.flush() }
        if (!process.waitFor(STOP_FORCIBLY_SECONDS, TimeUnit.SECONDS)) {
            log.error("Waiting too long for stream ${captureConfig.name} to stop")
            process.destroyForcibly()
        }
    }
}

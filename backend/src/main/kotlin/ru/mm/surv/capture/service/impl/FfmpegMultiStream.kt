package ru.mm.surv.capture.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import ru.mm.surv.capture.config.CurrentPlatform.get
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Scope
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import ru.mm.surv.capture.config.FolderConfig
import ru.mm.surv.capture.event.StreamStatusEvent
import ru.mm.surv.config.Users
import ru.mm.surv.capture.repository.WebcamRepository
import ru.mm.surv.capture.ffmpeg.installer.FfmpegInstaller
import ru.mm.surv.capture.service.FfmpegStream
import ru.mm.surv.capture.ffmpeg.executor.FfmpegExecutor
import ru.mm.surv.capture.ffmpeg.executor.impl.FfmpegExecutorImpl
import ru.mm.surv.capture.ffmpeg.feature.impl.*
import ru.mm.surv.dto.StreamStatus
import ru.mm.surv.dto.aggregatedStreamStatus
import java.util.HashMap
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.function.BiConsumer

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class FfmpegMultiStream(
    private val folders: FolderConfig,
    @Value("\${ffmpeg.publisher}") private val publisher: String,
    private val users: Users,
    private val webcamRepository: WebcamRepository,
    private val ffmpegInstaller: FfmpegInstaller,
    @Qualifier("taskScheduler") private val scheduler: TaskScheduler,
    private val eventPublisher: ApplicationEventPublisher
) : FfmpegStream {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")
    private val recorders: MutableMap<String, FfmpegExecutor> = HashMap()

    @Synchronized
    private fun remove(stream: FfmpegExecutor) {
        recorders.remove(stream.getName())
    }

    @Synchronized
    override fun start() {
        val publishUser = users.users[publisher]?: throw RuntimeException("User $publisher not found in users list")
        val currentDate = LocalDateTime.now().format(dateTimeFormatter)
        webcamRepository.all().forEach {
            val features = listOf(
                WebcamStreamInput(it, get()),
                WebmStreamOutput(it, publishUser),
                HlsStreamOutput(it, folders),
                Mp4FileOutput(it, folders, currentDate),
                Mp4ThumbOutput(it, folders, currentDate),
                StreamThumbOutput(it, folders)
            )
            val listeners: List<BiConsumer<StreamStatus, FfmpegExecutor>> = listOf(
                BiConsumer { _, _ ->
                    val status = status()
                    val streams = if (status == StreamStatus.RUNNING) streamNames() else listOf()
                    eventPublisher.publishEvent(StreamStatusEvent(status, streams, this)) },
                BiConsumer { status, stream ->
                    if (status == StreamStatus.STOPPED) this.remove(stream) }
            )
            recorders[it.name] = FfmpegExecutorImpl(
                ffmpegInstaller,
                it,
                scheduler,
                features,
                listeners
            )
        }
        recorders.forEach { (_, ffmpeg) -> ffmpeg.start() }
    }

    @Synchronized
    override fun stop() {
        recorders.forEach { (_, ffmpeg) -> ffmpeg.stop() }
    }

    @Synchronized
    override fun status(): StreamStatus {
        return aggregatedStreamStatus(recorders.values.map(FfmpegExecutor::getStatus))
    }

    @Synchronized
    override fun streamNames(): Collection<String> {
        return recorders.keys
    }

    @Synchronized
    override fun getThumb(stream: String): File? {
        return recorders[stream]?.getThumb()
    }
}

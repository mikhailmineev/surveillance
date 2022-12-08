package ru.mm.surv.capture.service.impl

import ru.mm.surv.capture.config.CurrentPlatform.get
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import ru.mm.surv.capture.config.FolderConfig
import ru.mm.surv.config.Users
import ru.mm.surv.capture.repository.WebcamRepository
import ru.mm.surv.capture.service.FfmpegInstaller
import ru.mm.surv.capture.service.FfmpegStream
import java.util.HashMap
import java.io.File

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class FfmpegMultiStream(
    private val folders: FolderConfig,
    @Value("\${ffmpeg.publisher}") private val publisher: String,
    private val users: Users,
    private val webcamRepository: WebcamRepository,
    private val ffmpegInstaller: FfmpegInstaller
) : FfmpegStream {

    private val recorders: MutableMap<String, FfmpegSingleStream> = HashMap()

    @Synchronized
    private fun remove(stream: FfmpegSingleStream) {
        recorders.remove(stream.getName())
    }

    @Synchronized
    override fun start() {
        val publishUser = users.users[publisher]?: throw RuntimeException("User $publisher not found in users list")
        webcamRepository.all().forEach {
            recorders[it.name] = FfmpegSingleStream(
                get(),
                ffmpegInstaller,
                it,
                folders,
                publishUser
            ) { stream: FfmpegSingleStream -> this.remove(stream) }
        }
    }

    @Synchronized
    override fun stop() {
        recorders.forEach { (_, ffmpeg) -> ffmpeg.stop() }
    }

    @Synchronized
    override fun isActive(): Boolean {
        return recorders.values.stream().anyMatch { it.isActive() }
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

package ru.mm.surv.capture.ffmpeg.installer

import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Service
import ru.mm.surv.capture.config.CurrentPlatform
import ru.mm.surv.capture.config.Platform
import java.lang.RuntimeException
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path

private const val FFMPEG_RESOURCES_PATH = "classpath:/ffmpeg"

@Service
@Slf4j
class FfmpegInstallerImpl(
    @Value("\${ffmpeg.folder}") private val executableFolder: Path) : FfmpegInstaller {

    private val log = LoggerFactory.getLogger(FfmpegInstallerImpl::class.java)

    private val ffmpeg = getFfmpegExecutable()

    private fun getFfmpegExecutable(): Path {
            val platform = CurrentPlatform.get()
            val ffmpegResource = locateFfmpegResource(platform)
            val ffmpegBinName = ffmpegResource.filename ?: throw RuntimeException("No filename in resource $ffmpegResource")
            val target = executableFolder.resolve(ffmpegBinName)
            return executableFolder.resolve(ffmpegBinName)
                .takeIf(Files::exists)
                ?: installFfmpeg(platform, ffmpegResource, target)
        }

    override fun path(): Path {
        return ffmpeg
    }

    private fun locateFfmpegResource(platform: Platform): Resource {
        return try {
            val resources =
                PathMatchingResourcePatternResolver().getResources(FFMPEG_RESOURCES_PATH + "/" + platform.osName + "/*")
            val size = resources.size
            if (size != 1) {
                throw RuntimeException("Found not 1 executable but $size")
            }
            resources[0]
        } catch (e: Exception) {
            throw RuntimeException("Failed to locate ffmpeg in distribution path", e)
        }
    }

    private fun installFfmpeg(platform: Platform, ffmpegResource: Resource, target: Path): Path {
        return try {
            log.info("Ffmpeg not installed, installing new")
            Files.createDirectories(executableFolder)
            ffmpegResource.inputStream.use { inputStream ->
                FileOutputStream(target.toFile()).use { outputStream ->
                    inputStream.transferTo(
                        outputStream
                    )
                }
            }
            platform.ffmpegPostInstall.accept(target)
            target
        } catch (e: Exception) {
            throw RuntimeException("Failed to install ffmpeg", e)
        }
    }
}

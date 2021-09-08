package ru.mm.surv.capture

import ru.mm.surv.capture.config.CurrentPlatform.get
import ru.mm.surv.capture.config.InputFormatFactory.fromWinLine
import lombok.extern.slf4j.Slf4j
import ru.mm.surv.capture.service.FfmpegInstaller
import java.lang.Process
import org.springframework.stereotype.Component
import ru.mm.surv.capture.config.*
import java.lang.ProcessBuilder
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.annotation.PreDestroy
import java.util.stream.Collectors
import java.nio.file.Path
import java.util.*
import java.util.stream.Stream

@Slf4j
@Component
class WebcamDiscovery(ffmpegInstaller: FfmpegInstaller) {

    private val ffmpeg: Path = ffmpegInstaller.path()

    private val platform: Platform = get()

    private var process: Process? = null

    fun getInputSources(): List<InputSource> {
        val sources = getDeviceList()
        val result = ArrayList<InputSource>(sources.size)
        for (source in sources) {
            val formats = getInputFormats(source)
            val sourceWithFormats = InputSource(source.type, source.id, source.name, formats)
            result.add(sourceWithFormats)
        }
        return result
    }

    private fun getDeviceList(): List<InputSource> {
        process = createListDevicesProcess()
        val getDeviceListRaw = getProcessResult(process)
        return parse(platform, getDeviceListRaw)
    }

    private fun createListDevicesProcess(): Process {
        val args = arrayOf(
            ffmpeg.toString(),
            "-f", platform.osCaptureFunction,
            "-list_devices", "true",
            "-i", "dummy"
        )
        return ProcessBuilder(*args).redirectErrorStream(true).start()
    }

    private fun getInputFormats(source: InputSource): List<InputFormat> {
        process = createListDeviceInfoProcess(source)
        val getDeviceInfoListRaw = getProcessResult(process)
        return parseFormats(platform, getDeviceInfoListRaw)
    }

    private fun createListDeviceInfoProcess(source: InputSource): Process {
        val args = arrayOf(
            ffmpeg.toString(),
            "-list_options", "true",
            "-f", platform.osCaptureFunction,
            "-i", "video=" + source.id
        )
        return ProcessBuilder(*args).redirectErrorStream(true).start()
    }

    private fun getProcessResult(process: Process?): Stream<String> {
        val reader = BufferedReader(InputStreamReader(process!!.inputStream))
        return reader.lines()
    }

    @PreDestroy
    fun stop() {
        if (process != null) {
            process!!.destroy()
        }
    }

    fun parse(platform: Platform, command: String): List<InputSource> {
        val linesArray = command.split("\n").toTypedArray()
        val lines = listOf(*linesArray)
        return parse(platform, lines)
    }

    protected fun parse(platform: Platform, lines: Stream<String>): List<InputSource> {
        val list = lines.collect(Collectors.toList())
        return parse(platform, list)
    }

    protected fun parse(platform: Platform, lines: List<String>): List<InputSource> {
        val sources: MutableList<InputSource> = ArrayList()
        var inputType: InputType? = null
        for (line in lines) {
            if (line.contains(platform.osCaptureName + " video devices")) {
                inputType = InputType.VIDEO
                continue
            }
            if (line.contains(platform.osCaptureName + " audio devices")) {
                inputType = InputType.AUDIO
                continue
            }
            if (line.contains("Alternative name")) {
                continue
            }
            if (!line.startsWith("[")) {
                continue
            }
            if (inputType != null) {
                sources.add(platform.inputSourceBuilder.apply(inputType, line))
            }
        }
        return sources
    }

    protected fun parseFormats(platform: Platform, lines: Stream<String>): List<InputFormat> {
        val list = lines.collect(Collectors.toList())
        return parseFormats(platform, list)
    }

    fun parseFormats(platform: Platform, lines: List<String>): List<InputFormat> {
        val sources: MutableList<InputFormat> = ArrayList()
        for (line in lines) {
            if (line.contains(platform.osCaptureName + " video device")) {
                continue
            }
            if (!line.startsWith("[")) {
                continue
            }
            if (!line.contains("   vcodec")) {
                continue
            }
            sources.add(fromWinLine(line))
        }
        return sources
    }
}

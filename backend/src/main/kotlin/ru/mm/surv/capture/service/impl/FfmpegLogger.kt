package ru.mm.surv.capture.service.impl

import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import java.lang.Runnable
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

@Slf4j
internal class FfmpegLogger(private val streamName: String, stream: InputStream) : Runnable {

    private val log = LoggerFactory.getLogger(FfmpegLogger::class.java)

    private val reader = BufferedReader(InputStreamReader(stream))

    override fun run() {
        try {
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                log.info("{} {}", streamName, line)
            }
        } catch (e: IOException) {
            val errorMessage = e.message
            log.error("$streamName $errorMessage", e)
        }
    }
}

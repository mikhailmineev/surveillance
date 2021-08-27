package ru.mm.surv.web

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import ru.mm.surv.codecs.webm.incubator.streamm.ControlledStream
import java.util.concurrent.ConcurrentHashMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.PathVariable
import javax.servlet.http.HttpServletRequest
import ru.mm.surv.codecs.webm.util.stream.MeasuredInputStream
import ru.mm.surv.codecs.webm.incubator.streamm.StreamInput
import java.io.IOException
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.RequestParam
import ru.mm.surv.codecs.webm.util.stream.MeasuredOutputStream
import ru.mm.surv.codecs.webm.incubator.streamm.StreamClient
import java.io.UncheckedIOException

private const val MIME_TYPE_WEBM = "video/webm; codecs=\"vp8,vorbis\""
private const val STR_CONTENT_TYPE = "Content-type"
private const val STR_X_SEQUENCE = "X-Sequence"
private const val PACKET_SIZE = 24 * 1024

@RestController
@RequestMapping("stream/webm")
class WebmStreamController {

    private val log = LoggerFactory.getLogger(WebmStreamController::class.java)

    private val streams: MutableMap<String, ControlledStream> = ConcurrentHashMap()

    @PostMapping("publish/{streamId}")
    @ResponseBody
    fun publish(@PathVariable streamId: String, request: HttpServletRequest) {
        // stopping stream if already isRunning
        val oldStream = streams[streamId]
        oldStream?.stop()
        // creating new Sream instance
        val stream = ControlledStream(10)
        stream.mimeType = MIME_TYPE_WEBM
        // put stream to in the collection
        streams[streamId] = stream
        // generate transfer events to mesaure bandwidth usage
        val mis = MeasuredInputStream(request.inputStream, stream)
        // creating input reader
        val streamInput = StreamInput(stream, mis)
        /*
         * Start publishing
         */
        // this thread is working (will be blocked) while the stream is being published
        try {
            streamInput.run()
        } catch (e: IOException) {
            log.warn("Stream stopped: {}", e.message)
        }
        /*
         * End of publishing
         */
        // stopping stream (clients get notified)
        stream.stop()
    }

    @GetMapping(value = ["{streamId}/stream.webm"], produces = [MIME_TYPE_WEBM])
    fun consume(
        @PathVariable streamId: String, response: HttpServletResponse,
        @RequestParam(name = "sendHeader", defaultValue = "true") sendHeaderParam: String,
        @RequestParam(name = "fragmentSequence", defaultValue = "-1") fragmentSequenceParam: String,
        @RequestParam(name = "singleFragment", defaultValue = "false") singleFragmentParam: String
    ) {
        // getting stream
        val stream = streams[streamId]
        if (stream == null || !stream.isRunning) {
            throw HttpException(503, "Stream Not Running")
        }

        // setting rsponse content-type
        response.addHeader(STR_CONTENT_TYPE, stream.mimeType)

        // request GET parameters
        val sendHeader = !("0" == sendHeaderParam || "false" == sendHeaderParam)
        val fragmentSequence = fragmentSequenceParam.toInt(10)
        val singleFragment = !("0" == singleFragmentParam || "false" == singleFragmentParam)

        // setting rsponse sequence number (if needed)
        if (singleFragment) {
            val sequenceHeader = if (fragmentSequence > stream.fragmentAge) {
                fragmentSequence.toString()
            } else {
                stream.fragmentAge.toString()
            }
            response.addHeader(STR_X_SEQUENCE, sequenceHeader)
        }

        // check if there are free slots
        if (!stream.subscribe(false)) {
            throw HttpException(503, "Resource Busy")
        }

        // log transfer events (bandwidth usage)
        val mos = MeasuredOutputStream(response.outputStream, stream, PACKET_SIZE)

        // create a client
        val client = StreamClient(stream, mos)

        // whether to send header
        client.setSendHeader(sendHeader)

        // start output with the requested fragment
        if (fragmentSequence > 0) {
            client.setRequestedFragmentSequence(fragmentSequence)
        }

        // send only a single fragment
        client.setSendSingleFragment(singleFragment)

        // serving the request (from this thread)
        client.run()

        // freeing the slot
        stream.unsubscribe()
    }
}

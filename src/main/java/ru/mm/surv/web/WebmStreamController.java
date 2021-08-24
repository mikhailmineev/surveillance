package ru.mm.surv.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.mm.surv.codecs.webm.incubator.streamm.ControlledStream;
import ru.mm.surv.codecs.webm.incubator.streamm.StreamClient;
import ru.mm.surv.codecs.webm.incubator.streamm.StreamInput;
import ru.mm.surv.codecs.webm.util.stream.MeasuredInputStream;
import ru.mm.surv.codecs.webm.util.stream.MeasuredOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("stream/webm")
@Slf4j
public class WebmStreamController {

    private final String MIME_TYPE_WEBM = "video/webm; codecs=\"vp8,vorbis\"";
    private final String STR_CONTENT_TYPE = "Content-type";
    private final String STR_X_SEQUENCE = "X-Sequence";

    protected Map<String, ControlledStream> streams = new ConcurrentHashMap<>();

    @PostMapping("publish/{streamId}")
    @ResponseBody
    public void publish(@PathVariable String streamId, HttpServletRequest request) {
        try {
            // stopping stream if already isRunning
            ControlledStream stream = streams.get(streamId);
            if (stream != null) {
                stream.stop();
            }
            // creating new Sream instance
            stream = new ControlledStream(10);
            stream.setMimeType(MIME_TYPE_WEBM);
            // put stream to in the collection
            streams.put(streamId, stream);
            // generate transfer events to mesaure bandwidth usage
            MeasuredInputStream mis = new MeasuredInputStream(request.getInputStream(), stream);
            // creating input reader
            StreamInput streamInput = new StreamInput(stream, mis);
            /*
             * Start publishing
             */
            // this thread is working (will be blocked) while the stream is being published
            try {
                streamInput.run();
            } catch (IOException e) {
                log.warn("Stream stopped: {}", e.getMessage());
            }
            /*
             * End of publishing
             */
            // stopping stream (clients get notified)
            stream.stop();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @GetMapping(value = "{streamId}/stream.webm", produces = MIME_TYPE_WEBM)
    public void consume(@PathVariable String streamId, HttpServletResponse response,
                        @RequestParam(name = "sendHeader", defaultValue = "true") String sendHeaderParam,
                        @RequestParam(name = "fragmentSequence", defaultValue = "-1") String fragmentSequenceParam,
                        @RequestParam(name = "singleFragment", defaultValue = "false") String singleFragmentParam) {
        try {
            // getting stream
            ControlledStream stream = streams.get(streamId);
            if (stream == null || !stream.isRunning()) {
                throw new HttpException(503, "Stream Not Running");
            }

            // setting rsponse content-type
            response.addHeader(STR_CONTENT_TYPE, stream.getMimeType());

            // request GET parameters
            boolean sendHeader = !("0".equals(sendHeaderParam) || "false".equals(sendHeaderParam));
            int fragmentSequence = Integer.parseInt(fragmentSequenceParam, 10);
            boolean singleFragment = !("0".equals(singleFragmentParam) || "false".equals(singleFragmentParam));

            // setting rsponse sequence number (if needed)
            if (singleFragment) {
                String sequenceHeader;

                if (fragmentSequence > stream.getFragmentAge()) {
                    sequenceHeader = Integer.toString(fragmentSequence);
                } else {
                    sequenceHeader = Integer.toString(stream.getFragmentAge());
                }
                response.addHeader(STR_X_SEQUENCE, sequenceHeader);
            }

            // check if there are free slots
            if (!stream.subscribe(false)) {
                throw new HttpException(503, "Resource Busy");
            }

            // log transfer events (bandwidth usage)
            final int PACKET_SIZE = 24 * 1024;
            MeasuredOutputStream mos = new MeasuredOutputStream(response.getOutputStream(), stream, PACKET_SIZE);

            // create a client
            StreamClient client = new StreamClient(stream, mos);

            // whether to send header
            client.setSendHeader(sendHeader);

            // start output with the requested fragment
            if (fragmentSequence > 0) {
                client.setRequestedFragmentSequence(fragmentSequence);
            }

            // send only a single fragment
            client.setSendSingleFragment(singleFragment);

            // serving the request (from this thread)
            client.run();

            // freeing the slot
            stream.unsubscribe();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

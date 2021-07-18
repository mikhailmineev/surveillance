package ru.mm.surv.web;

import org.czentral.incubator.streamm.ControlledStream;
import org.czentral.incubator.streamm.StreamClient;
import org.czentral.incubator.streamm.StreamInput;
import org.czentral.util.stream.MeasuredInputStream;
import org.czentral.util.stream.MeasuredOutputStream;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class Web {

    private final String MIME_TYPE_WEBM = "video/webm; codecs=\"vp8,vorbis\"";
    private final String STR_CONTENT_TYPE = "Content-type";
    private final String STR_X_SEQUENCE = "X-Sequence";

    protected Map<String, ControlledStream> streams = new HashMap<>();

    @GetMapping
    public String mainPage(Model model) {
        return "video.html";
    }
    @ExceptionHandler({ HTTPException.class })
    public ResponseEntity<String> handleException(HTTPException e) {
        return ResponseEntity.status(e.getCode()).body(e.getMessage());
    }

    @PostMapping("publish/{streamID}")
    public void publish(@PathVariable String streamID, HttpServletRequest request) throws HTTPException, IOException {
        // stopping stream if already isRunning
        ControlledStream stream = streams.get(streamID);
        if (stream != null) {
            stream.stop();
        }
        // creating new Sream instance
        stream = new ControlledStream(10);
        stream.setMimeType(MIME_TYPE_WEBM);
        // put stream to in the collection
        streams.put(streamID, stream);
        // generate transfer events to mesaure bandwidth usage
        MeasuredInputStream mis = new MeasuredInputStream(request.getInputStream(), stream);
        // creating input reader
        StreamInput streamInput = new StreamInput(stream, mis);
        /*
         * Start publishing
         */
        // this thread is working (will be blocked) while the stream is being published
        streamInput.run();
        /*
         * End of publishing
         */
        // stopping stream (clients get notified)
        stream.stop();
    }

    @GetMapping("consume/{streamID}")
    public void consume(@PathVariable String streamID, HttpServletResponse response,
                        @RequestParam(name = "sendHeader", defaultValue = "true") String sendHeaderParam,
                        @RequestParam(name = "fragmentSequence", defaultValue = "-1") String fragmentSequenceParam,
                        @RequestParam(name = "singleFragment", defaultValue = "false") String singleFragmentParam
                        ) throws IOException, HTTPException {
        // getting stream
        ControlledStream stream = streams.get(streamID);
        if (stream == null || !stream.isRunning()) {
            throw new HTTPException(503, "Stream Not Running");
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
            throw new HTTPException(503, "Resource Busy");
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
    }
}

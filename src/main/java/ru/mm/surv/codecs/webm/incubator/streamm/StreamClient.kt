package ru.mm.surv.codecs.webm.incubator.streamm

import java.lang.RuntimeException
import java.io.OutputStream
import java.lang.Exception

class StreamClient(private val stream: Stream, private val output: OutputStream) {
    private var runs = true
    private var requestedFragmentSequence = -1
    private var sendSingleFragment = false

    fun setRequestedFragmentSequence(requestedFragmentSequence: Int) {
        this.requestedFragmentSequence = requestedFragmentSequence
    }

    fun setSendSingleFragment(sendSingleFragment: Boolean) {
        this.sendSingleFragment = sendSingleFragment
    }

    fun run() {
        output.use { output ->
            // report the starting of this client
            stream.postEvent(ServerEvent(ServerEvent.CLIET_START))

            // waiting for header
            var header: ByteArray? = null
            while (header == null) {
                if (stream.isRunning) {
                    return
                }
                Thread.sleep(200)
                header = stream.header
            }

            // writing header
            try {
                output.write(header)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

            /* Web browsers may try to detect some features of the server and open
             * multiple connections. They may close the connection eventually so
             * we wait for a little bit to ensure this request is serious. (And the
             * client needs more than just the header).
             */Thread.sleep(1000)

            // sending fragments
            var nextSequence = requestedFragmentSequence
            while (runs && stream.isRunning) {
                var fragmentSent = false

                // while there is a new fragment is available
                var streamAge = stream.fragmentAge
                while (runs
                        && stream.isRunning
                        && nextSequence <= stream.fragmentAge.also { streamAge = it }) {

                    // notification if a fragment was skipped
                    if (nextSequence > 0 && streamAge - nextSequence > 1) stream.postEvent(ServerEvent(ServerEvent.CLIET_FRAGMENT_SKIP))
                    nextSequence = streamAge + 1

                    // getting current movie fragment
                    val fragment = stream.fragment

                    // send the fragment data to the client
                    try {
                        val buffers = fragment!!.getBuffers()
                        for (buffer in buffers) {

                            // writing data packet
                            output.write(buffer.data, buffer.offset, buffer.length)
                        }
                        fragmentSent = true

                        // only one fragment requested:
                        if (sendSingleFragment) {
                            runs = false
                        }
                    } catch (e: Exception) {

                        // closed connection
                        runs = false
                    }
                }

                // currently no new fragment, sleeping 200 ms
                if (!fragmentSent) {
                    Thread.sleep(200)
                }
            }

            // report the stopping of thes client
            stream.postEvent(ServerEvent(ServerEvent.CLIET_STOP))
        }
    }
}
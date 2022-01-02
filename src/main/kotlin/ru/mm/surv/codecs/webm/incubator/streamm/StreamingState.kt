package ru.mm.surv.codecs.webm.incubator.streamm

import ru.mm.surv.codecs.webm.incubator.streamm.EBMLElement.Companion.loadUnsigned
import java.lang.RuntimeException
import ru.mm.surv.codecs.webm.util.stream.Processor

internal class StreamingState(
    private val stream: Stream,
    private val videoTrackNumber: Long,
) : Processor {
    private var clusterTimeCode: Long = 0
    private var fragment: MatroskaFragment

    override fun process(buffer: ByteArray, offset: Int, length: Int): Int {
        var offset = offset
        val endOffset = offset + length
        val startOffset = offset
        var elem: EBMLElement
        while (offset < endOffset - 12) {
            elem = EBMLElement(buffer, offset, length)

            // clusters do not need to be fully loaded, so that is an exception
            /* Note: cluster check was moved to be the first because of the
             * possibility of infinite clusters (gstreamer's curlsink?).
             */if (elem.id != ID_CLUSTER && elem.getEndOffset() > endOffset) {

                /* The element is not fully loaded: we need more data, so we end
                 * this processing cycle. The StreamInput will fill the buffer
                 * and take care of the yet unprocessed element. We signal this
                 * by sending the element's offset back. The StreamInput will
                 * keep the data beyound this offset and the next processing cycle
                 * will begin with this element.
                 */
                return elem.elementOffset - startOffset
            }

            /* Timecode for this cluster. We use a flat processing model (we do
             * not care about the nesting of elements), so this timecode will
             * live until we get the next one. It will be the Timecode element
             * of the next Cluster (according to standard).
             */if (elem.id == ID_TIMECODE) {

                // we have the timecode, so open a new cluster in our movie fragment
                clusterTimeCode = loadUnsigned(buffer, elem.dataOffset, elem.dataSize.toInt())
                //System.out.println("tc: " + clusterTimeCode);

                // cluster opened
                fragment.openCluster(clusterTimeCode)
            } else if (elem.id == ID_SIMPLEBLOCK) {

                // sample (video or audio) recieved
                var trackNum: Int = buffer[elem.dataOffset].toInt() and 0xff
                if (trackNum and 0x80 == 0) throw RuntimeException("Track numbers > 127 are not implemented.")
                trackNum = trackNum xor 0x80

                // check if this is a video frame
                if (trackNum.toLong() == videoTrackNumber) {
                    //DEBUG System.out.print("video ");
                    val flags: Int = buffer[elem.dataOffset + 3].toInt() and 0xff
                    if (flags and 0x80 != 0) {
                        // keyframe

                        //DEBUG System.out.print("key ");
                        if (fragment.length() >= MovieFragment.LIMIT_FRAME_MINIMUM) {

                            // closing current cluster (of the curent fragment)
                            fragment.closeCluster()

                            // send the complete fragment to the stream coordinator
                            stream.pushFragment(fragment)

                            // create a new fragment
                            fragment = MatroskaFragment()

                            // set up new fragment's timecode
                            fragment.openCluster(clusterTimeCode)

                            // notification about starting the input process
                            stream.postEvent(ServerEvent(ServerEvent.INPUT_FRAGMENT_START))
                            if (flags and 0x60 != 0) {
                                throw RuntimeException("Lacing is not yet supported.")
                            }
                        }
                    }
                }

                // saving the block
                fragment.appendKeyBlock(buffer, elem.elementOffset, elem.getElementSize())
            } else if (elem.id == ID_BLOCKGROUP) {

                // append the BlockGroup to the current fragment
                fragment.appendBlock(buffer, elem.elementOffset, elem.getElementSize())
            }
            offset =
                if (elem.id == ID_CLUSTER || elem.dataSize >= 0x100000000L) {
                    elem.dataOffset
                } else {
                    elem.getEndOffset()
                }
        }
        return offset - startOffset
    }

    override fun finished(): Boolean {
        return false
    }

    companion object {
        private const val ID_CLUSTER: Long = 0x1F43B675
        private const val ID_SIMPLEBLOCK: Long = 0xA3
        private const val ID_BLOCKGROUP: Long = 0xA0
        private const val ID_TIMECODE: Long = 0xE7
    }

    init {
        fragment = MatroskaFragment()
    }
}

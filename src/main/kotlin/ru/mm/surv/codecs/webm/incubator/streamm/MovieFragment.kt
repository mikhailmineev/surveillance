package ru.mm.surv.codecs.webm.incubator.streamm

import ru.mm.surv.codecs.webm.util.stream.Buffer

/**
 *
 * @author Varga Bence (vbence@czentral.org)
 */
interface MovieFragment {
    fun getBuffers(): Array<Buffer>
    fun length(): Int

    companion object {
        const val LIMIT_FRAME_MINIMUM = 100 * 1024
        const val LIMIT_FRAME_MAXIMUM = 2048 * 1024 + 64 * 1024
    }
}
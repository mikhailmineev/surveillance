package ru.mm.surv.codecs.webm.util.stream

import ru.mm.surv.codecs.webm.event.EventImpl

class TransferEvent(type: Int) : EventImpl(type) {
    companion object {
        const val STREAM_INPUT = 1
        const val STREAM_OUTPUT = 2
    }
}
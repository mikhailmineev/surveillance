package ru.mm.surv.codecs.webm.incubator.streamm

import ru.mm.surv.codecs.webm.event.EventImpl

class ServerEvent(type: Int) : EventImpl(type) {

    override fun toString(): String {
        return "{cls:1, type:" +
                getType() +
                ",time:" +
                getDate().time +
                "},"
    }

    companion object {
        const val INPUT_START = 1
        const val INPUT_STOP = 2
        const val INPUT_FRAGMENT_START = 3
        const val INPUT_FIRST_FRAGMENT = 5
        const val CLIET_START = 1001
        const val CLIET_STOP = 1002
        const val CLIET_FRAGMENT_SKIP = 1005
    }
}
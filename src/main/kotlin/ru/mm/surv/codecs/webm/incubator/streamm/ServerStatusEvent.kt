package ru.mm.surv.codecs.webm.incubator.streamm

import ru.mm.surv.codecs.webm.event.EventImpl
import java.util.*

class ServerStatusEvent : EventImpl(DEFAULT_TYPE, Date()) {
    var clientCount = -1

    override fun toString(): String {
        return "{cls:4, clientCount:" + clientCount + ", clientLimit:" + -1 + ",time:" + getDate().time + "},"
    }

    companion object {
        const val DEFAULT_TYPE = 1
    }
}
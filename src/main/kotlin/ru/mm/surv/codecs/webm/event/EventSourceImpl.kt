package ru.mm.surv.codecs.webm.event

open class EventSourceImpl : EventSource {
    override fun postEvent(event: Event): Boolean {
        return false
    }
}
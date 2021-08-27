package ru.mm.surv.codecs.webm.event

interface EventSource {
    fun postEvent(event: Event): Boolean
}

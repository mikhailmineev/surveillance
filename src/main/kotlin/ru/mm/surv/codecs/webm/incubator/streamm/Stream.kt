package ru.mm.surv.codecs.webm.incubator.streamm

import ru.mm.surv.codecs.webm.event.EventSourceImpl

open class Stream : EventSourceImpl() {
    @get:Synchronized
    var fragment: MovieFragment? = null
        private set

    @get:Synchronized
    var fragmentAge = 0
        private set

    @get:Synchronized
    @set:Synchronized
    var header: ByteArray? = null
    var mimeType = "application/octet-stream"

    @get:Synchronized
    var isRunning = true
        private set

    @Synchronized
    fun stop() {
        isRunning = false
    }

    @Synchronized
    fun pushFragment(newFragment: MovieFragment?) {
        if (fragmentAge == 0) postEvent(ServerEvent(ServerEvent.INPUT_FIRST_FRAGMENT))
        fragment = newFragment
        fragmentAge++
    }
}
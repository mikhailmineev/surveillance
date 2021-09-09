package ru.mm.surv.codecs.webm.incubator.streamm

class ControlledStream(private val maxClients: Int) : Stream() {
    private var numClients = 0

    fun subscribe(force: Boolean): Boolean {
        if (force || numClients < maxClients) {
            numClients++
            refreshStatus()
            return true
        }
        return false
    }

    fun unsubscribe() {
        numClients--
        refreshStatus()
    }

    private fun refreshStatus() {
        val event = ServerStatusEvent()
        event.clientCount = numClients
        postEvent(event)
    }
}
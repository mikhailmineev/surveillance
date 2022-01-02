package ru.mm.surv.codecs.webm.event

import java.util.*

/**
 * Interface for general-purpose events.
 */
interface Event {
    /**
     * Gets the type of the event. Implementing classes should define the possible
     * types. Event handlers should check for the class of the event object
     * (e.g. with instanceOf operator), then check for the type.
     *
     * @return Event type (specified by implementing classes).
     */
    fun getType(): Int

    /**
     * Gets the time when this event occurred.
     *
     * @return Date object representing the time of the event.
     */
    fun getDate(): Date
}

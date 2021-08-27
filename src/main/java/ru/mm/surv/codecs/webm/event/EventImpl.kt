package ru.mm.surv.codecs.webm.event

import java.util.*

abstract class EventImpl(
    private val type: Int,
    private val date: Date = Date()
) : Event {
    constructor(type: Int) : this(type, Date())

    override fun getType(): Int {
        return type
    }

    override fun getDate(): Date {
        return date
    }
}

package ru.mm.surv.capture.ffmpeg.executor

import java.lang.Process
import java.lang.Runnable
import java.util.function.Consumer

class LivenessChecker(
    private val process: Process,
    private val action: Consumer<Process>) : Runnable {

    override fun run() {
        if (!process.isAlive) {
            action.accept(process)
        }
    }
}

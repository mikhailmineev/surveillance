package ru.mm.surv.capture.config

import java.util.regex.Pattern

object InputFormatFactory {
    private val INPUT_FORMAT_WIN_PATTERN =
        Pattern.compile(".*min s=(\\d+)x(\\d+) fps=(\\d+) max s=(\\d+)x(\\d+) fps=(\\d+)")

    fun fromWinLine(line: String): InputFormat {
        val m = INPUT_FORMAT_WIN_PATTERN.matcher(line)
        require(m.find()) { "Can't parse line $line" }
        val hmax = m.group(4)
        val vmax = m.group(5)
        val resmax = hmax + "x" + vmax
        val fmax = m.group(6)
        return InputFormat(resmax, fmax)
    }

}

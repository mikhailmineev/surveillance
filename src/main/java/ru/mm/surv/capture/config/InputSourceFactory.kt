package ru.mm.surv.capture.config

import java.util.regex.Pattern

object InputSourceFactory {
    private val MAC_PATTERN = Pattern.compile("\\[.*] \\[(\\d)] (.*)")
    private val WIN_PATTERN = Pattern.compile("\\[.*]  \"(.*)\"")

    fun fromMacLine(inputType: InputType, line: String): InputSource {
        val m = MAC_PATTERN.matcher(line)
        require(m.find()) { "Can't parse line $line" }
        val id = m.group(1)
        val name = m.group(2)
        return InputSource(inputType, id, name, emptyList())
    }

    fun fromWinLine(inputType: InputType, line: String): InputSource {
        val m = WIN_PATTERN.matcher(line)
        require(m.find()) { "Can't parse line $line" }
        val id = m.group(1)
        return InputSource(inputType, id, id, emptyList())
    }
}

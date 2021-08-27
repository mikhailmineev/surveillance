package ru.mm.surv.capture.config

import org.apache.commons.lang3.SystemUtils
import java.lang.RuntimeException

object CurrentPlatform {
    fun get(): Platform {
        return when {
            SystemUtils.IS_OS_WINDOWS -> {
                Platform.WIN
            }
            SystemUtils.IS_OS_MAC -> {
                Platform.MACOS
            }
            else -> {
                throw RuntimeException("Only Windows, MacOS supported")
            }
        }
    }
}

package ru.mm.surv.web

import java.lang.RuntimeException

class HttpException(val code: Int, msg: String) : RuntimeException(msg)

package ru.mm.surv.web

import org.springframework.web.bind.annotation.ControllerAdvice
import lombok.extern.slf4j.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
@Slf4j
class GlobalExceptionHandler {

    @ExceptionHandler(HttpException::class)
    fun handleException(e: HttpException): ResponseEntity<String> {
        return ResponseEntity.status(e.code).body(e.message)
    }
}

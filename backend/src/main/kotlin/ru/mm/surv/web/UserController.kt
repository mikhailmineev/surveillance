package ru.mm.surv.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    @GetMapping("user")
    fun currentUser(): String {
        // TODO #22 Refactor after token auth implemented
        return """{"role":"ADMIN"}"""
    }
}

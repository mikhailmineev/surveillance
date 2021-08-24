package ru.mm.surv

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Surveillance

fun main(args: Array<String>) {
    runApplication<Surveillance>(*args)
}


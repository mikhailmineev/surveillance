package ru.mm.surv

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@ConfigurationPropertiesScan
class Surveillance

fun main(args: Array<String>) {
    runApplication<Surveillance>(*args)
}


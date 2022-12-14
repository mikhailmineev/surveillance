package ru.mm.surv.capture.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class AsyncConfig {

    @Bean
    fun taskScheduler(): TaskScheduler {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.poolSize = 1
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler")
        return threadPoolTaskScheduler
    }
}

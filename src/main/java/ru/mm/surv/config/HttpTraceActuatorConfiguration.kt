package ru.mm.surv.config

import org.springframework.boot.actuate.audit.AuditEventRepository
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository
import org.springframework.boot.actuate.trace.http.HttpTraceRepository
import org.springframework.boot.actuate.trace.http.HttpExchangeTracer
import org.springframework.boot.actuate.web.trace.servlet.HttpTraceFilter
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private const val ORDER_BEFORE_SECURITY_FILTER = -102

@Configuration
class HttpTraceActuatorConfiguration {

    @Bean
    fun auditEventRepository(): AuditEventRepository {
        return InMemoryAuditEventRepository()
    }

    @Bean
    fun httpTraceFilter(repository: HttpTraceRepository, tracer: HttpExchangeTracer): HttpTraceFilter {
        val httpTraceFilter = HttpTraceFilterWithPrincipal(repository, tracer)
        httpTraceFilter.order = ORDER_BEFORE_SECURITY_FILTER
        return httpTraceFilter
    }

    @Bean
    fun httpTraceRepository(): HttpTraceRepository {
        return InMemoryHttpTraceRepository()
    }
}

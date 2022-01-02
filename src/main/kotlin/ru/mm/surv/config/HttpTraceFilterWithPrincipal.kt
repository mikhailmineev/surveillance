package ru.mm.surv.config

import org.springframework.boot.actuate.trace.http.HttpTraceRepository
import org.springframework.boot.actuate.trace.http.HttpExchangeTracer
import org.springframework.boot.actuate.web.trace.servlet.HttpTraceFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.FilterChain

class HttpTraceFilterWithPrincipal(repository: HttpTraceRepository, tracer: HttpExchangeTracer) :
    HttpTraceFilter(repository, tracer) {

    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val wrapped = HttpServletRequestWrapperWithPrincipal(request)
        super.doFilterInternal(wrapped, response, filterChain)
    }
}

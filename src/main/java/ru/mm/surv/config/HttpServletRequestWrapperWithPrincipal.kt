package ru.mm.surv.config

import com.sun.security.auth.UserPrincipal
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.core.context.SecurityContextImpl
import java.security.Principal

class HttpServletRequestWrapperWithPrincipal(private val request: HttpServletRequest) :
    HttpServletRequestWrapper(
        request
    ) {

    override fun getUserPrincipal(): Principal? {
        return super.getUserPrincipal()
            ?:userPrincipalFromSession()
    }

    private fun userPrincipalFromSession(): Principal? {
        return request.getSession(false)
            ?.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)
            ?.let { it as? SecurityContextImpl }
            ?.authentication
            ?.principal
            ?.let { it as? ru.mm.surv.config.User }
            ?.username
            ?.let(::UserPrincipal)
    }
}

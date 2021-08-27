package ru.mm.surv.config;

import com.sun.security.auth.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.security.Principal;
import java.util.Optional;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

class HttpServletRequestWrapperWithPrincipal extends HttpServletRequestWrapper {

    private final HttpServletRequest request;

    public HttpServletRequestWrapperWithPrincipal(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public Principal getUserPrincipal() {
        return Optional
                .ofNullable(super.getUserPrincipal())
                .or(this::getUserPrincipalFromSession)
                .orElse(null);
    }

    private Optional<Principal> getUserPrincipalFromSession() {
        return Optional
                .ofNullable(request.getSession(false))
                .map(e -> e.getAttribute(SPRING_SECURITY_CONTEXT_KEY))
                .filter(e -> e instanceof SecurityContextImpl)
                .map(e -> (SecurityContextImpl) e)
                .map(SecurityContextImpl::getAuthentication)
                .map(Authentication::getPrincipal)
                .filter(e -> e instanceof User)
                .map(e -> (User) e)
                .map(User::getUsername)
                .map(UserPrincipal::new);
    }
}

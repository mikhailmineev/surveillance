package ru.mm.surv.config;

import com.sun.security.auth.UserPrincipal;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.security.Principal;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

class HttpServletRequestWrapperWithPrincipal extends HttpServletRequestWrapper {

    private final HttpServletRequest request;

    public HttpServletRequestWrapperWithPrincipal(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public Principal getUserPrincipal() {
        var userPrincipal = super.getUserPrincipal();
        if (userPrincipal != null) {
            return userPrincipal;
        }
        var attribute = request.getSession().getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        if (attribute instanceof SecurityContextImpl) {
            var context = (SecurityContextImpl) attribute;
            var principal = context.getAuthentication().getPrincipal();
            if (principal instanceof User) {
                var user = (User) principal;
                return new UserPrincipal(user.getUsername());
            }
        }
        return null;
    }
}

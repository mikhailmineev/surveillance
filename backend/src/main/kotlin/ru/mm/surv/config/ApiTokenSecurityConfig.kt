package ru.mm.surv.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.web.client.RestTemplate

@Configuration
@Order(2)
class ApiTokenSecurityConfig @Autowired constructor(
    private val users: Users,
    private val passwordEncoder: PasswordEncoder,
    private val restTemplate: RestTemplate,
    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") private val jwkSetUrl: String
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        configureUsers(auth, users, passwordEncoder)
    }

    override fun configure(http: HttpSecurity) {
        http
            .csrf()
            .disable()
            .authorizeRequests()
            .antMatchers("/actuator/**").hasRole(UserRole.ADMIN.toString())
            .antMatchers("/configure/**").hasRole(UserRole.ADMIN.toString())
            .antMatchers("/stream/control/**").hasRole(UserRole.ADMIN.toString())
            .antMatchers("/stream/**").hasRole(UserRole.CONSUMER.toString())
            .antMatchers("/record/**").hasRole(UserRole.CONSUMER.toString())
            .antMatchers("/system/**").hasRole(UserRole.CONSUMER.toString())
            .anyRequest().authenticated()
            .and()
            .formLogin().disable()
            .logout().disable()
            .oauth2ResourceServer {
                it.jwt {
                    it.decoder(jwtDecoder())
                    it.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }
    }

    @Bean
    /*
     * Overriding default decoder only to pass custom restTemplate
     * Have to set jwks uri because it can use custom restTemplate, but well-known param can`t
     */
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUrl).restOperations(restTemplate).build()
    }

    @Bean
    /*
     * Overriding converter because need to fetch user roles
     * It is impossible to use scopes as user roles, they restrict only clients
     */
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles")
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_")

        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
        return jwtAuthenticationConverter
    }

}

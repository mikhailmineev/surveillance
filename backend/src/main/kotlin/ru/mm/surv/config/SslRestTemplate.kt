package ru.mm.surv.config

import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContextBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.web.client.RestTemplate
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import java.io.File
import javax.net.ssl.SSLContext

@Configuration
class SslRestTemplate(
    @Value("\${client.ssl.trust-store}") val trustStore: Resource,
    @Value("\${client.ssl.trust-store-password}") val trustStorePassword: String
) {

    @Bean
    fun restTemplateWithTrustStore(builder: RestTemplateBuilder): RestTemplate {
        val sslContext: SSLContext = SSLContextBuilder()
            .loadTrustMaterial(File("keys/keystore.jks"), trustStorePassword.toCharArray())
            .build()
        val socketFactory = SSLConnectionSocketFactory(sslContext)
        val httpClient = HttpClients.custom()
            .setSSLSocketFactory(socketFactory)
            .build()
        return builder
            .requestFactory { HttpComponentsClientHttpRequestFactory(httpClient) }
            .build()
    }
}

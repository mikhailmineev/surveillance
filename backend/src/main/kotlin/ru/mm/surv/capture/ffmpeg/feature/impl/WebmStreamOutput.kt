package ru.mm.surv.capture.ffmpeg.feature.impl

import org.springframework.http.HttpHeaders
import ru.mm.surv.capture.config.CameraConfig
import ru.mm.surv.capture.ffmpeg.feature.Feature
import ru.mm.surv.config.User
import java.nio.charset.Charset
import java.util.*

class WebmStreamOutput(
    private val captureConfig: CameraConfig,
    private val user: User
    ): Feature {

    private fun createWebmAuthorization(): String {
        val basicCredentials = user.username + ":" + user.password
        val basicCredentialBytes = basicCredentials.toByteArray(Charset.defaultCharset())
        val base64EncodedCredentials = Base64.getEncoder().encodeToString(basicCredentialBytes)
        return "${HttpHeaders.AUTHORIZATION}: Basic $base64EncodedCredentials\r\n"
    }

    override fun args(): Array<String> {
        val webmAuthorization = createWebmAuthorization()
        val webmPublishUrl = "https://127.0.0.1:8444/api/stream/webm/publish/" + captureConfig.name
        return arrayOf(
            "-f", "webm",
            "-headers", webmAuthorization,
            webmPublishUrl
        )
    }


}

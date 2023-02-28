package rubber.dutch.hat.apigateway.events.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.rsocket.metadata.WellKnownMimeType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.util.MimeTypeUtils
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

@Configuration
class RsocketConfig {

    @Bean
    fun rsocketStrategies(objectMapper: ObjectMapper): RSocketStrategies {
        return RSocketStrategies.builder()
            .decoder(Jackson2JsonDecoder(objectMapper))
            .encoder(Jackson2JsonEncoder(objectMapper))
            .metadataExtractorRegistry { registry ->
                registry.metadataToExtract(
                    MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string),
                    ByteBuffer::class.java
                ) { parameter, metadataMap ->
                    val token = StandardCharsets.UTF_8.decode(parameter.position(PAYLOAD_POSITION)).toString()
                    metadataMap[ACCESS_TOKEN_HEADER] = token
                }
            }
            .build()
    }

    companion object {
        /**
         * ПервыЙ байт - это служебная информация, а дальше токен.
         * См. https://github.com/rsocket/rsocket/blob/master/Extensions/Security/Authentication.md
         */
        const val PAYLOAD_POSITION = 1
        const val ACCESS_TOKEN_HEADER = "access_token"
    }
}

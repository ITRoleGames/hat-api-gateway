package rubber.dutch.hat.apigateway.events.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.rsocket.metadata.WellKnownMimeType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.util.MimeTypeUtils

@Configuration
class RsocketConfig(private val objectMapper: ObjectMapper) {

    @Bean
    fun rsocketMessageHandler(rSocketStrategies: RSocketStrategies): RSocketMessageHandler? {
        val handler = RSocketMessageHandler()
        handler.rSocketStrategies = rsocketStrategies()
        return handler
    }

    @Bean
    fun rsocketStrategies(): RSocketStrategies {

        return RSocketStrategies.builder()
            .decoder(Jackson2JsonDecoder(objectMapper))
            .encoder(Jackson2JsonEncoder(objectMapper))
            .metadataExtractorRegistry { registry ->
                registry.metadataToExtract(
                    MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string),
                    String::class.java
                ) { parameter, metadataMap ->
                    metadataMap[ACCESS_TOKEN_HEADER] = parameter.substring(NON_PAYLOAD_DATA_LENGTH)
                }
            }
            .build()
    }

    companion object {
        /**
         * Первые 4 байта - это служебная информация.
         * См. https://github.com/rsocket/rsocket/blob/master/Extensions/Security/Authentication.md
         */
        const val NON_PAYLOAD_DATA_LENGTH = 1
        const val ACCESS_TOKEN_HEADER = "access_token"
    }
}

package rubber.dutch.hat.apigateway.events.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.messaging.rsocket.RSocketStrategies

@Configuration
class RsocketConfig {

    @Bean
    fun rsocketStrategies(objectMapper: ObjectMapper): RSocketStrategies {
        return RSocketStrategies.builder()
            .decoder(Jackson2JsonDecoder(objectMapper))
            .encoder(Jackson2JsonEncoder(objectMapper))
            .build()
    }
}

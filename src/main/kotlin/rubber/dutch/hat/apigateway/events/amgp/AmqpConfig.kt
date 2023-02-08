package rubber.dutch.hat.apigateway.events.amgp

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AmqpConfig {

    companion object {
        const val QUEUE_NAME = "game-update-event"
    }

    @Bean
    fun jackson2MessageConverter(objectMapper : ObjectMapper): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter(objectMapper)
    }
}


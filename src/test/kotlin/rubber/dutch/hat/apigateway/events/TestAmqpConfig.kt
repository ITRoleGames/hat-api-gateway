package rubber.dutch.hat.apigateway.events

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import rubber.dutch.hat.apigateway.events.amqp.AmqpConfig.Companion.GAME_EVENT_QUEUE_NAME

@Configuration
class TestAmqpConfig {

    @Bean
    fun queue(): Queue {
        return Queue(GAME_EVENT_QUEUE_NAME, true)
    }
}

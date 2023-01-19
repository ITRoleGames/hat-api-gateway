package rubber.dutch.hat.apigateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerAdapter
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
class WebSocketConfig(private val webSocketHandler: WebSocketHandler) {

    @Bean
    fun wsHandlerAdapter(): HandlerAdapter? {
        return WebSocketHandlerAdapter()
    }

    @Bean
    fun handlerMapping(): HandlerMapping {
        val path = "/push/{id}"
        val map = mapOf(path to webSocketHandler)
        return SimpleUrlHandlerMapping(map, -1)
    }
}

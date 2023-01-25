package rubber.dutch.hat.apigateway.controller

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import rubber.dutch.hat.apigateway.service.EventUnicastService

@Component
class WebSocketHandler(private val eventUnicastService: EventUnicastService,
                       private val objectMapper: ObjectMapper) : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        val messages: Flux<WebSocketMessage> = session.receive()
            .flatMap { message ->
                print(message)
                return@flatMap eventUnicastService.getMessages()
            }
            .filter { o -> session.handshakeInfo.uri.path?.contains("game/${o.receiverId}") ?: false }
            .flatMap { o ->
                try {
                    return@flatMap Mono.just(objectMapper.writeValueAsString(o))
                } catch (e: JsonProcessingException) {
                    return@flatMap Mono.error(e)
                }
            }.map(session::textMessage)


        return session.send(messages)
    }
}

package rubber.dutch.hat.apigateway.events

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import rubber.dutch.hat.game.api.GameUpdatedEvent
import java.util.concurrent.ConcurrentHashMap

@Service
class GameEventMulticastService {

    private val events = ConcurrentHashMap<String, MutableMap<String, Sinks.Many<GameUpdatedEvent>>>()

    fun onNext(event: GameUpdatedEvent) {
        events.getOrPut(event.gameId.toString()) { ConcurrentHashMap() }
            .values.forEach {
                it.tryEmitNext(event)
            }
    }

    fun onSubscribe(gameId: String, userId: String) {
        events.getOrPut(gameId) { ConcurrentHashMap() }
            .putIfAbsent(userId, createSink())
    }

    fun onUnsubscribe(gameId: String, userId: String) {
        events.getOrDefault(gameId, mutableMapOf()).remove(userId)
    }

    fun get(gameId: String, userId: String): Flux<GameUpdatedEvent> {
        return events.getOrPut(gameId) { ConcurrentHashMap() }
            .getOrPut(userId) { createSink() }
            .asFlux()
    }

    private fun createSink(): Sinks.Many<GameUpdatedEvent> {
        return Sinks.many().replay().limit(1)
    }
}

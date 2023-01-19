package rubber.dutch.hat.apigateway.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import rubber.dutch.hat.apigateway.model.event.Event
import java.util.concurrent.atomic.AtomicInteger

@Component
class EventGenerator(private val eventUnicastService: EventUnicastService) {

    val counter = AtomicInteger(0)

    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    fun generateEvent(){
        val count = counter.getAndIncrement()
        val event = Event(name= "test", count = count)
        eventUnicastService.onNext(event)
    }
}

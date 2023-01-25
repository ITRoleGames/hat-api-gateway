package rubber.dutch.hat.apigateway.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import rubber.dutch.hat.apigateway.model.event.Event
import java.util.concurrent.atomic.AtomicInteger

@Component
class EventGenerator(private val eventUnicastService: EventUnicastService) {

    val counter = AtomicInteger(0)
    val counter2 = AtomicInteger(0)

//    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    fun generateEvent() {
        val count = counter.getAndIncrement()
        val count2 = counter2.getAndDecrement()
        val event = Event(receiverId = 1, name = "test", count = count)
        val event2 = Event(receiverId = 2, name = "test", count = count2)
        eventUnicastService.onNext(event)
        eventUnicastService.onNext(event2)
    }
}

package rubber.dutch.hat.apigateway.events.model

import java.util.*

data class GameUpdatedEvent(val gameId: UUID, val type : GameEventType)

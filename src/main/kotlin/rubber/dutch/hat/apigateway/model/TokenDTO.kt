package rubber.dutch.hat.apigateway.model

import java.util.UUID

data class TokenDTO(val token: String, val expired: Boolean, val userId: UUID?)

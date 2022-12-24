package rubber.dutch.hat.apigateway.model

import java.util.*

data class TokenDTO(val token: String, val expired: Boolean, val userId: UUID?)

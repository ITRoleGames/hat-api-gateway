package rubber.dutch.hat.apigateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
/**
 * Конфигурация сервиса пользователей.
 */

@ConfigurationProperties("hat.user-service")
data class UserServiceConfig @ConstructorBinding constructor(val host: String, val tokenUrlPath: String)

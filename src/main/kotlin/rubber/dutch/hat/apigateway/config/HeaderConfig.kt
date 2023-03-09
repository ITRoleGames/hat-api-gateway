package rubber.dutch.hat.apigateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
/**
 * Конфигурация заголовков запроса
 */
@ConfigurationProperties("application.headers")
data class HeaderConfig @ConstructorBinding constructor(val authorization: String, val userId: String)

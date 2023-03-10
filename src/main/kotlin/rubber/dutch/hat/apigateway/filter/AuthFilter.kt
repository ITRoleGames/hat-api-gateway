package rubber.dutch.hat.apigateway.filter

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import rubber.dutch.hat.apigateway.config.HeaderConfig
import rubber.dutch.hat.apigateway.model.TokenDTO
import rubber.dutch.hat.apigateway.service.AuthService
import rubber.dutch.hat.apigateway.service.UnsecureRouteService

/**
 * Фильтр сессии. Проверяет наличие Authorization-токена.
 */
@Component
@Order(-1)
class AuthFilter(
    private val headerConfig: HeaderConfig,
    private val authService: AuthService,
    private val unsecureRouteService: UnsecureRouteService
) : GlobalFilter, Ordered {

    companion object {
        /**
         * Длина префикса "Bearer", после которого в заголовке запроса следует токен.
         */
        const val BEARER_PREFIX_LENGTH = 7
    }

    override fun filter(serverWebExchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        if (unsecureRouteService.isUnsecure(serverWebExchange)) return chain.filter(serverWebExchange)

        val token = getTokenFromHeader(serverWebExchange)

        return authService.validate(token)
            .flatMap { chain.filter(addSessionHeader(serverWebExchange, it)) }
            .onErrorResume(RuntimeException::class.java) { handleUnauthorized(serverWebExchange) }
    }

    private fun handleUnauthorized(exchange: ServerWebExchange): Mono<Void> {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        return exchange.response.setComplete()
    }

    private fun getTokenFromHeader(exchange: ServerWebExchange): String? {
        return exchange.request.headers.getFirst(headerConfig.authorization)?.substring(BEARER_PREFIX_LENGTH)
    }

    fun addSessionHeader(exchange: ServerWebExchange, tokenData: TokenDTO): ServerWebExchange {
        if (tokenData.expired) {
            error("Token expired")
        }

        val mutatedRequest = exchange.request.mutate().header(headerConfig.userId, tokenData.userId.toString()).build()

        return exchange.mutate().request(mutatedRequest).build()
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE
    }
}

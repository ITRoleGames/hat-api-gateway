package rubber.dutch.hat.apigateway.filter

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import rubber.dutch.hat.apigateway.service.SessionService
import rubber.dutch.hat.apigateway.service.UnsecureRouteService

/**
 * <p>Фильтр сессии. Проверяет наличие Authorization-токена.</p>
 */
@Component
class SessionFilter(val sessionService: SessionService, val unsecureRouteService: UnsecureRouteService) : GlobalFilter {

    override fun filter(serverWebExchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        if (unsecureRouteService.isUnsecure(serverWebExchange)) return chain.filter(serverWebExchange)

        return sessionService.validate(serverWebExchange)
            .flatMap { exchange -> chain.filter(exchange) }
            .onErrorResume(RuntimeException::class.java) { handleUnauthorized(serverWebExchange) }
    }

    private fun handleUnauthorized(exchange: ServerWebExchange): Mono<Void> {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        return exchange.response.setComplete()
    }
}

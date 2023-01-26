package rubber.dutch.hat.apigateway.service

import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.server.ServerWebExchange
import rubber.dutch.hat.apigateway.config.SessionFilterConfig
import rubber.dutch.hat.apigateway.model.Route

@Component
class UnsecureRouteService(private val sessionFilterConfig: SessionFilterConfig) {
    companion object {
        private const val ALL_HTTP_METHODS = "all"
    }

    fun isUnsecure(serverWebExchange: ServerWebExchange): Boolean {
        return sessionFilterConfig.unsecureRoutes.any { matchRoute(it, serverWebExchange) }
    }

    private fun matchRoute(route: Route, serverWebExchange: ServerWebExchange): Boolean {
        val path = serverWebExchange.request.path.value()
        val method = serverWebExchange.request.method.name()

        val matchPath = AntPathMatcher().match(route.path, path)
        val matchMethod = route.method == ALL_HTTP_METHODS || route.method == method

        return matchPath && matchMethod
    }
}

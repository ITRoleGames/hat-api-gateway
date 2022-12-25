package rubber.dutch.hat.apigateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import rubber.dutch.hat.apigateway.model.Route

@ConfigurationProperties("hat.session-filter")
data class SessionFilterConfig @ConstructorBinding constructor(val unsecureRoutes: List<Route>)

package rubber.dutch.hat.apigateway.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class VersionController(@Value("\${application.version}") private val version: String) {

    @GetMapping("/version")
    fun getVersion(): VersionInfo {
        return VersionInfo(version)
    }

    data class VersionInfo(val version: String)
}

package org.kvxd.skinport

import java.net.URI

data class ProxyCfg(
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null
) {

    companion object {
        fun fromUrl(url: String): ProxyCfg {
            val uri = URI(url)

            val (user, pass) = uri.userInfo?.split(":")?.let {
                it[0] to it.getOrNull(1)
            } ?: (null to null)

            return ProxyCfg(
                host = uri.host,
                port = uri.port,
                username = user,
                password = pass
            )
        }
    }
}

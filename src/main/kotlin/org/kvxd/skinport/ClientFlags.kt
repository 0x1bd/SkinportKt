package org.kvxd.skinport

public data class ClientFlags(
    var proxy: ProxyCfg? = null,
    var mimicBrowser: Boolean = false,
    var apiSecret: SkinportAPISecret? = null,
    var enableBrotli: Boolean = false,
)

public fun ClientFlags.proxy(proxyConfig: ProxyCfg.() -> Unit) {
    val cfg = ProxyCfg().apply(proxyConfig)
    proxy = cfg
}

public fun ClientFlags.auth(secret: SkinportAPISecret.() -> Unit) {
    val cfg = SkinportAPISecret("", "").apply(secret)
    apiSecret = cfg
}
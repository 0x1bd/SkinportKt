package org.kvxd.skinport

data class ClientFlags(
    var proxy: ProxyCfg? = null,
    var mimicBrowser: Boolean = false,
    var apiSecret: SkinportAPISecret? = null,
    var useErrorPlugin: Boolean = false,
    var enableBrotli: Boolean = false,
)

fun ClientFlags.proxy(proxyConfig: ProxyCfg.() -> Unit) {
    val cfg = ProxyCfg().apply(proxyConfig)
    proxy = cfg
}

fun ClientFlags.auth(secret: SkinportAPISecret.() -> Unit) {
    val cfg = SkinportAPISecret("", "").apply(secret)
    apiSecret = cfg
}
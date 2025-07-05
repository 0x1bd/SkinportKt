package org.kvxd.skinport.internalapi.models

import kotlinx.serialization.Serializable
import org.kvxd.skinport.models.Currency

@Serializable
public data class DataResponse(
    val requestId: String,
    val success: Boolean,
    val message: String? = null,
    val csrf: String,
    val country: String,
    val currency: Currency,
    val rate: Double,
    val rates: Map<String, Double>,
    val locale: String,
)
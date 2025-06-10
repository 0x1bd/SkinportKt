package org.kvxd.skinport.models

import kotlinx.serialization.Serializable

@Serializable
data class SkinportErrorResponse(
    val errors: List<SkinportError>
)

@Serializable
data class SkinportError(
    val id: String,
    val message: String
)

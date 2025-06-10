package org.kvxd.skinport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Order {

    @SerialName("asc")
    ASC,
    @SerialName("desc")
    DESC

}
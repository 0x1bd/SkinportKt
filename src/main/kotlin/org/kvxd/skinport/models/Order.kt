package org.kvxd.skinport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class Order {

    @SerialName("asc")
    ASC,
    @SerialName("desc")
    DESC

}
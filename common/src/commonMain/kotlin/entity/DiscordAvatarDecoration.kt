package dev.kord.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordAvatarDecoration(
    val asset: String,
    @SerialName("sku_id")
    val skuId: Snowflake
)
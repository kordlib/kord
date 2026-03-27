package dev.kord.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The representation for the users avatar decoration.
 *
 * @property asset The avatar decoration hash
 * @property skuId The ID of the avatar decoration's SKU
 */
@Serializable
public data class DiscordAvatarDecoration(
    val asset: String,
    @SerialName("sku_id")
    val skuId: Snowflake
)
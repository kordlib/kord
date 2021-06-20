package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * @param id id of the sticker
 * @param packId id of the pack the sticker is from
 * @param name name of the sticker
 * @param description description of the sticker
 * @param tags a comma-separated list of tags for the sticker
 * @param asset sticker asset hash
 * @param formatType type of sticker format
 */
@Serializable
data class DiscordSticker(
    val id: Snowflake,
    @SerialName("pack_id")
    val packId: OptionalSnowflake = OptionalSnowflake.Missing,
    val name: String,
    val description: String,
    val tags: String,
    val asset: String,
    val type: StickerType,
    @SerialName("format_type")
    val formatType: StickerFormatType,
    val available: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val user: Optional<DiscordUser> = Optional.Missing(),
    @SerialName("sort_value")
    val sortValue: OptionalInt = OptionalInt.Missing
)

@Serializable(with = StickerType.Serializer::class)
sealed class StickerType(val value: Int) {
    class Unknown(value: Int) : StickerType(value)
    object Standard : StickerType(1)
    object Guild : StickerType(2)

    companion object {
        val values: Set<StickerType> = setOf(Standard, Guild)
    }

    internal object Serializer : KSerializer<StickerType> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.StickerType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): StickerType {
            return when (val value = decoder.decodeInt()) {
                1 -> Standard
                2 -> Guild
                else -> Unknown(value)
            }
        }

        override fun serialize(encoder: Encoder, value: StickerType) = encoder.encodeInt(value.value)

    }
}

@Serializable(with = StickerFormatType.Serializer::class)
sealed class StickerFormatType(val value: Int) {
    class Unknown(value: Int) : StickerFormatType(value)
    object PNG : StickerFormatType(1)
    object APNG : StickerFormatType(2)
    object LOTTIE : StickerFormatType(3)

    companion object {
        val values: Set<StickerFormatType> = setOf(PNG, APNG, LOTTIE)
    }

    internal object Serializer : KSerializer<StickerFormatType> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.StickerFormatType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): StickerFormatType = when (val value = decoder.decodeInt()) {
            1 -> PNG
            2 -> APNG
            3 -> LOTTIE
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: StickerFormatType) {
            encoder.encodeInt(value.value)
        }
    }
}

@Serializable
data class DiscordStickerPack(
    val id: Snowflake,
    val stickers: List<DiscordSticker>,
    val name: String,
    @SerialName("sku_id")
    val skuId: Snowflake,
    @SerialName("cover_sticker_id")
    val coverStickerId: Snowflake,
    val description: String,
    @SerialName("banner_asset_id")
    val bannerAssetId: String
)

@file:GenerateKordEnum(
    name = "PresenceStatus", valueType = STRING,
    entries = [
        Entry("Online", stringValue = "online", kDoc = "Online."),
        Entry("DoNotDisturb", stringValue = "dnd", kDoc = "Do Not Disturb."),
        Entry("Idle", stringValue = "idle", kDoc = "AFK."),
        Entry("Invisible", stringValue = "invisible", kDoc = "Invisible and shown as offline."),
        Entry("Offline", stringValue = "offline", kDoc = "Offline."),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.STRING
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
public data class DiscordPresenceUpdate(
    val user: DiscordPresenceUser,
    /*
    Don't trust the docs:
    2020-11-05: Discord documentation incorrectly claims this field is non-optional,
    yet it is not present during the READY event.
    */
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val status: PresenceStatus,
    val activities: List<DiscordActivity>,
    @SerialName("client_status")
    val clientStatus: DiscordClientStatus,
)

@Serializable(with = DiscordPresenceUser.Serializer::class)
public data class DiscordPresenceUser(
    val id: Snowflake,
    val details: JsonObject,
) {

    internal object Serializer : KSerializer<DiscordPresenceUser> {

        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Kord.DiscordPresenceUser") {
            element<Snowflake>("id")
            element<JsonElement>("details")
        }

        override fun deserialize(decoder: Decoder): DiscordPresenceUser {
            val jsonDecoder = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")
            val json = jsonDecoder.decodeJsonElement().jsonObject
            val id = Snowflake(json.getValue("id").jsonPrimitive.content)
            val details = json.toMutableMap()
            details.remove("id")

            return DiscordPresenceUser(id, JsonObject(details))
        }

        override fun serialize(encoder: Encoder, value: DiscordPresenceUser) {
            val jsonEncoder = encoder as? JsonEncoder ?: error("Can be serialized only by JSON")
            val details = value.details.toMutableMap()
            details["id"] = JsonPrimitive(value.id.toString())

            jsonEncoder.encodeJsonElement(JsonObject(details))
        }
    }

}

@Serializable
public data class DiscordClientStatus(
    val desktop: Optional<PresenceStatus> = Optional.Missing(),
    val mobile: Optional<PresenceStatus> = Optional.Missing(),
    val web: Optional<PresenceStatus> = Optional.Missing(),
)

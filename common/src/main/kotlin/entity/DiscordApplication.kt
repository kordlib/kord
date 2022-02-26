package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public sealed interface BaseDiscordApplication {
    public val id: Snowflake
    public val name: String
    public val icon: String?
    public val description: String
    public val rpcOrigins: Optional<List<String>>
    public val termsOfServiceUrl: Optional<String>
    public val privacyPolicyUrl: Optional<String>
    public val owner: Optional<DiscordUser>
    public val summary: String
    public val verifyKey: String
    public val guildId: OptionalSnowflake
    public val primarySkuId: OptionalSnowflake
    public val slug: Optional<String>
    public val coverImage: Optional<String>
    public val flags: Optional<ApplicationFlags>
}

/**
 * A representation of the
 * [Application Structure](https://discord.com/developers/docs/resources/application#application-object-application-structure).
 */
@Serializable
public data class DiscordApplication(
    override val id: Snowflake,
    override val name: String,
    override val icon: String?,
    override val description: String,
    @SerialName("rpc_origins")
    override val rpcOrigins: Optional<List<String>> = Optional.Missing(),
    @SerialName("bot_public")
    val botPublic: Boolean,
    @SerialName("bot_require_code_grant")
    val botRequireCodeGrant: Boolean,
    @SerialName("terms_of_service_url")
    override val termsOfServiceUrl: Optional<String> = Optional.Missing(),
    @SerialName("privacy_policy_url")
    override val privacyPolicyUrl: Optional<String> = Optional.Missing(),
    override val owner: Optional<DiscordUser> = Optional.Missing(),
    override val summary: String,
    @SerialName("verify_key")
    override val verifyKey: String,
    val team: DiscordTeam?,
    @SerialName("guild_id")
    override val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("primary_sku_id")
    override val primarySkuId: OptionalSnowflake = OptionalSnowflake.Missing,
    override val slug: Optional<String> = Optional.Missing(),
    @SerialName("cover_image")
    override val coverImage: Optional<String> = Optional.Missing(),
    override val flags: Optional<ApplicationFlags> = Optional.Missing(),
) : BaseDiscordApplication

/**
 * A representation of the partial
 * [Application Structure](https://discord.com/developers/docs/resources/application#application-object-application-structure)
 * sent in [invite create events](https://discord.com/developers/docs/topics/gateway#invite-create).
 */
@Serializable
public data class DiscordPartialApplication(
    override val id: Snowflake,
    override val name: String,
    override val icon: String?,
    override val description: String,
    @SerialName("rpc_origins")
    override val rpcOrigins: Optional<List<String>> = Optional.Missing(),
    @SerialName("terms_of_service_url")
    override val termsOfServiceUrl: Optional<String> = Optional.Missing(),
    @SerialName("privacy_policy_url")
    override val privacyPolicyUrl: Optional<String> = Optional.Missing(),
    override val owner: Optional<DiscordUser> = Optional.Missing(),
    override val summary: String,
    @SerialName("verify_key")
    override val verifyKey: String,
    @SerialName("guild_id")
    override val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("primary_sku_id")
    override val primarySkuId: OptionalSnowflake = OptionalSnowflake.Missing,
    override val slug: Optional<String> = Optional.Missing(),
    @SerialName("cover_image")
    override val coverImage: Optional<String> = Optional.Missing(),
    override val flags: Optional<ApplicationFlags> = Optional.Missing(),
) : BaseDiscordApplication

public enum class ApplicationFlag(public val code: Int) {
    GatewayPresence(1 shl 12),
    GatewayPresenceLimited(1 shl 13),
    GatewayGuildMembers(1 shl 14),
    GatewayGuildMembersLimited(1 shl 15),
    VerificationPendingGuildLimit(1 shl 16),
    Embedded(1 shl 17),
    GatewayMessageContent(1 shl 18),
    GatewayMessageContentLimited(1 shl 19);

    public operator fun plus(flag: ApplicationFlag): ApplicationFlags = ApplicationFlags(this.code or flag.code)

    public operator fun plus(flags: ApplicationFlags): ApplicationFlags = flags + this
}

@Serializable(with = ApplicationFlags.Serializer::class)
public data class ApplicationFlags internal constructor(val code: Int) {

    val flags: List<ApplicationFlag> get() = ApplicationFlag.values().filter { this.contains(it) }

    public operator fun contains(flag: ApplicationFlag): Boolean = this.code and flag.code == flag.code

    public operator fun contains(flags: ApplicationFlags): Boolean = this.code and flags.code == flags.code

    public operator fun plus(flag: ApplicationFlag): ApplicationFlags = ApplicationFlags(this.code or flag.code)

    public operator fun plus(flags: ApplicationFlags): ApplicationFlags = ApplicationFlags(this.code or flags.code)

    public operator fun minus(flag: ApplicationFlag): ApplicationFlags =
        ApplicationFlags(this.code and flag.code.inv())

    public operator fun minus(flags: ApplicationFlags): ApplicationFlags =
        ApplicationFlags(this.code and flags.code.inv())


    public inline fun copy(builder: Builder.() -> Unit): ApplicationFlags {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return Builder(code).apply(builder).flags()
    }


    internal object Serializer : KSerializer<ApplicationFlags> {

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ApplicationFlags", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ApplicationFlags {
            val flags = decoder.decodeInt()
            return ApplicationFlags(flags)
        }

        override fun serialize(encoder: Encoder, value: ApplicationFlags) {
            encoder.encodeInt(value.code)
        }
    }


    public class Builder(internal var code: Int = 0) {
        public operator fun ApplicationFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun ApplicationFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun ApplicationFlag.unaryMinus() {
            this@Builder.code = (this@Builder.code and this.code.inv())
        }

        public operator fun ApplicationFlags.unaryMinus() {
            this@Builder.code = (this@Builder.code and this.code.inv())
        }

        public fun flags(): ApplicationFlags = ApplicationFlags(code)
    }
}

public inline fun ApplicationFlags(builder: ApplicationFlags.Builder.() -> Unit): ApplicationFlags {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return ApplicationFlags.Builder().apply(builder).flags()
}

public fun ApplicationFlags(vararg flags: ApplicationFlag): ApplicationFlags = ApplicationFlags {
    flags.forEach { +it }
}

public fun ApplicationFlags(vararg flags: ApplicationFlags): ApplicationFlags = ApplicationFlags {
    flags.forEach { +it }
}

public fun ApplicationFlags(flags: Iterable<ApplicationFlag>): ApplicationFlags = ApplicationFlags {
    flags.forEach { +it }
}

@JvmName("ApplicationFlagsWithIterable")
public fun ApplicationFlags(flags: Iterable<ApplicationFlags>): ApplicationFlags = ApplicationFlags {
    flags.forEach { +it }
}

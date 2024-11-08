// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Where an app can be installed, also called its supported installation contexts
 *
 * See [ApplicationIntegrationType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/application#application-object-application-integration-types).
 */
@Serializable(with = ApplicationIntegrationType.Serializer::class)
public sealed class ApplicationIntegrationType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ApplicationIntegrationType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "ApplicationIntegrationType.Unknown(value=$value)"
            else "ApplicationIntegrationType.${this::class.simpleName}"

    /**
     * An unknown [ApplicationIntegrationType].
     *
     * This is used as a fallback for [ApplicationIntegrationType]s that haven't been added to Kord
     * yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : ApplicationIntegrationType(value)

    /**
     * App is installable to servers
     */
    public object GuildInstall : ApplicationIntegrationType(0)

    /**
     * App is installable to users
     */
    public object UserInstall : ApplicationIntegrationType(1)

    internal object Serializer : KSerializer<ApplicationIntegrationType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ApplicationIntegrationType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: ApplicationIntegrationType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): ApplicationIntegrationType =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [ApplicationIntegrationType]s.
         */
        public val entries: List<ApplicationIntegrationType> by lazy(mode = PUBLICATION) {
            listOf(
                GuildInstall,
                UserInstall,
            )
        }

        /**
         * Returns an instance of [ApplicationIntegrationType] with
         * [ApplicationIntegrationType.value] equal to the specified [value].
         */
        public fun from(`value`: Int): ApplicationIntegrationType = when (value) {
            0 -> GuildInstall
            1 -> UserInstall
            else -> Unknown(value)
        }
    }
}

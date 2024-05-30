// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection", "MemberVisibilityCanBePrivate"))

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
 * See [ChannelType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#channel-object-channel-types).
 */
@Serializable(with = ChannelType.Serializer::class)
public sealed class ChannelType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ChannelType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "ChannelType.Unknown(value=$value)"
            else "ChannelType.${this::class.simpleName}"

    /**
     * An unknown [ChannelType].
     *
     * This is used as a fallback for [ChannelType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : ChannelType(value)

    /**
     * A text channel within a server.
     */
    public object GuildText : ChannelType(0)

    /**
     * A direct message between users.
     */
    public object DM : ChannelType(1)

    /**
     * A voice channel within a server.
     */
    public object GuildVoice : ChannelType(2)

    /**
     * A direct message between multiple users.
     */
    public object GroupDM : ChannelType(3)

    /**
     * An
     * [organizational category](https://support.discord.com/hc/en-us/articles/115001580171-Channel-Categories-101)
     * that contains up to 50 channels.
     */
    public object GuildCategory : ChannelType(4)

    /**
     * A channel that
     * [users can follow and crosspost into their own server](https://support.discord.com/hc/en-us/articles/360032008192).
     */
    public object GuildNews : ChannelType(5)

    /**
     * A temporary sub-channel within a [GuildNews] channel.
     */
    public object PublicNewsThread : ChannelType(10)

    /**
     * A temporary sub-channel within a [GuildText] or [GuildForum] channel.
     */
    public object PublicGuildThread : ChannelType(11)

    /**
     * A temporary sub-channel within a [GuildText] channel that is only viewable by those invited
     * and those with the [ManageThreads][dev.kord.common.entity.Permission.ManageThreads] permission.
     */
    public object PrivateThread : ChannelType(12)

    /**
     * A voice channel for
     * [hosting events with an audience](https://support.discord.com/hc/en-us/articles/1500005513722).
     */
    public object GuildStageVoice : ChannelType(13)

    /**
     * The channel in a
     * [hub](https://support.discord.com/hc/en-us/articles/4406046651927-Discord-Student-Hubs-FAQ)
     * containing the listed servers.
     */
    public object GuildDirectory : ChannelType(14)

    /**
     * A channel that can only contain threads.
     */
    public object GuildForum : ChannelType(15)

    /**
     * A channel that can only contain threads, similar to [GuildForum] channels.
     */
    public object GuildMedia : ChannelType(16)

    internal object Serializer : KSerializer<ChannelType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ChannelType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: ChannelType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): ChannelType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [ChannelType]s.
         */
        public val entries: List<ChannelType> by lazy(mode = PUBLICATION) {
            listOf(
                GuildText,
                DM,
                GuildVoice,
                GroupDM,
                GuildCategory,
                GuildNews,
                PublicNewsThread,
                PublicGuildThread,
                PrivateThread,
                GuildStageVoice,
                GuildDirectory,
                GuildForum,
                GuildMedia,
            )
        }


        /**
         * Returns an instance of [ChannelType] with [ChannelType.value] equal to the specified
         * [value].
         */
        public fun from(`value`: Int): ChannelType = when (value) {
            0 -> GuildText
            1 -> DM
            2 -> GuildVoice
            3 -> GroupDM
            4 -> GuildCategory
            5 -> GuildNews
            10 -> PublicNewsThread
            11 -> PublicGuildThread
            12 -> PrivateThread
            13 -> GuildStageVoice
            14 -> GuildDirectory
            15 -> GuildForum
            16 -> GuildMedia
            else -> Unknown(value)
        }
    }
}

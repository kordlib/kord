// THIS FILE IS AUTO-GENERATED BY KordEnumProcessor.kt, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import dev.kord.common.entity.flags.BitFlags
import dev.kord.common.entity.flags.copy
import dev.kord.common.entity.flags.IntBitFlag
import dev.kord.common.entity.flags.IntBitFlags
import kotlin.Int
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind

/**
 * Convinience container of multiple [MessageFlags][MessageFlag] which can be combined into one.
 *
 * ## Creating a collection of message flags
 * You can create an [MessageFlags] object using the following methods
 * ```kotlin
 * // From flags
 * val flags1 = MessageFlags(MessageFlag.CrossPosted, MessageFlag.CrossPost)
 * // From an iterable
 * val flags2 = MessageFlags(listOf(MessageFlag.CrossPosted, MessageFlag.CrossPost))
 * // Using a builder
 * val flags3 = MessageFlags {
 *  +MessageFlag.CrossPosted
 *  -MessageFlag.CrossPost
 * }
 * ```
 *
 * ## Modifying existing flags
 * You can crate a modified copy of a [MessageFlags] instance using the [copy] method
 *
 * ```kotlin
 * flags.copy {
 *  +MessageFlag.SuppressNotifications
 * }
 * ```
 *
 * ## Mathmatical operators
 * All [MessageFlags] objects can use +/- operators
 *
 * ```kotlin
 * val flags = MessageFlags(MessageFlag.SuppressNotifications)
 * val flags2 = flags + MessageFlag.SuppressEmbeds
 * val otherFlags = flags - MessageFlag.SuppressNotifications
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for a flag
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val supressesEmbeds = MessageFlag.SuppressEmbeds in message.flags
 * val hasFlags = MessageFlags(MessageFlag.SuppressEmbeds, MessageFlag.SuppressNotifications) in message.flags
 * ```
 *
 * ## Unknown flag
 *
 * Whenever a newly added flag has not been added to Kord yet it will get deserialized as [MessageFlag.Unknown].
 * You can also use that to check for an yet unsupported flag
 * ```kotlin
 * val hasFlags = MessageFlag.Unknown(1 shl 69) in message.flags
 * ```
 * @see MessageFlag
 * @see Builder
 */
@Serializable(with = MessageFlags.Serializer::class)
public class MessageFlags(
    code: Int = 0,
) : IntBitFlags<MessageFlag, MessageFlags, MessageFlags.Builder>(MessageFlag.entries, code) {
    public override val name: String = "MessageFlags"

    public override fun buildUpon(): Builder = Builder(code)

    public override fun Implementation(flags: Int): MessageFlags = MessageFlags(flags)

    public class Builder(
        code: Int = 0,
    ) : IntBitFlags.Builder<MessageFlag, MessageFlags>(code) {
        public override fun flags(): MessageFlags = MessageFlags(code)
    }

    public class Serializer : BitFlags.Serializer<Int, MessageFlag, MessageFlags>(PrimitiveKind.INT,
            "code", Int.serializer()) {
        public override fun Implementation(code: Int): MessageFlags = MessageFlags(code)
    }

    public companion object : BitFlags.Companion<Int, MessageFlag, MessageFlags, Builder>() {
        public override fun Builder(): Builder = Builder()
    }
}

/**
 * See [MessageFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#message-object-message-flags).
 */
public sealed class MessageFlag(
    /**
     * The raw code used by Discord.
     */
    public override val code: Int,
) : IntBitFlag {
    /**
     * An unknown [MessageFlag].
     *
     * This is used as a fallback for [MessageFlag]s that haven't been added to Kord yet.
     */
    public class Unknown(
        code: Int,
    ) : MessageFlag(code)

    /**
     * This message has been published to subscribed channels (via Channel Following).
     */
    public object CrossPosted : MessageFlag(1)

    /**
     * This message originated from a message in another channel (via Channel Following).
     */
    public object IsCrossPost : MessageFlag(2)

    /**
     * Do not include any embeds when serializing this message.
     */
    public object SuppressEmbeds : MessageFlag(4)

    /**
     * The source message for this crosspost has been deleted (via Channel Following).
     */
    public object SourceMessageDeleted : MessageFlag(8)

    /**
     * This message came from the urgent message system.
     */
    public object Urgent : MessageFlag(16)

    /**
     * This message has an associated thread, with the same id as the message.
     */
    public object HasThread : MessageFlag(32)

    /**
     * This message is only visible to the user who invoked the Interaction.
     */
    public object Ephemeral : MessageFlag(64)

    /**
     * This message is an Interaction Response and the bot is "thinking".
     */
    public object Loading : MessageFlag(128)

    /**
     * This message failed to mention some roles and add their members to the thread.
     */
    public object FailedToMentionSomeRolesInThread : MessageFlag(256)

    public companion object {
        /**
         * A [List] of all known [MessageFlag]s.
         */
        public val entries: List<MessageFlag> by lazy(mode = PUBLICATION) {
            listOf(
                CrossPosted,
                IsCrossPost,
                SuppressEmbeds,
                SourceMessageDeleted,
                Urgent,
                HasThread,
                Ephemeral,
                Loading,
                FailedToMentionSomeRolesInThread,
            )
        }

    }
}

package com.gitlab.kordlib.core.exception

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.channel.Channel

class EntityNotFoundException : Exception {

    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)

    @Suppress("NOTHING_TO_INLINE")
    companion object {

        @PublishedApi
        internal inline fun entityNotFound(entityType: String, id: Snowflake): Nothing =
                throw EntityNotFoundException("$entityType with id ${id.asString} was not found.")

        @PublishedApi
        internal inline fun guildEntityNotFound(entityType: String, guildId: Snowflake, id: Snowflake): Nothing =
                throw EntityNotFoundException("$entityType with id ${id.asString} in guild ${guildId.asString} was not found.")


        inline fun guildNotFound(guildId: Snowflake): Nothing =
                entityNotFound("Guild", guildId)

        inline fun <reified T : Channel> channelNotFound(channelId: Snowflake): Nothing =
                entityNotFound(T::class.simpleName!!, channelId)

        inline fun memberNotFound(guildId: Snowflake, userId: Snowflake): Nothing =
                guildEntityNotFound("Member", guildId = guildId, id = userId)

        inline fun messageNotFound(channelId: Snowflake, messageId: Snowflake): Nothing = throw EntityNotFoundException("Message with id ${messageId.asString} in channel ${channelId.asString} was not found")

        inline fun userNotFound(userId: Snowflake): Nothing =
                entityNotFound("User", userId)

        inline fun selfNotFound(): Nothing =
                throw EntityNotFoundException("Self user not found")

        fun roleNotFound(guildId: Snowflake, roleId: Snowflake): Nothing =
                guildEntityNotFound("Role", guildId = guildId, id = roleId)

        fun banNotFound(guildId: Snowflake, userId: Snowflake): Nothing =
                guildEntityNotFound("Ban", guildId = guildId, id = userId)

        fun emojiNotFound(guildId: Snowflake, emojiId: Snowflake): Nothing =
                guildEntityNotFound("GuildEmoji", guildId = guildId, id = emojiId)

        fun webhookNotFound(webhookId: Snowflake): Nothing =
                entityNotFound("Webhook", webhookId)

        fun inviteNotFound(code: String): Nothing =
                throw EntityNotFoundException("Invite with code $code was not found.")

        fun widgetNotFound(id: Snowflake): Nothing =
                throw EntityNotFoundException("Widget for guild ${id.value} was not found.")

    }

}
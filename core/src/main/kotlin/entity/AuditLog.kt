package dev.kord.core.entity

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.data.IntegrationData
import dev.kord.core.cache.data.UserData
import dev.kord.core.cache.data.WebhookData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.thread.ThreadChannel

public class AuditLog(
    public val data: DiscordAuditLog,
    public val guildId: Snowflake,
    override val kord: Kord,
) : KordObject {

    public val users: List<User> get() = data.users.map { User(UserData.from(it), kord) }

    public val webhooks: List<Webhook> get() = data.webhooks.map { Webhook(WebhookData.from(it), kord) }

    public val integrations: List<Snowflake> get() = data.integrations.map { it.id }

    public val threads: List<ThreadChannel> get() = data.threads.map {
       val data =  ChannelData.from(it)
        Channel.from(data, kord)
    }.filterIsInstance<ThreadChannel>()

    public val entries: List<AuditLogEntry> get() = data.auditLogEntries.map { AuditLogEntry(it, kord) }

}

public class AuditLogEntry(public val data: DiscordAuditLogEntry, override val kord: Kord) : KordObject {
    public val targetId: Snowflake? get() = data.targetId

    public val changes: List<AuditLogChange<*>> get() = data.changes.orEmpty()

    public val userId: Snowflake get() = data.userId

    public val id: Snowflake get() = data.id

    public val actionType: AuditLogEvent get() = data.actionType

    public val options: AuditLogEntryOptionalInfo? get() = data.options.value

    public val reason: String? get() = data.reason.value

    @Suppress("UNCHECKED_CAST")
    public operator fun <T> get(value: AuditLogChangeKey<T>): AuditLogChange<T>? =
        changes.firstOrNull { it.key == value } as? AuditLogChange<T>

}

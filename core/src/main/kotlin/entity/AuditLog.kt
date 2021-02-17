package dev.kord.core.entity

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.UserData
import dev.kord.core.cache.data.WebhookData

class AuditLog(val data: DiscordAuditLog, val guildId: Snowflake, override val kord: Kord) : KordObject {

    val users: List<User> get() = data.users.map { User(UserData.from(it), kord) }

    val webhooks: List<Webhook> get() = data.webhooks.map { Webhook(WebhookData.from(it), kord) }

    val integrations: List<Snowflake> get() = data.integrations.map { it.id }

    val entries: List<AuditLogEntry> get() = data.auditLogEntries.map { AuditLogEntry(it, kord) }

}

class AuditLogEntry(val data: DiscordAuditLogEntry, override val kord: Kord) : KordObject {
    val targetId: Snowflake? get() = data.targetId

    val changes: List<AuditLogChange<*>> get() = data.changes.orEmpty()

    val userId: Snowflake get() = data.userId

    val id: Snowflake get() = data.id

    val actionType: AuditLogEvent get() = data.actionType

    val options: AuditLogEntryOptionalInfo? get() = data.options.value

    val reason: String? get() = data.reason.value

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(value: AuditLogChangeKey<T>): AuditLogChange<T>? =
            changes.firstOrNull { it.key == value } as? AuditLogChange<T>

}

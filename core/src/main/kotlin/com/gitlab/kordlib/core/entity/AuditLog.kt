package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.*
import com.gitlab.kordlib.common.entity.optional.orEmpty
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.cache.data.IntegrationData
import com.gitlab.kordlib.core.cache.data.UserData
import com.gitlab.kordlib.core.cache.data.WebhookData

class AuditLog(val data: DiscordAuditLog, val guildId: Snowflake, override val kord: Kord) : KordObject {

    val users: List<User> get() = data.users.map { User(UserData.from(it), kord) }

    val webhooks: List<Webhook> get() = data.webhooks.map { Webhook(WebhookData.from(it), kord) }

    val integrations: List<Snowflake> get() = data.integrations.map { it.id }

    val entries: List<AuditLogEntry> get() = data.auditLogEntries.map { AuditLogEntry(it, kord) }

}

class AuditLogEntry(val data: DiscordAuditLogEntry, override val kord:Kord): KordObject {
    val targetId: Snowflake? get() = data.targetId

    val changes: List<AuditLogChange<*>> get() = data.changes.orEmpty()

    val userId: Snowflake get() = data.userId

    val id: Snowflake get() = data.id

    val actionType: AuditLogEvent get() = data.actionType

    val options: AuditLogEntryOptionalInfo? get() = data.options.value

    val reason:  String? get() = data.reason.value

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(value: AuditLogChangeKey<T>): AuditLogChange<T>? =
            changes.firstOrNull { it.key == value } as? AuditLogChange<T>

}

package dev.kord.core.event.guild

import dev.kord.core.Kord
import dev.kord.core.entity.AuditLogEntry
import dev.kord.core.event.Event

public class GuildAuditLogEntryCreateEvent(
    public val auditLogEntry: AuditLogEntry,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?
): Event {
    override fun toString(): String {
        return "GuildAuditLogEntryCreate(auditLogEntry=$auditLogEntry, shard=$shard)"
    }
}
package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.TemplateBehavior
import dev.kord.core.cache.data.TemplateData
import java.time.Instant
import java.time.format.DateTimeFormatter

class Template(val data: TemplateData, override val kord: Kord) : KordObject, TemplateBehavior {
    override val code: String get() = data.code

    val name: String get() = data.name

    val description: String? get() = data.description

    val usageCount: Int get() = data.usageCount

    val creatorId: Snowflake get() = data.creatorId

    val creator: User get() = User(data.creator, kord)

    val createdAt: Instant get() = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(data.createdAt, Instant::from)

    val updatedAt: Instant get() = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(data.updatedAt, Instant::from)

    override val guildId: Snowflake get() = data.sourceGuildId

    val partialGuild: PartialGuild get() = PartialGuild(data.serializedSourceGuild, kord)

    val dirty: Boolean? = data.isDirty


}
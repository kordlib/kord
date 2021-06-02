package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.TemplateBehavior
import dev.kord.core.cache.data.TemplateData
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant

class Template(val data: TemplateData, override val kord: Kord) : KordObject, TemplateBehavior {
    override val code: String get() = data.code

    val name: String get() = data.name

    val description: String? get() = data.description

    val usageCount: Int get() = data.usageCount

    val creatorId: Snowflake get() = data.creatorId

    val creator: User get() = User(data.creator, kord)

    val createdAt: Instant get() = data.createdAt.toInstant()

    val updatedAt: Instant get() = data.updatedAt.toInstant()

    override val guildId: Snowflake get() = data.sourceGuildId

    val partialGuild: PartialGuild get() = PartialGuild(data.serializedSourceGuild, kord)

    val dirty: Boolean? = data.isDirty


}
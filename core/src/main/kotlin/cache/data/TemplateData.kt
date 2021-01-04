package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordTemplate
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class TemplateData(
    val code: String,
    val name: String,
    val description: String?,
    val usageCount: Int,
    val creatorId: Snowflake,
    val creator: UserData,
    val createdAt: String,
    val updatedAt: String,
    val sourceGuildId: Snowflake,
    val serializedSourceGuild: PartialGuildData,
    val isDirty: Boolean?

) {
    companion object {
        fun from(template: DiscordTemplate): TemplateData {
            return with(template) {
                TemplateData(
                    code,
                    name,
                    description,
                    usageCount,
                    creatorId,
                    creator.toData(),
                    createdAt,
                    updatedAt,
                    sourceGuildId,
                    PartialGuildData.from(serializedSourceGuild),
                    isDirty
                )
            }
        }
    }
}

fun DiscordTemplate.toData() = TemplateData.from(this)
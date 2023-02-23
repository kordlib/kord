package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.TemplateBehavior
import dev.kord.core.cache.data.TemplateData
import kotlinx.datetime.Instant

/**
 * Represents a code that when used, creates a guild based on a snapshot of an existing guild.
 *
 * @param data The [TemplateData] for the tempalte
 */
public class Template(public val data: TemplateData, override val kord: Kord) : KordObject, TemplateBehavior {
    override val code: String get() = data.code

    /** The template name */
    public val name: String get() = data.name

    /** The template description */
    public val description: String? get() = data.description

    /** The number of uses this template has. */
    public val usageCount: Int get() = data.usageCount

    /** The ID of the user that created the template. */
    public val creatorId: Snowflake get() = data.creatorId

    /** The [User] object of the user that created the template. */
    public val creator: User get() = User(data.creator, kord)

    /** The [Instant] the template was created. */
    public val createdAt: Instant get() = data.createdAt

    /** The [Instant] the template was last updated. */
    public val updatedAt: Instant get() = data.updatedAt

    override val guildId: Snowflake get() = data.sourceGuildId

    /** A [PartialGuild] object for the template. */
    public val partialGuild: PartialGuild get() = PartialGuild(data.serializedSourceGuild, kord)

    /** Whether the template has unsynced changes. */
    public val dirty: Boolean? = data.isDirty
}

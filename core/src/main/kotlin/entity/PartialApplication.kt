package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.PartialApplicationData
import dev.kord.core.event.guild.InviteCreateEvent
import java.util.*

/**
 * The partial details of an
 * [Application](https://discord.com/developers/docs/resources/application#application-object-application-structure)
 * sent in [InviteCreateEvent]s.
 */
public class PartialApplication(
    public val data: PartialApplicationData,
    override val kord: Kord,
) : KordEntity {

    override val id: Snowflake get() = data.id

    public val name: String get() = data.name

    public val iconHash: String? get() = data.icon

    public val description: String get() = data.description

    public val termsOfServiceUrl: String? get() = data.termsOfServiceUrl.value

    public val privacyPolicyUrl: String? get() = data.privacyPolicyUrl.value

    public val summary: String get() = data.summary

    public val verifyKey: String get() = data.verifyKey

    public val coverImageHash: String? get() = data.coverImage.value

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is ApplicationInfo -> other.id == id
        is PartialApplication -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "PartialApplication(data=$data, kord=$kord)"
    }
}

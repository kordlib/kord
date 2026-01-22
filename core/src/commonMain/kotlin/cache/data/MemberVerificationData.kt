package dev.kord.core.cache.data

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordGuildProfile
import dev.kord.common.entity.DiscordMemberVerification
import dev.kord.common.entity.DiscordMemberVerificationFormField
import dev.kord.common.entity.DiscordMemberVerificationGuild
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@KordPreview
@Serializable
public data class MemberVerificationData(
    val version: Instant? = null,
    val formFields: List<DiscordMemberVerificationFormField>,
    val description: String? = null,
    val guild: DiscordMemberVerificationGuild? = null,
    val profile: DiscordGuildProfile
) {
    public companion object {
        public fun from(entity: DiscordMemberVerification): MemberVerificationData = with(entity) {
            MemberVerificationData(
                version = version,
                formFields = formFields,
                description = description,
                guild = guild,
                profile = profile
            )
        }
    }
}

@KordPreview
public fun DiscordMemberVerification.toData(): MemberVerificationData = MemberVerificationData.from(this)

package dev.kord.core.entity

import dev.kord.common.annotation.DiscordAPIPreview
import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.GuildProfileData
import dev.kord.core.cache.data.MemberVerificationData
import dev.kord.core.cache.data.MemberVerificationFormFieldData
import dev.kord.core.cache.data.MemberVerificationGuildData
import kotlin.time.Instant

@DiscordAPIPreview
public class MemberVerification(
    public val data: MemberVerificationData,
    override val kord: Kord,
) : KordObject {
    /**
     * When the verification was last modified
     */
    public val version: Instant? get() = data.version

    /**
     * Questions for the applicants to answer
     */
    public val formFields: List<MemberVerificationFormField> get() {
        val list: MutableList<MemberVerificationFormField> = mutableListOf()
        data.formFields.forEach {
            list.add(MemberVerificationFormField(MemberVerificationFormFieldData.from(it), kord))
        }
        return list
    }

    /**
     * A description of what the guild is about. May be different to the guilds description
     */
    public val description: String? get() = data.description

    /**
     * The guild this member verification is for
     */
    public val guild: MemberVerificationGuild? get() = data.guild?.let { MemberVerificationGuild(MemberVerificationGuildData.from(it), kord) }

    /**
     * The profile of the guild this member verification is for.
     */
    public val profile: GuildProfile get() = GuildProfile(GuildProfileData.from(data.profile), kord)
}
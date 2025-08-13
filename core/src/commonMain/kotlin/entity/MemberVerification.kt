package dev.kord.core.entity

import dev.kord.common.entity.DiscordMemberVerificationFormField
import dev.kord.common.entity.DiscordMemberVerificationGuild
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.GuildProfileData
import dev.kord.core.cache.data.MemberVerificationData
import dev.kord.core.cache.data.MemberVerificationFormFieldData
import dev.kord.core.cache.data.MemberVerificationGuildData
import kotlinx.datetime.Instant

public class MemberVerification(
    public val data: MemberVerificationData,
    override val kord: Kord,
) : KordObject {
    public val version: Instant? get() = data.version

    public val formFields: List<MemberVerificationFormField> get() {
        val list: MutableList<MemberVerificationFormField> = mutableListOf()
        data.formFields.forEach {
            list.add(MemberVerificationFormField(MemberVerificationFormFieldData.from(it), kord))
        }
        return list
    }

    public val description: String? get() = data.description

    public val guild: MemberVerificationGuild? get() = data.guild?.let { MemberVerificationGuild(MemberVerificationGuildData.from(it), kord) }

    public val profile: GuildProfile get() = GuildProfile(GuildProfileData.from(data.profile), kord)
}
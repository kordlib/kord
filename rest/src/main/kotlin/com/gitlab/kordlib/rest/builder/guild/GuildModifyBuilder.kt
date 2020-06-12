package com.gitlab.kordlib.rest.builder.guild

import com.gitlab.kordlib.common.entity.DefaultMessageNotificationLevel
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.VerificationLevel
import com.gitlab.kordlib.rest.Image
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.rest.json.request.GuildModifyRequest
import com.gitlab.kordlib.common.annotation.KordDsl
import java.util.*

@KordDsl
class GuildModifyBuilder : AuditRequestBuilder<GuildModifyRequest> {
    override var reason: String? = null
    var name: String? = null
    var region: Snowflake? = null
    var verificationLevel: VerificationLevel? = null
    var notificationLevel: DefaultMessageNotificationLevel? = null
    var afkChannelId: Snowflake? = null
    var afkTimeout: Int? = null
    var icon: Image? = null
    var ownerId: Snowflake? = null
    var splash: Image? = null

    /**
     * The id of the channel where "PUBLIC" guilds display rules and/or guidelines.
     */
    var rulesChannelId: Snowflake? = null

    /**
     * The id of the channel where admins and moderators of "PUBLIC" guilds receive notices from Discord.
     */
    var publicUpdatesChannelId: Snowflake? = null

    /**
     * The preferred locale of a "PUBLIC" guild used in server discovery and notices from Discord; defaults to "en-US".
     */
    var preferredLocale: Locale? = null

    override fun toRequest(): GuildModifyRequest = GuildModifyRequest(
            name = name,
            region = region?.value,
            verificationLevel = verificationLevel,
            defaultMessageNotificationLevel = notificationLevel,
            afkChannel = afkChannelId?.value,
            afkTimeout = afkTimeout,
            icon = icon?.dataUri,
            ownerId = ownerId?.value,
            spalsh = splash?.dataUri,
            preferredLocale = preferredLocale?.let { listOf(it.language, it.country).joinToString("-") },
            publicUpdatesChannelId = publicUpdatesChannelId?.value
    )
}
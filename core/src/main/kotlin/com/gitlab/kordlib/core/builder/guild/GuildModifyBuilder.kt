package com.gitlab.kordlib.core.builder.guild

import com.gitlab.kordlib.common.entity.DefaultMessageNotificationLevel
import com.gitlab.kordlib.common.entity.VerificationLevel
import com.gitlab.kordlib.core.builder.AuditRequestBuilder
import com.gitlab.kordlib.core.entity.Image
import com.gitlab.kordlib.core.entity.Region
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.GuildModifyRequest

class GuildModifyBuilder : AuditRequestBuilder<GuildModifyRequest> {
    override var reason: String? = null
    var name: String? = null
    var region: Region? = null
    var verificationLevel: VerificationLevel? = null
    var notificationLevel: DefaultMessageNotificationLevel? = null
    var afkChannelId: Snowflake? = null
    var afkTimeout: Int? = null
    var icon: Image? = null
    var ownerId: Snowflake? = null
    var splash: Image? = null

    override fun toRequest(): GuildModifyRequest = GuildModifyRequest(
            name = name,
            region = region?.id?.value,
            verificationLevel = verificationLevel,
            defaultMessageNotificationLevel = notificationLevel,
            afkChannel = afkChannelId?.value,
            afkTimeout = afkTimeout,
            icon = icon?.dataUri,
            ownerId = ownerId?.value,
            spalsh = splash?.dataUri
    )
}
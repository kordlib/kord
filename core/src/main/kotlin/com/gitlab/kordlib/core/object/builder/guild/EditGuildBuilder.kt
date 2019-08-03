package com.gitlab.kordlib.core.`object`.builder.guild

import com.gitlab.kordlib.common.entity.DefaultMessageNotificationLevel
import com.gitlab.kordlib.common.entity.Embed
import com.gitlab.kordlib.common.entity.VerificationLevel
import com.gitlab.kordlib.core.`object`.Image
import com.gitlab.kordlib.core.entity.Region
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.ModifyGuildRequest

class EditGuildBuilder(
        var name: String? = null,
        var region: Region? = null,
        var verificationLevel: VerificationLevel? = null,
        var notificationLevel: DefaultMessageNotificationLevel? = null,
        var afkChannelId: Snowflake? = null,
        var afkTimeout: Int? = null,
        var icon: Image? = null,
        var ownerId: Snowflake? = null,
        var splash: Image? = null
) {
    internal fun toRequest() = ModifyGuildRequest(
            name = name,
            region = region?.id?.value,
            verificationLevel = verificationLevel,
            defaultMessageNotificationLevel = notificationLevel,
            afkChannel = afkChannelId?.value,
            afkTimeout = afkTimeout,
            icon = icon?.formatted,
            ownerId = ownerId?.value,
            spalsh = splash?.formatted
    )
}
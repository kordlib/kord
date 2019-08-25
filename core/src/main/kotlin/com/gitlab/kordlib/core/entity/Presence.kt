package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.core.`object`.Activity
import com.gitlab.kordlib.core.`object`.data.PresenceData
import com.gitlab.kordlib.gateway.Presence
import com.gitlab.kordlib.gateway.UpdateStatus

class Presence(val status: Status, val activity: Activity? = null) {
    constructor(data: PresenceData) : this(data.status, data.game?.let(::Activity))

    internal fun asUpdate() = UpdateStatus(null, activity?.toGatewayActivity(), status, false)
}
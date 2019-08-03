package com.gitlab.kordlib.core.`object`.builder.ban

import com.gitlab.kordlib.rest.json.request.AddGuildBanRequest


class NewBanBuilder(
        var reason: String? = null,
        var deleteMessagesDays: Int? = null
) {
    internal fun toRequest() = AddGuildBanRequest(reason, deleteMessagesDays)
}
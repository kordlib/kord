package com.gitlab.kordlib.core.`object`.builder.ban

import com.gitlab.kordlib.rest.json.request.AddGuildBanRequest


class NewBanBuilder (
        /**
         * The reason for banning this member.
         */
        var reason: String? = null,

        /**
         * The number of days to delete messages for (0-7).
         */
        var deleteMessagesDays: Int? = null
) {
    fun toRequest() = AddGuildBanRequest(reason, deleteMessagesDays)
}
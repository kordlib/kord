package dev.kord.rest.json.response

import dev.kord.common.entity.DiscordApplication

@Deprecated(
    "'ApplicationInfoResponse' was moved to common and renamed to 'DiscordApplication'.",
    ReplaceWith("DiscordApplication", "dev.kord.common.entity.DiscordApplication"),
    DeprecationLevel.ERROR,
)
public typealias ApplicationInfoResponse = DiscordApplication

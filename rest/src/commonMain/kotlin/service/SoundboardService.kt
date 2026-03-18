package dev.kord.rest.service

import dev.kord.common.entity.DiscordSoundboardSound
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

/**
 * Service for soundboard endpoints.
 */
public class SoundboardService(requestHandler: RequestHandler) : RestService(requestHandler) {

    /**
     * Retrieves the default [soundboard sounds][DiscordSoundboardSound], which can be used by all users.
     */
    public suspend fun getDefaultSounds(): List<DiscordSoundboardSound> = call(Route.GetSoundboardDefaultSounds)
}

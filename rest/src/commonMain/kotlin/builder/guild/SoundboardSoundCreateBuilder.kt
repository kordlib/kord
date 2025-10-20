package dev.kord.rest.builder.guild

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalDouble
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.Sound
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.CreateSoundboardSoundRequest

/**
 * Builder for [CreateSoundboardSoundRequest].
 *
 * @property name the name of the sound
 * @property sound the [sound data][Sound]
 * @property volume the volume of the soundboard sound, from 0 to 1, defaults to 1
 * @property emojiId the id of the custom emoji for the soundboard sound
 * @property emojiName the unicode character of a standard emoji for the soundboard sound
 */
public class SoundboardSoundCreateBuilder(
    public val name: String,
    public val sound: Sound
) : AuditRequestBuilder<CreateSoundboardSoundRequest> {
    override var reason: String? = null

    private var _volume: OptionalDouble = OptionalDouble.Missing
    public var volume: Double? by ::_volume.delegate()

    private var _emojiId: OptionalSnowflake = OptionalSnowflake.Missing
    public var emojiId: Snowflake? by ::_emojiId.delegate()

    private var _emojiName: Optional<String?> = Optional.Missing()
    public var emojiName: String? by ::_emojiName.delegate()

    override fun toRequest(): CreateSoundboardSoundRequest = CreateSoundboardSoundRequest(
        name,
        sound.dataUri,
        _volume,
        _emojiId,
        _emojiName
    )
}

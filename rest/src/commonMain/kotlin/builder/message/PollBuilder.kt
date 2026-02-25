package dev.kord.rest.builder.message

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.DiscordPoll
import dev.kord.common.entity.PollLayoutType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.serialization.DurationInHours
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.PollCreateRequest
import kotlin.time.Clock
import kotlin.time.Duration

/**
 * A builder for a [PollCreateRequest].
 *
 * @property question the question
 * @property answers a list of possible answers
 * @property duration the [Duration] of poll expiry
 * @property allowMultiselect whether users should be allowed to select multiple answers
 * @property layoutType the [PollLayoutType]
 */
@KordDsl
public class PollBuilder(public var question: DiscordPoll.Media) : RequestBuilder<PollCreateRequest> {
    public var answers: MutableList<DiscordPoll.Answer> = mutableListOf()

    internal var _duration: Optional<DurationInHours> = Optional.Missing()

    public var duration: Duration? by ::_duration.delegate()

    internal var _allowMultiselect: OptionalBoolean = OptionalBoolean.Missing
    public var allowMultiselect: Boolean? by ::_allowMultiselect.delegate()

    public var layoutType: PollLayoutType = PollLayoutType.Default

    /**
     * Sets the polls [Duration] to [duration].
     */
    public fun expiresIn(duration: Duration) {
        this@PollBuilder.duration = Duration.parse((Clock.System.now() + duration).toString())
    }

    /**
     * Adds an answer with [title].
     *
     * @param id the answer id
     */
    public fun answer(title: String, id: Int = answers.size): Unit =
        answer(title, emoji = null, id)

    /**
     * Adds an answer with [title] and [emojiUnicode].
     *
     * @param id the answer id
     */
    public fun answer(title: String, emojiUnicode: String? = null, id: Int = answers.size) {
        require(answers.size < 10) { "Cannot add more than 10 answers" }
        answers.add(
            DiscordPoll.Answer(
                answerId = id,
                pollMedia = DiscordPoll.Media(
                    Optional(title),
                    Optional(emojiUnicode?.let { DiscordPartialEmoji(name = it) }).coerceToMissing()
                )
            )
        )
    }

    /**
     * Adds an answer with [title] and [emoji].
     *
     * @param id the answer id
     */
    public fun answer(title: String, emoji: Snowflake? = null, id: Int = answers.size) {
        require(answers.size < 10) { "Cannot add more than 10 answers" }
        answers.add(
            DiscordPoll.Answer(
                answerId = id,
                pollMedia = DiscordPoll.Media(
                    Optional(title),
                    Optional(emoji?.let { DiscordPartialEmoji(id = it) }).coerceToMissing()
                )
            )
        )
    }

    override fun toRequest(): PollCreateRequest = PollCreateRequest(
        question,
        answers.toList(),
        _duration,
        _allowMultiselect,
        Optional(layoutType)
    )
}

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
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.PollCreateRequest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

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
public class PollBuilder(questionText: String) : RequestBuilder<PollCreateRequest> {
    public var question: DiscordPoll.Media = DiscordPoll.Media(Optional(questionText))

    public var answers: MutableList<DiscordPoll.Answer> = mutableListOf()

    public var duration: Int? = 24

    internal var _allowMultiselect: OptionalBoolean = OptionalBoolean.Missing
    public var allowMultiselect: Boolean? by ::_allowMultiselect.delegate()

    public var layoutType: PollLayoutType = PollLayoutType.Default

    /**
     * Sets the polls [Duration] to [duration].
     *
     * The minimum duration of polls is 1 hour.
     * The maximum duration of polls is 32 days.
     */
    public fun expiresIn(duration: Duration) {
        if (duration > 32.days) error("Polls cannot have a duration of more than 32 days!")
        if (duration < 1.hours) error("Polls cannot have a duration of less than 1 hour!")

        this@PollBuilder.duration = duration.inWholeHours.toInt()
    }

    /**
     * Adds an emoji to the question using its name
     */
    public fun emoji(emoji: String) {
        question = DiscordPoll.Media(
            question.text, Optional(DiscordPartialEmoji(name = emoji))
        )
    }

    /**
     * Adds an emoji to the question using its ID
     */
    public fun emoji(emoji: Snowflake) {
        question = DiscordPoll.Media(
            question.text, Optional(DiscordPartialEmoji(id = emoji))
        )
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
        duration,
        _allowMultiselect,
        Optional(layoutType)
    )
}

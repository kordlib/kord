package dev.kord.rest.builder.message

import dev.kord.common.annotation.KordDsl
import dev.kord.rest.json.request.CreatablePoll
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.DiscordPoll
import dev.kord.common.entity.PollLayoutType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * A builder for a [CreatablePoll].
 *
 * @property question the question
 * @property answers a list of possible answers
 * @property expiry the [Instant] of poll expiry
 * @property allowMultiselect whether users should be allowed to select multiple answers
 * @property layoutType the [PollLayoutType]
 */
@KordDsl
public class PollBuilder : RequestBuilder<CreatablePoll> {
    public var question: DiscordPoll.Media? = null
    public val answers: MutableList<DiscordPoll.Answer> = mutableListOf()

    public var expiry: Instant? = null

    internal var _allowMultiselect: Optional<Boolean> = Optional.Missing()
    public var allowMultiselect: Boolean? by ::_allowMultiselect.delegate()

    public var layoutType: PollLayoutType = PollLayoutType.DEFAULT

    /**
     * Sets the polls [Duration] to [duration].
     */
    public fun expiresIn(duration: Duration) {
        expiry = Clock.System.now() + duration
    }

    /**
     * Adds a question with [title].
     */
    // to resolve resolution ambiguity
    public fun question(title: String): Unit = question(title, emoji = null)

    /**
     * Adds a question with [title] and [emojiUnicode].
     */
    public fun question(title: String, emojiUnicode: String? = null) {
        question = DiscordPoll.Media(
            Optional(title),
            Optional(emojiUnicode?.let { DiscordPartialEmoji(name = it) }).coerceToMissing()
        )
    }

    /**
     * Adds a question with [title] and [emoji].
     */
    public fun question(title: String, emoji: Snowflake? = null) {
        question = DiscordPoll.Media(
            Optional(title),
            Optional(emoji?.let { DiscordPartialEmoji(id = it) }).coerceToMissing()
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

    override fun toRequest(): CreatablePoll = CreatablePoll(
        question ?: error("Please set a question"),
        answers,
        expiry ?: error("Please set an expiry"),
        _allowMultiselect,
        layoutType
    )
}
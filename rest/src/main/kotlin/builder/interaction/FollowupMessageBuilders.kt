package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
sealed interface FollowupMessageBuilder<T> : RequestBuilder<T> {

    var tts: Boolean?

    var allowedMentions: AllowedMentionsBuilder?

    val embeds: MutableList<EmbedBuilder>

    val components: MutableList<MessageComponentBuilder>

    val content: String?

}
@KordPreview
@OptIn(ExperimentalContracts::class)
inline fun <T> FollowupMessageBuilder<T>.actionRow(builder: ActionRowBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    components.add(ActionRowBuilder().apply(builder))
}
@KordPreview
@OptIn(ExperimentalContracts::class)
inline fun <T> FollowupMessageBuilder<T>.embed(builder: EmbedBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    embeds += EmbedBuilder().apply(builder)
}

/**
 * Configures the mentions that should trigger a mention (aka ping). Not calling this function will result in the default behavior
 * (ping everything), calling this function but not configuring it before the request is build will result in all
 * pings being ignored.
 */
@KordPreview
@OptIn(ExperimentalContracts::class)
inline fun <T> FollowupMessageBuilder<T>.allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
}



@KordPreview
@KordDsl
class PublicFollowupMessageModifyBuilder :
    FollowupMessageBuilder<MultipartFollowupMessageModifyRequest> {
    private var _content: Optional<String> = Optional.Missing()
    override var content: String? by ::_content.delegate()

    override val embeds: MutableList<EmbedBuilder> = mutableListOf()

    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    override var tts: Boolean? by ::_tts.delegate()

    val files: MutableList<Pair<String, InputStream>> = mutableListOf()


    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    override var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    override val components: MutableList<MessageComponentBuilder> = mutableListOf()


    fun addFile(name: String, content: InputStream) {
        files += name to content
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }
    override fun toRequest(): MultipartFollowupMessageModifyRequest {
        return MultipartFollowupMessageModifyRequest(
            FollowupMessageModifyRequest(
                _content,
                Optional.missingOnEmpty(embeds.map(EmbedBuilder::toRequest)),
                _allowedMentions.map { it.build() },
                Optional.missingOnEmpty(components.map(MessageComponentBuilder::build))
            ),
            files
        )
    }
}


@KordPreview
@KordDsl
class EphemeralFollowupMessageModifyBuilder :
    FollowupMessageBuilder<FollowupMessageModifyRequest> {
    private var _content: Optional<String> = Optional.Missing()
    override var content: String? by ::_content.delegate()

    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    override var tts: Boolean? by ::_tts.delegate()

    override val embeds: MutableList<EmbedBuilder> = mutableListOf()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    override var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    override val components: MutableList<MessageComponentBuilder> = mutableListOf()

    override fun toRequest(): FollowupMessageModifyRequest {
        return FollowupMessageModifyRequest(
            content = _content,
            allowedMentions = _allowedMentions.map { it.build() },
            components = Optional.missingOnEmpty(components.map(MessageComponentBuilder::build))
        )
    }
}


@KordPreview
@KordDsl
class PublicFollowupMessageCreateBuilder : FollowupMessageBuilder<MultipartFollowupMessageCreateRequest> {

    private var _content: Optional<String> = Optional.Missing()
    override var content: String? by ::_content.delegate()


    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    override var tts: Boolean? by ::_tts.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    override var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    val files: MutableList<Pair<String, InputStream>> = mutableListOf()
    override val embeds: MutableList<EmbedBuilder> = mutableListOf()

    override val components: MutableList<MessageComponentBuilder> = mutableListOf()



    override fun toRequest(): MultipartFollowupMessageCreateRequest =
        MultipartFollowupMessageCreateRequest(
            FollowupMessageCreateRequest(
                content = _content,
                tts = _tts,
                embeds = Optional.missingOnEmpty(embeds.map(EmbedBuilder::toRequest)),
                allowedMentions = _allowedMentions.map { it.build() },
                components = Optional.missingOnEmpty(components.map(MessageComponentBuilder::build))
            ),
            files,
        )

}


@KordPreview
@KordDsl
class EphemeralFollowupMessageCreateBuilder : FollowupMessageBuilder<MultipartFollowupMessageCreateRequest> {

    private var _content: Optional<String> = Optional.Missing()
    override var content: String? by ::_content.delegate()

    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    override var tts: Boolean? by ::_tts.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    override var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    override val embeds: MutableList<EmbedBuilder> = mutableListOf()

    override val components: MutableList<MessageComponentBuilder> = mutableListOf()


    override fun toRequest(): MultipartFollowupMessageCreateRequest =
        MultipartFollowupMessageCreateRequest(
            FollowupMessageCreateRequest(
                content = _content,
                embeds = Optional.missingOnEmpty(embeds.map(EmbedBuilder::toRequest)),
                tts = _tts,
                allowedMentions = _allowedMentions.map { it.build() },
                components = Optional.missingOnEmpty(components.map(MessageComponentBuilder::build))
            ),
        )


}
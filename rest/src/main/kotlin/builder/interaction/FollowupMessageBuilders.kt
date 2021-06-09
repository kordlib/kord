package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.components.ActionRowBuilder
import dev.kord.rest.builder.components.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.EmbedRequest
import dev.kord.rest.json.request.FollowupMessageCreateRequest
import dev.kord.rest.json.request.FollowupMessageModifyRequest
import dev.kord.rest.json.request.MultipartFollowupMessageCreateRequest
import dev.kord.rest.json.request.MultipartFollowupMessageModifyRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
@KordDsl
class PublicFollowupMessageModifyBuilder :
    RequestBuilder<MultipartFollowupMessageModifyRequest> {
    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    val files: MutableList<Pair<String, InputStream>> = mutableListOf()


    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    @KordPreview
    val components: MutableList<MessageComponentBuilder> = mutableListOf()

    @OptIn(ExperimentalContracts::class)
    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (embeds == null) embeds = mutableListOf()
        embeds!! += EmbedBuilder().apply(builder)
    }

    @OptIn(ExperimentalContracts::class)
    @KordPreview
    inline fun actionRow(builder: ActionRowBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(ActionRowBuilder().apply(builder))
    }


    fun addFile(name: String, content: InputStream) {
        files += name to content
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

    /**
     * Configures the mentions that should trigger a mention (aka ping). Not calling this function will result in the default behavior
     * (ping everything), calling this function but not configuring it before the request is build will result in all
     * pings being ignored.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
    }


    override fun toRequest(): MultipartFollowupMessageModifyRequest {
        return MultipartFollowupMessageModifyRequest(
            FollowupMessageModifyRequest(
                _content,
                _embeds.mapList { it.toRequest() },
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
    RequestBuilder<FollowupMessageModifyRequest> {
    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    val components: MutableList<MessageComponentBuilder> = mutableListOf()


    /**
     * Configures the mentions that should trigger a mention (aka ping). Not calling this function will result in the default behavior
     * (ping everything), calling this function but not configuring it before the request is build will result in all
     * pings being ignored.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
    }

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
class PublicFollowupMessageCreateBuilder : RequestBuilder<MultipartFollowupMessageCreateRequest> {

    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()


    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    var tts: Boolean? by ::_tts.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    val files: MutableList<Pair<String, InputStream>> = mutableListOf()
    var embeds: MutableList<EmbedRequest> = mutableListOf()

    val components: MutableList<MessageComponentBuilder> = mutableListOf()

    fun addFile(name: String, content: InputStream) {
        files += name to content
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

    /**
     * Configures the mentions that should trigger a mention (aka ping). Not calling this function will result in the default behavior
     * (ping everything), calling this function but not configuring it before the request is build will result in all
     * pings being ignored.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
    }

    @OptIn(ExperimentalContracts::class)
    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        embeds.add(EmbedBuilder().apply(builder).toRequest())
    }

    override fun toRequest(): MultipartFollowupMessageCreateRequest =
        MultipartFollowupMessageCreateRequest(
            FollowupMessageCreateRequest(
                content = _content,
                tts = _tts,
                embeds = Optional.missingOnEmpty(embeds),
                allowedMentions = _allowedMentions.map { it.build() },
                components = Optional.missingOnEmpty(components.map(MessageComponentBuilder::build))
            ),
            files,
        )

}


@KordPreview
@KordDsl
class EphemeralFollowupMessageCreateBuilder(var content: String) :
    RequestBuilder<MultipartFollowupMessageCreateRequest> {

    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    var tts: Boolean? by ::_tts.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    /**
     * Configures the mentions that should trigger a mention (aka ping). Not calling this function will result in the default behavior
     * (ping everything), calling this function but not configuring it before the request is build will result in all
     * pings being ignored.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
    }

    val components: MutableList<MessageComponentBuilder> = mutableListOf()

    override fun toRequest(): MultipartFollowupMessageCreateRequest =
        MultipartFollowupMessageCreateRequest(
            FollowupMessageCreateRequest(
                content = Optional.Value(content),
                tts = _tts,
                allowedMentions = _allowedMentions.map { it.build() },
                components = Optional.missingOnEmpty(components.map(MessageComponentBuilder::build))
            ),
        )

}

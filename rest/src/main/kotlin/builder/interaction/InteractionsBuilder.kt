package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.AllowedMentions
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
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
@KordDsl
class ApplicationCommandCreateBuilder(
    val name: String,
    val description: String,
) : RequestBuilder<ApplicationCommandCreateRequest>, BaseApplicationBuilder() {

    private var _options: Optional<MutableList<OptionsBuilder>> = Optional.Missing()
    override var options: MutableList<OptionsBuilder>? by ::_options.delegate()

    override fun toRequest(): ApplicationCommandCreateRequest {
        return ApplicationCommandCreateRequest(name,
            description,
            _options.mapList { it.toRequest() })

    }

}

@KordPreview
@KordDsl
class ApplicationCommandsCreateBuilder : RequestBuilder<List<ApplicationCommandCreateRequest>> {
    val commands: MutableList<ApplicationCommandCreateBuilder> = mutableListOf()
    fun command(
        name: String,
        description: String,
        builder: ApplicationCommandCreateBuilder.() -> Unit
    ) {
        commands += ApplicationCommandCreateBuilder(name, description).apply(builder)
    }

    override fun toRequest(): List<ApplicationCommandCreateRequest> {
        return commands.map { it.toRequest() }
    }

}

@KordPreview
@KordDsl
sealed class BaseApplicationBuilder {
    abstract var options: MutableList<OptionsBuilder>?

    @OptIn(ExperimentalContracts::class)
    inline fun boolean(name: String, description: String, builder: BooleanBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(BooleanBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun int(name: String, description: String, builder: IntChoiceBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(IntChoiceBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun string(
        name: String,
        description: String,
        builder: StringChoiceBuilder.() -> Unit = {},
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(StringChoiceBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun group(name: String, description: String, builder: GroupCommandBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(GroupCommandBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun subCommand(
        name: String,
        description: String,
        builder: SubCommandBuilder.() -> Unit = {},
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(SubCommandBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun role(name: String, description: String, builder: RoleBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(RoleBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun user(name: String, description: String, builder: UserBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(UserBuilder(name, description).apply(builder))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun channel(name: String, description: String, builder: ChannelBuilder.() -> Unit = {}) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (options == null) options = mutableListOf()
        options!!.add(ChannelBuilder(name, description).apply(builder))
    }
}

@KordPreview
@KordDsl
class ApplicationCommandModifyBuilder : BaseApplicationBuilder(),
    RequestBuilder<ApplicationCommandModifyRequest> {

    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()

    private var _description: Optional<String> = Optional.Missing()
    var description: String? by ::_name.delegate()

    private var _options: Optional<MutableList<OptionsBuilder>> = Optional.Missing()
    override var options: MutableList<OptionsBuilder>? by ::_options.delegate()

    override fun toRequest(): ApplicationCommandModifyRequest {
        return ApplicationCommandModifyRequest(_name,
            _description,
            _options.mapList { it.toRequest() })

    }

}

@KordPreview
@KordDsl
class InteractionResponseModifyBuilder :
    RequestBuilder<MultipartInteractionResponseModifyRequest> {
    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    val files: MutableList<Pair<String, InputStream>> = mutableListOf()

    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        if (embeds == null) embeds = mutableListOf()
        embeds!! += EmbedBuilder().apply(builder)
    }

    fun addFile(name: String, content: InputStream) {
        files += name to content
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

    override fun toRequest(): MultipartInteractionResponseModifyRequest {
        return MultipartInteractionResponseModifyRequest(
            InteractionResponseModifyRequest(
                _content,
                _embeds.mapList { it.toRequest() },
                _allowedMentions.map { it.build() }
            ),
            files
        )
    }
}

@KordPreview
@KordDsl
class FollowupMessageModifyBuilder :
    RequestBuilder<FollowupMessageModifyRequest> {
    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        if (embeds == null) embeds = mutableListOf()
        embeds!! += EmbedBuilder().apply(builder)
    }


    override fun toRequest(): FollowupMessageModifyRequest {
        return FollowupMessageModifyRequest(
            _content,
            _embeds.mapList { it.toRequest() },
            _allowedMentions.map { it.build() })
    }
}

@KordPreview
@KordDsl
class InteractionApplicationCommandCallbackDataBuilder {

    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()


    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    var tts: Boolean? by ::_tts.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    private var _flags: Optional<MessageFlags> = Optional.Missing()
    var flags: MessageFlags? by ::_flags.delegate()

    @OptIn(ExperimentalContracts::class)
    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        if (embeds == null) embeds = mutableListOf()
        embeds!! += EmbedBuilder().apply(builder)
    }

    fun build(): InteractionApplicationCommandCallbackData {

        return InteractionApplicationCommandCallbackData(
            _tts,
            _content,
            _embeds.mapList { it.toRequest() },
            _allowedMentions.map { it.build() },
            _flags
        )

    }
}

@KordPreview
@KordDsl
class FollowupMessageCreateBuilder : RequestBuilder<MultipartFollowupMessageCreateRequest> {

    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _username: Optional<String> = Optional.Missing()
    var username: String? by ::_username.delegate()

    private var _avatarUrl: Optional<String> = Optional.Missing()
    var avatarUrl: String? by ::_avatarUrl.delegate()

    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    var tts: Boolean? by ::_tts.delegate()

    private var _allowedMentions: Optional<AllowedMentions> = Optional.Missing()
    var allowedMentions: AllowedMentions? by ::_allowedMentions.delegate()

    val files: MutableList<Pair<String, InputStream>> = mutableListOf()
    var embeds: MutableList<EmbedRequest> = mutableListOf()

    fun addFile(name: String, content: InputStream) {
        files += name to content
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
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
                username = _username,
                avatar = _avatarUrl,
                tts = _tts,
                embeds = Optional.missingOnEmpty(embeds),
                allowedMentions = _allowedMentions
            ),
            files,
        )

}

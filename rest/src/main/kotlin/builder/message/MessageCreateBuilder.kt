package dev.kord.rest.builder.message

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.AllowedMentionType
import dev.kord.common.entity.AllowedMentions
import dev.kord.common.entity.DiscordMessageReference
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.MessageCreateRequest
import dev.kord.rest.json.request.MultipartMessageCreateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
class MessageCreateBuilder : RequestBuilder<MultipartMessageCreateRequest> {

    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _nonce: Optional<String> = Optional.Missing()
    var nonce: String? by ::_nonce.delegate()

    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    var tts: Boolean? by ::_tts.delegate()

    private var _embed: Optional<EmbedBuilder> = Optional.Missing()
    var embed: EmbedBuilder? by ::_embed.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    val files: MutableList<Pair<String, InputStream>> = mutableListOf()

    private var _messageReference: OptionalSnowflake = OptionalSnowflake.Missing

    private var _failIfNotExists: OptionalBoolean = OptionalBoolean.Missing
    /**
     * The id of the message being replied to.
     * Requires the [ReadMessageHistory][dev.kord.common.entity.Permission.ReadMessageHistory] permission.
     *
     * Replying will not mention the author by default,
     * set [AllowedMentionsBuilder.repliedUser] to `true` via [allowedMentions]  to mention the author.
     */
    var messageReference: Snowflake? by ::_messageReference.delegate()

    var failIfNotExists: Boolean? by ::_failIfNotExists.delegate()

    @OptIn(ExperimentalContracts::class)
    inline fun embed(block: EmbedBuilder.() -> Unit) {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        embed = (embed ?: EmbedBuilder()).apply(block)
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
    inline fun allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
        allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
    }

    override fun toRequest(): MultipartMessageCreateRequest = MultipartMessageCreateRequest(
        MessageCreateRequest(
            _content,
            _nonce,
            _tts,
            _embed.map { it.toRequest() },
            _allowedMentions.map { it.build() },
            _messageReference.map { DiscordMessageReference(id = OptionalSnowflake.Value(it), failIfNotExists = _failIfNotExists) }
        ),
        files
    )

}

/**
 * The mentions that should trigger a ping. See the [Discord documentation](https://discord.com/developers/docs/resources/channel#allowed-mentions-object).
 *
 */
class AllowedMentionsBuilder {
    /**
     * The roles that should be mentioned in this message, any id that is mentioned in this list but not present in the
     * [MessageCreateBuilder] will be ignored.
     */
    val roles: MutableSet<Snowflake> = mutableSetOf()

    /**
     * The users that should be mentioned in this message, any id that is mentioned in this list but not present in the
     * [MessageCreateBuilder] will be ignored.
     */
    val users: MutableSet<Snowflake> = mutableSetOf()

    /**
     * The types of pings that should trigger in this message. Selecting [AllowedMentionType.UserMentions] or [AllowedMentionType.RoleMentions]
     * together with any value in [users] or [roles] respectively will result in an error.
     */
    val types: MutableSet<AllowedMentionType> = mutableSetOf()

    private var _repliedUser: OptionalBoolean = OptionalBoolean.Missing

    /**
     * Whether to mention the user being replied to.
     *
     * Only set this if [MessageCreateBuilder.messageReference] is not `null`.
     */
    var repliedUser: Boolean? by ::_repliedUser.delegate()

    /**
     * Adds the type to the list of types that should receive a ping.
     */
    operator fun AllowedMentionType.unaryPlus() {
        types.add(this)
    }

    /**
     * Adds the type to the list of types that should receive a ping.
     */
    fun add(type: AllowedMentionType) {
        type.unaryPlus()
    }

    fun build(): AllowedMentions = AllowedMentions(
        parse = types.toList(),
        users = users.map { it.asString },
        roles = roles.map { it.asString },
        repliedUser = _repliedUser
    )

}

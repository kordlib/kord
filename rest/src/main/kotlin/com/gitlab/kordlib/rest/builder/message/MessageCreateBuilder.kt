package com.gitlab.kordlib.rest.builder.message

import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.AllowedMentions
import com.gitlab.kordlib.rest.json.request.MessageCreateRequest
import com.gitlab.kordlib.rest.json.request.MultipartMessageCreateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

@KordDsl
class MessageCreateBuilder : RequestBuilder<MultipartMessageCreateRequest> {
    var content: String? = null
    var nonce: String? = null
    var tts: Boolean? = null
    var embed: EmbedBuilder? = null
    var allowedMentions: AllowedMentionsBuilder? = null
    val files: MutableList<Pair<String, InputStream>> = mutableListOf()

    inline fun embed(block: EmbedBuilder.() -> Unit) {
        embed = (embed ?: EmbedBuilder()).apply(block)
    }

    fun addFile(name: String, content: InputStream) {
        files += name to content
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

    /**
     * Configures the mentions that should trigger a ping. Not calling this function will result in the default behavior
     * (ping everything), calling this function but not configuring it before the request is build will result in all
     * pings being ignored.
     */
    inline fun allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
        allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
    }

    override fun toRequest(): MultipartMessageCreateRequest = MultipartMessageCreateRequest(
            MessageCreateRequest(content, nonce, tts, embed?.toRequest(), allowedMentions?.build()),
            files
    )

}

/**
 * The mentions that should trigger a ping. See the [Discord documentation](https://discordapp.com/developers/docs/resources/channel#allowed-mentions-object).
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
     * The types of pings that should trigger in this message. Selecting [MentionTypes.Users] or [MentionTypes.Roles]
     * together with any value in [users] or [roles] respectively will result in an error.
     */
    val types: MutableSet<MentionTypes> = mutableSetOf()

    /**
     * Adds the type to the list of types that should receive a ping.
     */
    operator fun MentionTypes.unaryPlus() = types.add(this)

    fun build(): AllowedMentions = AllowedMentions(
            parse = types.map { it.serialName },
            users = users.map { it.value },
            roles = roles.map { it.value }
    )

}


enum class MentionTypes(val serialName: String) {
    Roles("roles"), Users("users"), Everyone("everyone")
}

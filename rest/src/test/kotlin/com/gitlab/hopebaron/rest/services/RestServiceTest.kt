package com.gitlab.hopebaron.rest.services

import com.gitlab.hopebaron.common.entity.ChannelType
import com.gitlab.hopebaron.common.entity.DefaultMessageNotificationLevel
import com.gitlab.hopebaron.common.entity.ExplicitContentFilter
import com.gitlab.hopebaron.common.entity.VerificationLevel
import com.gitlab.hopebaron.rest.json.request.*
import com.gitlab.hopebaron.rest.ratelimit.ExclusionRequestHandler
import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.service.RestClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.request.header
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestServiceTest {

    private val token = System.getenv("token")

    private lateinit var requestHandler: RequestHandler

    private lateinit var client: HttpClient

    private lateinit var rest: RestClient

    //created guild id
    private lateinit var guildId: String

    //created channel id
    private lateinit var channelId: String

    @BeforeAll
    fun setup() {
        client = HttpClient(CIO) {
            defaultRequest {
                header("Authorization", "Bot $token")
            }
        }
        requestHandler = ExclusionRequestHandler(client)
        rest = RestClient(ExclusionRequestHandler(client))
    }

    @Test
    @Order(1)
    fun `create guild`() = runBlocking {
        val region = rest.voice.getVoiceRegions().first()

        val request = CreateGuildRequest(
                "TEST GUILD",
                region.id,
                null,
                VerificationLevel.None,
                DefaultMessageNotificationLevel.AllMessages,
                ExplicitContentFilter.AllMembers,
                emptyList(),
                emptyList()
        )

        val guild = rest.guild.createGuild(request)

        guildId = guild.id
    }

    @Test
    @Order(2)
    fun `create invite`() = runBlocking {
        val generalId = rest.guild.getGuildChannels(guildId).first { it.type == ChannelType.GuildText }.id

        rest.channel.createInvite(generalId, InviteCreateRequest())

        Unit
    }

    @Test
    @Order(3)
    fun `create channel`() = runBlocking {
        val channel = rest.guild.createGuildChannel(guildId, CreateGuildChannelRequest("BOT TEST RUN"))
        channelId = channel.id

        rest.channel.getChannel(channel.id)

        Unit
    }

    @Test
    @Order(4)
    fun `reaction in channel`() = runBlocking {
        with(rest.channel) {
            val message = createMessage(channelId, MessageCreateRequest("TEST"))
            editMessage(channelId, message.id, MessageEditRequest("EDIT TEST"))

            createReaction(channelId, message.id, "\ud83d\udc4e")
            deleteOwnReaction(channelId, message.id, "\ud83d\udc4e")

            createReaction(channelId, message.id, "\ud83d\udc4d")
            deleteReaction(channelId, message.id, message.author.id, "\ud83d\udc4d")

            createReaction(channelId, message.id, "\ud83d\udc4e")
            deleteAllReactions(channelId, message.id)

            deleteMessage(channelId, message.id)
        }
    }

    @Test
    @Order(5)
    fun `message in channel`() = runBlocking {
        with(rest.channel) {
            triggerTypingIndicator(channelId)

            val message = createMessage(channelId, MessageCreateRequest("TEST"))

            getMessage(channelId, message.id)

            deleteMessage(channelId, message.id)

            createMessage(channelId, MessageCreateRequest("TEST"))
            createMessage(channelId, MultipartMessageCreateRequest(MessageCreateRequest("TEST")))

            val messages = getMessages(channelId)

            this.bulkDelete(channelId, BulkDeleteRequest(messages.map { it.id }))
        }
    }

    @Test
    @Order(Int.MAX_VALUE - 2)
    fun `delete channel`() = runBlocking {
        rest.channel.deleteChannel(channelId)

        Unit
    }

    @Test
    @Order(Int.MAX_VALUE - 1)
    fun `audit logs`() = runBlocking {
        rest.auditLog.getAuditLogs(guildId)

        Unit
    }


    @Test
    @Order(Int.MAX_VALUE)
    fun `delete guild`() = runBlocking {
        rest.guild.deleteGuild(guildId)

        Unit
    }

    @AfterAll
    fun close() {
        client.close()
    }

}


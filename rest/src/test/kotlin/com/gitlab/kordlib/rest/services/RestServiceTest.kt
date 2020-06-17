package com.gitlab.kordlib.rest.services

import com.gitlab.kordlib.common.Color
import com.gitlab.kordlib.common.entity.*
import com.gitlab.kordlib.rest.Image
import com.gitlab.kordlib.rest.json.request.*
import com.gitlab.kordlib.rest.ratelimit.ExclusionRequestRateLimiter
import com.gitlab.kordlib.rest.request.KtorRequestHandler
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.service.RestClient
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.util.*

fun image(path: String): String {
    val loader = Unit::class.java.classLoader
    val image = loader?.getResource(path)?.readBytes()
    val encoded = Base64.getEncoder().encodeToString(image)
    val imageType = path.split(".").last()
    return "data:image/$imageType;base64, $encoded"
}

fun imageBinary(path: String): Image {
    val loader = Unit::class.java.classLoader
    val image = loader?.getResource(path)?.readBytes()
    val imageType = path.split(".").last()
    val format = Image.Format.fromContentType("image/$imageType")
    return Image.raw(image!!, format)
}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIfEnvironmentVariable(named = "TARGET_BRANCH", matches = "master")
class RestServiceTest {

    private val publicGuildId = Snowflake(322850917248663552)

    private val token = System.getenv("KORD_TEST_TOKEN")

    private lateinit var requestHandler: RequestHandler

    private lateinit var client: HttpClient

    private lateinit var rest: RestClient

    //created guild id
    private lateinit var guildId: String

    //created channel id
    private lateinit var channelId: String

    private lateinit var userId: String

    @BeforeAll
    fun setup() = runBlocking {
        requestHandler = KtorRequestHandler(token, ExclusionRequestRateLimiter())
        rest = RestClient(requestHandler)

        userId = rest.user.getCurrentUser().id
    }

    @Test
    @Order(1)
    fun `create guild`() = runBlocking {
        val region = rest.voice.getVoiceRegions().first()
        val guilds = rest.user.getCurrentUserGuilds()

        guilds.filter { it.owner == true }.forEach {
            rest.guild.deleteGuild(it.id)
        }

        val guild = rest.guild.createGuild {
            name = "TEST GUILD"
            this.region = region.id
            verificationLevel = VerificationLevel.None
            defaultMessageNotificationLevel = DefaultMessageNotificationLevel.AllMessages
            explicitContentFilter = ExplicitContentFilter.AllMembers
        }

        guildId = guild.id

        rest.guild.getGuild(guildId, true)

        rest.guild.modifyGuild(guildId) {
            name = "Edited Guild Test"
        }

        rest.guild.getGuildVoiceRegions(guildId).first()


        Unit
    }

    @Test
    @Order(2)
    fun `create invite`() = runBlocking {
        val generalId = rest.guild.getGuildChannels(guildId).first { it.type == ChannelType.GuildText }.id

        val invite = rest.channel.createInvite(generalId)

        rest.invite.getInvite(invite.code, true)

        Unit
    }

    @Test
    @Order(3)
    fun `create channel`() = runBlocking {
        val channel = rest.guild.createGuildChannel(guildId, GuildCreateChannelRequest("BOT TEST RUN"))
        channelId = channel.id

        rest.channel.getChannel(channel.id)

        Unit
    }

    @Test
    @Order(4)
    fun `reaction in channel`() = runBlocking {
        with(rest.channel) {
            val message = createMessage(channelId) {
                content = "TEST <@&${guildId}>"
            }
            editMessage(channelId, message.id) {
                content = "EDIT TEST"
            }

            createReaction(channelId, message.id, "\ud83d\udc4e")
            deleteOwnReaction(channelId, message.id, "\ud83d\udc4e")

            createReaction(channelId, message.id, "\ud83d\udc4d")
            deleteReaction(channelId, message.id, message.author!!.id, "\ud83d\udc4d")

            createReaction(channelId, message.id, "\ud83d\udc4e")
            getReactions(channelId, message.id, "\ud83d\udc4e")
            deleteAllReactions(channelId, message.id)

            createReaction(channelId, message.id, "\ud83d\udc4e")
            deleteAllReactionsForEmoji(channelId, message.id, "\ud83d\udc4e")

            deleteMessage(channelId, message.id)
        }
    }

    @Test
    @Order(5)
    fun `message in channel`() = runBlocking {
        with(rest.channel) {
            triggerTypingIndicator(channelId)

            val message = createMessage(channelId) {
                content = "TEST"

                addFile("test.txt", ClassLoader.getSystemResourceAsStream("images/kord.png")!!)
            }

            getMessage(channelId, message.id)

            deleteMessage(channelId, message.id)

            repeat(2) {
                createMessage(channelId) {
                    content = "TEST"
                }
            }

            val messages = getMessages(channelId)

            bulkDelete(channelId, BulkDeleteRequest(messages.map { it.id }))

        }
    }

    @Test
    @Order(6)
    fun `pinned messages in channel`() = runBlocking {
        with(rest.channel) {
            val pinnedMessage = createMessage(channelId) {
                content = "TEST"
            }

            addPinnedMessage(channelId, pinnedMessage.id)

            getChannelPins(channelId)

            deletePinnedMessage(channelId, pinnedMessage.id)

            Unit
        }

    }

    @Test
    @Order(7)
    fun `invites in channel`() = runBlocking {
        with(rest.channel) {
            getChannelInvites(channelId)

            Unit
        }

    }

    @Test
    @Order(8)
    fun `permissions in channels`() = runBlocking {
        val role = rest.guild.createGuildRole(guildId)
        with(rest.channel) {
            val allow = Permissions { +Permission.CreateInstantInvite }
            val deny = Permissions { +Permission.SendTTSMessages }

            editChannelPermissions(channelId, role.id, ChannelPermissionEditRequest(allow, deny, "role"))

            deleteChannelPermission(channelId, role.id)

        }
    }

    @Test
    @Order(9)
    fun `modify channels`() = runBlocking {
        with(rest.channel) {
            //TODO Test Put method

            patchChannel(channelId, ChannelModifyPatchRequest("PATCH"))

            Unit

        }
    }

//TODO Add Group Channel Tests

    @Test
    @Disabled("Member is not added to guild yet due to Guild#addGuildMember")
    @Order(11)
    fun `member in guild`() = runBlocking {
        with(rest.guild) {
            @Suppress("UNUSED_VARIABLE") val members = getGuildMembers(guildId)
            //TODO add member to guild

            modifyGuildMember(guildId, userId) {
                nickname = "My nickname"
                muted = true
                deafened = true
            }

            getGuildMember(guildId, userId)

            //deleteGuildMember(guildId, user)

            modifyCurrentUserNickname(guildId, CurrentUserNicknameModifyRequest("Kord"))

            Unit
        }
    }

    @Test
    @Order(12)
    fun `roles in guild`() = runBlocking {
        with(rest.guild) {
            val role = createGuildRole(guildId) {
                name = "Sudoers"
                permissions = Permissions { +Permission.Administrator }
                color = Color(0xFF0000)
                hoist = true
                mentionable = true
            }

            modifyGuildRole(guildId, role.id) {
                name = "Edited role"
            }

            addRoleToGuildMember(guildId, userId, role.id)

            deleteRoleFromGuildMember(guildId, userId, role.id)

            modifyGuildRolePosition(guildId) {
                move(Snowflake(role.id) to 0)
            }

            getGuildRoles(guildId)

            deleteGuildRole(guildId, role.id)

            Unit
        }
    }

    @Test
    @Disabled("User to ban is not there.")
    @Order(13)
    fun `bans in guild`() = runBlocking {

        with(rest.guild) {

            //addGuildBan(guildId, user, AddGuildBanRequest())

            getGuildBans(guildId)

            //getGuildBan(guildId, user)

            //deleteGuildBan(guildId, user)

            Unit

        }
    }

    @Test
    @Order(14)
    fun `invites in guild`() = runBlocking {
        with(rest.guild) {
            //            getVanityInvite(guildId)  //¯\_(ツ)_/¯
            getGuildInvites(guildId)

            Unit
        }
    }

    @Test
    @Order(15)
    fun `prune members in guilds`() = runBlocking {
        with(rest.guild) {
            getGuildPruneCount(guildId)
            beginGuildPrune(guildId)

            Unit
        }
    }

    @Test
    @Disabled
    @Order(16)
    fun `integrations in guild`() = runBlocking {

        //TODO
    }

    @Test
    @Order(17)
    fun `embeds in guild`() = runBlocking {

        with(rest.guild) {

            modifyGuildEmbed(guildId, GuildEmbedModifyRequest(true, channelId))

            getGuildEmbed(guildId)

            Unit
        }
    }

    @Test
    @Order(18)
    fun `user`() = runBlocking {
        with(rest.user) {
            getCurrentUser()

            getCurrentUserGuilds()

            modifyCurrentUser {
                username = "Happy Kord"
            }

            getUserConnections()

            // getUser(user) // works, but can't be tested with bots client id, use your own id

            // createDM(CreateDMRequest(user)) // works, but can't be tested with bots client id use your own id

            //TODO test groups

            Unit
        }
    }

    @Test
    @Order(19)
    fun `emojis in guilds`() = runBlocking {
        with(rest.emoji) {

            val emoji = createEmoji(guildId) {
                name = "kord"
                image = imageBinary("images/kord.png")
                roles = setOf(Snowflake(guildId))
            }

            modifyEmoji(guildId, emoji.id!!) {
                name = "edited"
            }

            getEmojis(guildId)

            getEmoji(guildId, emoji.id!!)

            deleteEmoji(guildId, emoji.id!!)

            Unit
        }
    }

    @Test
    @Order(20)
    fun `get public guild preview`() = runBlocking {
        val preview = rest.guild.getGuildPreview(publicGuildId.value)
        Unit
    }

    @Test
    @Order(20)
    fun `webhooks tests`() = runBlocking {
        val webhook = rest.webhook.createWebhook(channelId) {
            name = "Test webhook"
        }

        rest.webhook.executeWebhook(webhook.id, webhook.token!!, wait = true) {
            content = "hello world!"
        }!! //angry shouting to make sure this doesn't return null

        rest.webhook.executeWebhook(webhook.id, webhook.token!!, wait = false) {
            content = "hello world, I don't want to hear back from you!"
        }

        rest.webhook.deleteWebhook(webhook.id, reason = "test")

        Unit
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

}


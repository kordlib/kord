package com.gitlab.kordlib.core.rest

import com.gitlab.kordlib.common.Color
import com.gitlab.kordlib.common.entity.*
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.*
import com.gitlab.kordlib.core.behavior.channel.createMessage
import com.gitlab.kordlib.core.behavior.channel.createWebhook
import com.gitlab.kordlib.core.behavior.channel.edit
import com.gitlab.kordlib.core.behavior.channel.editRolePermission
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.ReactionEmoji
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.rest.Image
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.request.RestRequestException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

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

    private lateinit var kord: Kord

    //created guild id
    private lateinit var guildId: Snowflake
    private lateinit var guild: Guild

    //created channel id
    private lateinit var channelId: Snowflake
    private lateinit var channel: TextChannel


    private lateinit var userId: Snowflake

    @BeforeAll
    fun setup() = runBlocking {
        kord = Kord.restOnly(token)

        userId = kord.getSelf().id
    }


    @Test
    @Order(1)
    fun `create guild`() = runBlocking {
        val region = kord.regions.first()
        val guilds = kord.guilds.toList()

        guilds.filter { it.isOwner }.forEach {
            it.delete()
        }

        val guild = kord.createGuild {
            name = "TEST GUILD"
            this.region = region.id
            verificationLevel = VerificationLevel.None
            defaultMessageNotificationLevel = DefaultMessageNotificationLevel.AllMessages
            explicitContentFilter = ExplicitContentFilter.AllMembers
        }

        guildId = guild.id

        this@RestServiceTest.guild = kord.getGuild(guildId)!!

        guild.edit {
            name = "Edited Guild Test"
        }

        guild.regions.first()

        Unit
    }

    @Test
    @Order(2)
    fun `create invite`() = runBlocking {
        val channel = guild.channels.filterIsInstance<GuildMessageChannel>().first()
        val invite = channel.createInvite()

        guild.getInvite(invite.code)
        Unit
    }

    @Test
    @Order(3)
    fun `create channel`() = runBlocking {
        val channel = guild.createTextChannel {
            name = "BOT TEST RUN"
            reason = """
                a multiline
                
                reason
                
                to check if encoding is okay
            """.trimIndent()
        }
        channelId = channel.id
        this@RestServiceTest.channel = channel

        guild.getChannelOf<TextChannel>(channel.id)

        Unit
    }

    @Test
    @Order(4)
    fun `reaction in channel`(): Unit = runBlocking {
        val message = channel.createMessage("TEST <@&${guildId}>")

        message.edit { content = "EDIT TEST" }

        val emoji = ReactionEmoji.Unicode("\ud83d\udc4e")
        message.addReaction(emoji)
        message.deleteOwnReaction(emoji)

        message.addReaction(emoji)
        message.deleteReaction(kord.selfId, emoji)

        message.addReaction(emoji)
        message.getReactors(emoji)
        message.deleteAllReactions()

        message.addReaction(emoji)
        message.deleteReaction(emoji)

        message.delete()
    }

    @Test
    @Order(5)
    fun `message in channel`() = runBlocking {
        channel.type()

        val message = channel.createMessage {
            content = "TEST"

            addFile("test.txt", ClassLoader.getSystemResourceAsStream("images/kord.png")!!)
        }

        channel.getMessage(message.id)

        channel.deleteMessage(message.id)

        repeat(2) {
            channel.createMessage {
                content = "TEST"
            }
        }

        val messages = channel.messages.toList()

        channel.bulkDelete(messages.map { it.id })
    }

    @Test
    @Order(6)
    fun `pinned messages in channel`() = runBlocking {
        val message = channel.createMessage { content = "TEST" }

        message.pin()
        channel.pinnedMessages.toList()

        message.unpin()
        Unit
    }

    @Test
    @Order(7)
    fun `invites in channel`(): Unit = runBlocking {
        channel.invites.toList()
        Unit
    }

    @Test
    @Order(8)
    fun `permissions in channels`(): Unit = runBlocking {
        val role = guild.createRole()

        channel.editRolePermission(role.id) {
            allowed = Permissions { +Permission.CreateInstantInvite }
            denied = Permissions { +Permission.SendTTSMessages }
        }

        channel.asChannel().permissionOverwrites.first().delete()
    }

    @Test
    @Order(9)
    fun `modify channels`(): Unit = runBlocking {
        channel.edit { name = "PATCH" }
        //TODO Test Put method
    }

//TODO Add Group Channel Tests

    @Test
    @Disabled("Member is not added to guild yet due to Guild#addGuildMember")
    @Order(11)
    fun `member in guild`() = runBlocking {
        guild.members.toList()
        //TODO add member to guild

        guild.getMember(userId).edit {
            nickname = "my nickname"
            muted = true
            deafened = true
        }

        guild.editSelfNickname("Kord")
        //deleteGuildMember(guildId, user)
        Unit
    }

    @Test
    @Order(12)
    fun `roles in guild`() = runBlocking {
        val role = guild.createRole {
            name = "Sudoers"
            permissions = Permissions { +Permission.Administrator }
            color = Color(0xFF0000)
            hoist = true
            mentionable = true
        }

        role.edit {
            name = "Edited role"
        }

        val member = guild.getMember(userId)
        member.addRole(role.id)
        member.removeRole(role.id)

        role.changePosition(0)

        guild.roles.toList()

        role.delete()
        Unit
    }

    @Test
    @Disabled("User to ban is not there.")
    @Order(13)
    fun `bans in guild`(): Unit = runBlocking {

        //addGuildBan(guildId, user, AddGuildBanRequest())
        guild.bans.toList()

        //getGuildBan(guildId, user)

        //deleteGuildBan(guildId, user)

        Unit
    }

    @Test
    @Order(14)
    fun `invites in guild`(): Unit = runBlocking {
        guild.getVanityUrl()

        guild.invites.toList()
    }

    @Test
    @Order(15)
    fun `prune members in guilds`(): Unit = runBlocking {
        guild.getPruneCount()
        guild.prune()
    }

    @Test
    @Disabled
    @Order(16)
    fun `integrations in guild`() = runBlocking {

        //TODO
    }

    @Test
    @Order(17)
    fun `embeds in guild`(): Unit = runBlocking {

        val widget = guild.getWidget().edit {
            this.enabled
        }

        Unit
    }

    @Test
    @Order(18)
    fun `user`(): Unit = runBlocking {
        kord.getSelf()
        kord.guilds.toList()

        kord.editSelf { username = "Happy Kord" }


        kord.rest.user.getUserConnections()//not sure this should be in core

        // getUser(user) // works, but can't be tested with bots client id, use your own id

        // createDM(CreateDMRequest(user)) // works, but can't be tested with bots client id use your own id

        //TODO test groups

        Unit
    }

    @Test
    @Order(19)
    fun `emojis in guilds`(): Unit = runBlocking {
        val emoji = guild.createEmoji {
            name = "kord"
            image = imageBinary("images/kord.png")
            roles = setOf(guildId)
        }

        emoji.edit { name = "edited" }

        guild.emojis.toList()
        guild.getEmoji(emoji.id)
        emoji.delete()

        Unit
    }

    @Test
    @Order(20)
    fun `get public guild preview`() = runBlocking {
        kord.getGuildPreview(publicGuildId)
        Unit
    }

    @Test
    @Order(20)
    fun `webhooks tests`() = runBlocking {
        val webhook = channel.createWebhook { name = "Test webhook" }

        webhook.execute(webhook.token!!) {
            content = "hello world!"
        }

        webhook.executeIgnored(webhook.token!!) {
            content = "hello world!"
        }

        webhook.delete(reason = "test")
        Unit
    }

    @Test
    @Order(21)
    fun `errors are thrown correctly`() = runBlocking {
        val exception = assertThrows<RestRequestException> {
            runBlocking { kord.getChannel(Snowflake(-500)) }
        }

        assert(exception.error != null)
    }

    @Test
    @Order(Int.MAX_VALUE - 2)
    fun `delete channel`() = runBlocking {
        channel.delete()
        Unit
    }

    @Test
    @Order(Int.MAX_VALUE - 1)
    fun `audit logs`() = runBlocking {
        //TODO("core audit logs")
        Unit
    }


    @Test
    @Order(Int.MAX_VALUE)
    fun `delete guild`() = runBlocking {
        guild.delete()

        Unit
    }

}

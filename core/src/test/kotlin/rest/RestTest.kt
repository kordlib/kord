package dev.kord.core.rest

import dev.kord.common.Color
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.core.Kord
import dev.kord.core.behavior.*
import dev.kord.core.behavior.channel.*
import dev.kord.core.entity.Guild
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.createInvite
import dev.kord.rest.Image
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.request.RestRequestException
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.test.assertEquals

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
    @OptIn(KordExperimental::class)
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

        val guild = kord.createGuild("TEST GUILD") {
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
        val channel = guild.createTextChannel("BOT TEST RUN") {
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

        run {
            val message = channel.createMessage {
                content = "TEST"
            }
            val reply = message.reply {
                content = "TEST REPLY"
            }

            assert(reply.referencedMessage?.id == message.id)
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
    @Order(11)
    fun `member in guild`() = runBlocking {
        guild.members.toList()
        //TODO add member to guild

        guild.getMember(userId)

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

        @Suppress("UNUSED_VARIABLE")
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
        val emoji = guild.createEmoji("kord", imageBinary("images/kord.png")) {
            roles.add(guildId)
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
        val webhook = channel.createWebhook("Test webhook")

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

    @Order(21)
    fun `category channel creation`(): Unit = runBlocking {
        val category = guild.createCategory("my category")

        val textChannel = category.createTextChannel("test child text channel")
        assert(textChannel.category == category)

        val voiceChannel = category.createVoiceChannel("test child voice channel")
        assert(voiceChannel.category == category)
    }

    @Test
    @Order(22)
    fun `message with file and content serializes correctly`(): Unit = runBlocking {
        val message = channel.createMessage {
            content = "TEST"

            addFile("test.txt", ClassLoader.getSystemResourceAsStream("images/kord.png")!!)
        }

        assertEquals("TEST", message.content)
        assertEquals(1, message.attachments.size)
        assertEquals("test.txt", message.attachments.first().filename)
    }

    @Test
    @Order(23)
    fun `message with only file correctly`(): Unit = runBlocking {
        val message = channel.createMessage {
            addFile("test.txt", ClassLoader.getSystemResourceAsStream("images/kord.png")!!)
        }

        assertEquals(1, message.attachments.size)
        assertEquals("test.txt", message.attachments.first().filename)
    }

    @Test
    @Order(24)
    fun `message with only content serializes correctly`(): Unit = runBlocking {
        val message = channel.createMessage {
            content = "TEST"

            addFile("test.txt", ClassLoader.getSystemResourceAsStream("images/kord.png")!!)
        }

        assertEquals("TEST", message.content)
    }

    @Test
    @Order(25)
    @OptIn(KordExperimental::class)
    fun `channel moves in guild`(): Unit = runBlocking {
        val category = guild.createCategory("move category")
        val textChannel = guild.createTextChannel("move me to a category")

        guild.swapChannelPositions {
            move(textChannel.id){ parentId = category.id }
        }

        val currentTextChannel = guild.getChannelOf<TextChannel>(textChannel.id)
        assertEquals(category.id,currentTextChannel.categoryId)
    }

    @OptIn(KordPreview::class)
    fun `guild application commands`(): Unit = runBlocking {
        val command = guild.createApplicationCommand("test", "automated test"){
            group("test-group", "automated test") {
                subCommand("test-sub-command", "automated test") {
                    int("integer", "test choice") {
                        choice("one", 1)
                        choice("two", 2)
                    }
                }
            }

            subCommand("test-sub-command", "automated test") {
                int("integer", "test choice")
            }
        }

        assertEquals("test-group", command.name)
        assertEquals("automated test", command.description)
        assertEquals(1, command.subCommands.size)
        assertEquals(1, command.groups.size)

        assertEquals(1, command.subCommands.values.first().parameters.size)
        val parameter = command.subCommands.values.first().parameters.values.first()
        assertEquals(1, parameter.choices.size)
        assertEquals("1", parameter.choices.values.first())

        assertEquals("test-group", command.groups.values.first().name)
        assertEquals("test-sub-command", command.subCommands.values.first().name)

        val updated = command.edit {
            description = "other description"
        }

        assertEquals("other description", updated.description)

        updated.delete()
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
        guild.getAuditLogEntries().toList()
        Unit
    }

    @Test
    @Order(Int.MAX_VALUE)
    fun `delete guild`() = runBlocking {
        guild.delete()

        Unit
    }

}

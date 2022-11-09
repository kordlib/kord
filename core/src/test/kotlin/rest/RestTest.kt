package rest

import BoxedSnowflake
import dev.kord.common.Color
import dev.kord.common.entity.*
import dev.kord.core.Kord
import dev.kord.core.behavior.*
import dev.kord.core.behavior.channel.*
import dev.kord.core.behavior.channel.threads.edit
import dev.kord.core.entity.Guild
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.rest.Image
import dev.kord.rest.builder.interaction.group
import dev.kord.rest.builder.interaction.int
import dev.kord.rest.builder.interaction.subCommand
import io.ktor.client.request.forms.*
import io.ktor.util.cio.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.io.path.toPath
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun imageBinary(path: String): Image {
    val loader = Unit::class.java.classLoader
    val image = loader?.getResource(path)?.readBytes()
    val imageType = path.split(".").last()
    val format = Image.Format.fromContentType("image/$imageType")
    return Image.raw(image!!, format)
}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
class RestServiceTest {

    // should we run checks which require boosts on the public guild?
    private val boostEnabled = System.getenv("KORD_BOOST_ENABLED")?.equals("true", ignoreCase = true) ?: false

    private val publicGuildId = Snowflake(322850917248663552u)

    private val token = System.getenv("KORD_TEST_TOKEN")

    private lateinit var kord: Kord

    //created guild id
    private lateinit var guildId: BoxedSnowflake
    private lateinit var guild: Guild

    //created channel id
    private lateinit var channelId: BoxedSnowflake
    private lateinit var channel: TextChannel


    private lateinit var userId: BoxedSnowflake

    @BeforeAll
    fun setup() = runBlocking {
        kord = Kord.restOnly(token)

        userId = BoxedSnowflake(kord.getSelf().id)
    }


    @Test
    @Order(1)
    fun `create guild`(): Unit = runBlocking {
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

        guildId = BoxedSnowflake(guild.id)

        this@RestServiceTest.guild = kord.getGuildOrThrow(guildId.value)

        guild.edit {
            name = "Edited Guild Test"
        }

        guild.regions.first()
    }

    @Test
    @Order(2)
    fun `create invite`(): Unit = runBlocking {
        val channel = guild.channels.filterIsInstance<TopGuildMessageChannel>().first()
        val invite = channel.createInvite()

        guild.getInvite(invite.code)
    }

    @Test
    @Order(3)
    fun `create channel`(): Unit = runBlocking {
        val channel = guild.createTextChannel("BOT TEST RUN") {
            reason = """
                a multiline
                
                reason
                
                to check if encoding is okay
            """.trimIndent()
        }
        channelId = BoxedSnowflake(channel.id)
        this@RestServiceTest.channel = channel

        guild.getChannelOf<TextChannel>(channel.id)
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

            addFile(
                "test.txt",
                ChannelProvider { ClassLoader.getSystemResource("images/kord.png").toURI().toPath().readChannel() }
            )
        }

        channel.getMessage(message.id)

        channel.deleteMessage(message.id)

        run {
            val referenceMessage = channel.createMessage {
                content = "TEST"
            }
            val reply = referenceMessage.reply {
                content = "TEST REPLY"
            }

            assert(reply.referencedMessage?.id == referenceMessage.id)
        }

        val messages = channel.messages.toList()

        channel.bulkDelete(messages.map { it.id })
    }

    @Test
    @Order(6)
    fun `pinned messages in channel`(): Unit = runBlocking {
        val message = channel.createMessage { content = "TEST" }

        message.pin()
        channel.pinnedMessages.toList()

        message.unpin()
    }

    @Test
    @Order(7)
    fun `invites in channel`(): Unit = runBlocking {
        channel.invites.toList()
    }

    @Test
    @Order(8)
    fun `permissions in channels`(): Unit = runBlocking {
        val role = guild.createRole()

        channel.editRolePermission(role.id) {
            allowed = Permissions { +Permission.CreateInstantInvite }
            denied = Permissions { +Permission.SendTTSMessages }
        }

        channel.fetchChannel().permissionOverwrites.first().delete()
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
    fun `member in guild`(): Unit = runBlocking {
        guild.members.toList()
        //TODO add member to guild

        guild.getMember(userId.value)

        guild.editSelfNickname("Kord")
        //deleteGuildMember(guildId, user)
    }

    @Test
    @Order(12)
    fun `roles in guild`(): Unit = runBlocking {
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

        val member = guild.getMember(userId.value)
        member.addRole(role.id)
        member.removeRole(role.id)

        role.changePosition(0)

        guild.roles.toList()

        role.delete()
    }

    @Test
    @Disabled("User to ban is not there.")
    @Order(13)
    fun `bans in guild`(): Unit = runBlocking {

        //addGuildBan(guildId, user, AddGuildBanRequest())
        guild.bans.toList()

        //getGuildBan(guildId, user)

        //deleteGuildBan(guildId, user)
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
    }

    @Test
    @Order(18)
    fun user(): Unit = runBlocking {
        kord.getSelf()
        kord.guilds.toList()

        kord.editSelf { username = "Happy Kord" }


        kord.rest.user.getUserConnections()//not sure this should be in core

        // getUser(user) // works, but can't be tested with bots client id, use your own id

        // createDM(CreateDMRequest(user)) // works, but can't be tested with bots client id use your own id

        //TODO test groups
    }

    @Test
    @Order(19)
    fun `emojis in guilds`(): Unit = runBlocking {
        val emoji = guild.createEmoji("kord", imageBinary("images/kord.png")) {
            roles.add(guildId.value)
        }

        emoji.edit { name = "edited" }

        guild.emojis.toList()
        guild.getEmoji(emoji.id)
        emoji.delete()
    }

    @Test
    @Order(20)
    fun `get public guild preview`(): Unit = runBlocking {
        kord.getGuildPreview(publicGuildId)
    }

    @Test
    @Order(20)
    fun `webhooks tests`(): Unit = runBlocking {
        val webhook = channel.createWebhook("Test webhook")

        webhook.execute(webhook.token!!) {
            content = "hello world!"
        }

        webhook.executeIgnored(webhook.token!!) {
            content = "hello world!"
        }

        val message = webhook.execute(webhook.token!!) {
            content = "a"
        }

        message.edit(webhook.id, webhook.token!!) {
            content = "b"
        }

        webhook.delete(reason = "test")
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

            addFile(
                "test.txt",
                ChannelProvider { ClassLoader.getSystemResource("images/kord.png").toURI().toPath().readChannel() }
            )
        }

        assertEquals("TEST", message.content)
        assertEquals(1, message.attachments.size)
        assertEquals("test.txt", message.attachments.first().filename)
    }

    @Test
    @Order(23)
    fun `message with only file correctly`(): Unit = runBlocking {
        val message = channel.createMessage {
            addFile(
                "test.txt",
                ChannelProvider { ClassLoader.getSystemResource("images/kord.png").toURI().toPath().readChannel() }
            )
        }

        assertEquals(1, message.attachments.size)
        assertEquals("test.txt", message.attachments.first().filename)
    }

    @Test
    @Order(24)
    fun `message with only content serializes correctly`(): Unit = runBlocking {
        val message = channel.createMessage {
            content = "TEST"

            addFile(
                "test.txt",
                ChannelProvider { ClassLoader.getSystemResource("images/kord.png").toURI().toPath().readChannel() }
            )
        }

        assertEquals("TEST", message.content)
    }

    @Test
    @Order(25)
    fun `channel moves in guild`(): Unit = runBlocking {
        val category = guild.createCategory("move category")
        val textChannel = guild.createTextChannel("move me to a category")

        guild.swapChannelPositions {
            move(textChannel.id) { parentId = category.id }
        }

        val currentTextChannel = guild.getChannelOf<TextChannel>(textChannel.id)
        assertEquals(category.id, currentTextChannel.categoryId)
    }

    @Test
    @Order(26)
    fun `global application commands`(): Unit = runBlocking {
        val command = kord.createGlobalChatInputCommand("test", "automated test") {
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

        assertEquals("test", command.name)
        assertEquals("automated test", command.description)

        assertEquals(1, command.groups.size)
        val group = command.groups.values.first()
        assertEquals("test-group", group.name)
        assertEquals("automated test", group.description)

        assertEquals(1, group.subcommands.size)
        val commandInGroup = group.subcommands.values.first()
        assertEquals("test-sub-command", commandInGroup.name)
        assertEquals("automated test", commandInGroup.description)

        assertEquals(1, commandInGroup.parameters.size)
        val commandInGroupParameter = commandInGroup.parameters.values.first()
        assertEquals("integer", commandInGroupParameter.name)
        assertEquals("test choice", commandInGroupParameter.description)
        assertEquals(2, commandInGroupParameter.choices.size)
        assertEquals("1", commandInGroupParameter.choices["one"])
        assertEquals("2", commandInGroupParameter.choices["two"])

        assertEquals(1, command.subCommands.size)
        val subCommand = command.subCommands.values.first()
        assertEquals("test-sub-command", subCommand.name)
        assertEquals("automated test", subCommand.description)

        assertEquals(1, subCommand.parameters.size)
        val subCommandParameter = subCommand.parameters.values.first()
        assertEquals("integer", subCommandParameter.name)
        assertEquals("test choice", subCommandParameter.description)

        val updated = command.edit {
            description = "other description"
        }

        assertEquals("other description", updated.description)

        updated.delete()
    }

    @Order(28)
    @Test
    @Disabled("Requires Community Guild")
    fun `create thread`() = runBlocking {

        val publicThread = channel.startPublicThread("TEST THREAD")
        val active = channel.activeThreads
        assertEquals(false, publicThread.isPrivate)

        assertTrue(active.toList().isNotEmpty())
        publicThread.join()

        val privateThread = channel.startPrivateThread("TEST PRIVATE THREAD")

        privateThread.join()

        assertTrue(privateThread.isPrivate)

        publicThread.edit {
            archived = true
        }

        privateThread.edit {
            archived = true
        }

        val joined = channel.getJoinedPrivateArchivedThreads()
        assertTrue(joined.toList().isNotEmpty())


        val publicArchive = channel.getPublicArchivedThreads()
        assertTrue(publicArchive.toList().isNotEmpty())
        val privateArchive = channel.getPrivateArchivedThreads()
        assertTrue(privateArchive.toList().isNotEmpty())


    }

    @Test
    @Order(Int.MAX_VALUE - 2)
    fun `delete channel`(): Unit = runBlocking {
        channel.delete()
    }

    @Test
    @Order(Int.MAX_VALUE - 1)
    fun `audit logs`(): Unit = runBlocking {
        guild.getAuditLogEntries().toList()
    }

    @Test
    @Order(Int.MAX_VALUE)
    fun `delete guild`(): Unit = runBlocking {
        guild.delete()
    }

    @Test
    fun `create role with image icon`(): Unit = runBlocking {
        if (!boostEnabled)
            return@runBlocking
        val guild = kord.getGuildOrThrow(publicGuildId)
        guild.createRole {
            name = "Test Image Icon"
            hoist = true
            icon = imageBinary("images/kord_icon.png")
        }
    }

    @Test
    fun `create role with unicode icon`(): Unit = runBlocking {
        if (!boostEnabled)
            return@runBlocking
        val guild = kord.getGuildOrThrow(publicGuildId)
        guild.createRole {
            name = "Test Unicode Icon"
            hoist = true
            unicodeEmoji = "\uD83D\uDE04"
        }
    }
}

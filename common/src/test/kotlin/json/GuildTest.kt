package json

import dev.kord.common.entity.*
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds


private fun file(name: String): String {
    val loader = ChannelTest::class.java.classLoader
    return loader.getResource("json/guild/$name.json")!!.readText()
}

class GuildTest {

    @Test
    fun `Guild serialization`() {
        val guild = Json.decodeFromString(DiscordGuild.serializer(), file("guild"))

        with(guild) {
            id shouldBe "197038439483310086"
            name shouldBe "Discord Testers"
            icon shouldBe "f64c482b807da4f539cff778d174971c"
            description shouldBe "The official place to report Discord Bugs!"
            splash shouldBe null
            discoverySplash shouldBe null
            features shouldBe listOf(
                GuildFeature.AnimatedIcon,
                GuildFeature.Verified,
                GuildFeature.News,
                GuildFeature.VanityUrl,
                GuildFeature.Discoverable,
                GuildFeature.InviteSplash,
                GuildFeature.Banner,
                GuildFeature.Community,
            )
            emojis shouldBe emptyList()
            banner shouldBe "9b6439a7de04f1d26af92f84ac9e1e4a"
            ownerId shouldBe "73193882359173120"
            applicationId shouldBe null
            @Suppress("DEPRECATION")
            region shouldBe "us-west"
            afkChannelId shouldBe null
            afkTimeout shouldBe 300.seconds
            systemChannelId shouldBe null
            widgetEnabled shouldBe true
            widgetChannelId shouldBe null
            verificationLevel shouldBe VerificationLevel.High
            roles shouldBe emptyList()
            defaultMessageNotifications shouldBe DefaultMessageNotificationLevel.OnlyMentions
            mfaLevel shouldBe MFALevel.Elevated
            explicitContentFilter shouldBe ExplicitContentFilter.AllMembers
            maxPresences shouldBe 40000
            maxMembers shouldBe 250000
            vanityUrlCode shouldBe "discord-testers"
            premiumTier shouldBe PremiumTier.Three
            premiumSubscriptionCount shouldBe 33
            systemChannelFlags shouldBe SystemChannelFlags(0)
            preferredLocale shouldBe "en-US"
            rulesChannelId shouldBe "441688182833020939"
            publicUpdatesChannelId shouldBe "281283303326089216"
            nsfwLevel shouldBe NsfwLevel.Default
        }
    }

    @Test
    fun `UnavailableGuild serialization`() {
        val guild = Json.decodeFromString(DiscordUnavailableGuild.serializer(), file("unavailableguild"))

        with(guild) {
            id shouldBe "41771983423143937"
            unavailable shouldBe true
        }

    }

    @Test
    fun `GuildMember serialization`() {
        val member = Json.decodeFromString(DiscordGuildMember.serializer(), file("guildmember"))

        with(member) {
            nick shouldBe "NOT API SUPPORT"
            roles shouldBe emptyList()
            joinedAt shouldBe Instant.parse("2015-04-26T06:26:56.936000+00:00")
            deaf shouldBe false
            mute shouldBe false
        }
    }

    @Test
    fun `PartialGuild serialization`() {
        val guild = Json.decodeFromString(DiscordPartialGuild.serializer(), file("partialguild"))

        with(guild) {
            id shouldBe "80351110224678912"
            name shouldBe "1337 Krew"
            icon shouldBe "8342729096ea3675442027381ff50dfe"
            owner shouldBe true
            permissions shouldBe Permissions("36953089")
            features shouldBe listOf(GuildFeature.Community, GuildFeature.News)
        }
    }
}

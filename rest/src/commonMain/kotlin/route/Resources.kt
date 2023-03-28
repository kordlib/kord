package dev.kord.rest.route

import dev.kord.common.entity.Snowflake
import io.ktor.resources.*
import kotlinx.serialization.Contextual

@Resource("/users")
public class Users {
    @Resource("@me")
    public class Me(public val parent: Users = Users()) {
        @Resource("guilds")
        public class Guilds(public val parent: Me = Me()) {
            @Resource("{guildId}")
            public class ById(public val parent: Guilds = Guilds(), public val guildId: Snowflake)
        }
        @Resource("channels")
        public class Channels(public val parent: Me = Me())
        @Resource("connections")
        public class Connections(public val parent: Me = Me())


    }
    @Resource("{userId}")
    public class ById(public val userId: Snowflake, public val parent: Users = Users())
}

@Resource("/guilds")
public class Guilds {
    @Resource("{guildId}")
    public class ById(public val guildId: Snowflake, public val parent: Guilds = Guilds()) {
        @Resource("audit-log")
        public class AuditLog(public val parent: Guilds = Guilds())
        @Resource("mfa")
        public class MFA(public val parent: Guilds = Guilds())
        @Resource("prune")
        public class Prune(public val parent: Guilds = Guilds())
        @Resource("regions")
        public class Regions(public val parent: Guilds = Guilds())
        @Resource("widget")
        public class Widget(public val parent: Guilds = Guilds())
        @Resource("vanity-url")
        public class VanityUrl(public val parent: Guilds = Guilds())
        @Resource("welcome-screen")
        public class WelcomeScreen(public val parent: Guilds = Guilds())
        @Resource("preview")
        public class Preview(public val parent: Guilds = Guilds())
        @Resource("members")
        public class Members(public val parent: Guilds = Guilds()) {
            @Resource("{userId}")
            public class ById(public val parent: Members = Members(), public val userId: Snowflake) {
                @Resource("roles")
                public class Roles(public val parent: ById) {
                    @Resource("{roleId}")
                    public class ById(public val parent: Roles, public val roleId: Snowflake)
                }
            }
            @Resource("search")
            public class Search
            @Resource("me")
            public class Me {
                @Resource("nick")
                public class Nick
            }
        }
        @Resource("scheduled-events")
        public class ScheduledEvents(public val parent: Guilds = Guilds())  {
            @Resource("{scheduledEventId}")
            public class ById(public val scheduledEventId: Snowflake) {
                @Resource("users")
                public class Users(public val parent: ById)
            }
        }
        @Resource("stickers")
        public class Stickers(public val parent: Guilds = Guilds()) {
            @Resource("{stickerId}")
            public class ById(public val stickerId: Snowflake, public val parent: Stickers = Stickers())
        }
        @Resource("bans")
        public class Bans(public val parent: Guilds = Guilds()) {
            @Resource("{userId}")
            public class ById(public val userId: Snowflake, public val parent: Bans = Bans())
        }
        @Resource("channels")
        public class Channels(public val parent: Guilds = Guilds())
        @Resource("threads")
        public class Threads(public val parent: Guilds = Guilds()) {
            @Resource("active")
            public class Active(public val parent: Threads = Threads())
        }
        @Resource("webhooks")
        public class Webhooks(public val parent: Guilds = Guilds())
        @Resource("templates")
        public class Templates(public val parent: Guilds = Guilds()) {
            @Resource("templateCode")
            public class ById(public val templateCode: String, public val parent: Templates = Templates())
        }
        @Resource("invites")
        public class Invites(public val parent: Guilds = Guilds()) {
            @Resource("{inviteId}")
            public class ById(public val inviteId: Snowflake, public val parent: Invites = Invites())
        }
        @Resource("integrations")
        public class Integrations(public val parent: Guilds = Guilds()) {
            @Resource("{integrationId}")
            public class ById(public val integrationId: Snowflake, public val parent: Integrations = Integrations()) {
                @Resource("sync")
                public class Sync(public val parent: ById)
            }
        }
        @Resource("voice-states")
        public class VoiceStates(public val parent: Guilds = Guilds()) {
            @Resource("{voiceStateId}")
            public class ById(public val id: Snowflake, public val parent: VoiceStates = VoiceStates())
        }
        @Resource("emojis")
        public class Emojis(public val parent: Guilds = Guilds()) {
            public class ById( public val emojiId: Snowflake, public val parent: Emojis = Emojis())
        }

        @Resource("auto-moderation")
        public class AutoModeration(public val parent: Guilds = Guilds()) {
            @Resource("rules")
            public class Rules(public val parent: AutoModeration = AutoModeration()) {
                public class ById(public val autoModerationRuleId: Snowflake, public val parent: Rules = Rules())
            }
        }
    }
}
@Resource("/channels")
public class Channels {
    @Resource("{channelId}")
    public class ById(public val channelId: Snowflake, public val parent: Channels = Channels()) {
        @Resource("typing")
        public class Typing(public val parent: ById)
        @Resource("recipients")
        public class Recipients(public val parent: ById) {
            @Resource("{userId}")
            public class ById(public val parent: Recipients, public val userId: Snowflake)
        }

        @Resource("threads")
        public class Threads(public val parent: ById) {
            @Resource("private")
            public class Private(public val parent: Threads)
            @Resource("archived")
            public class Archived(public val parent: Threads) {
                @Resource("private")
                public class Private(public val parent: Archived)
                @Resource("public")
                public class Public(public val parent: Archived)
            }
        }

        @Resource("thread-members")
        public class ThreadMembers(public val parent: ById) {
            @Resource("@me")
            public class Me(public val parent: ThreadMembers)

            @Resource("{userId}")
            public class ById(public val userId: Snowflake, public val parent: ThreadMembers)

        }
        @Resource("invites")
        public class Invites(public val parent: ById)

        @Resource("pins")
        public class Pins(public val parent: ById) {
            @Resource("{messageId}")
            public class ById(public val parent: Pins, public val messageId: Snowflake)
        }
        @Resource("messages")
        public class Messages(public val parent: ById) {
            @Resource("bulk-delete")
            public class BulkDelete(public val parent: Messages)

            @Resource("{messageId}")
            public class ById(public val parent: Messages, public val messageId: Snowflake) {
                @Resource("threads")
                public class Threads(public val parent: ById)
                @Resource("crosspost")
                public class CrossPost(public val parent: ById)
                @Resource("reactions")
                public class Reactions(public val parent: Messages.ById) {
                    public class ById(public val emojiId: Snowflake, public val parent: Reactions) {
                        @Resource("@me")
                        public class Me(@Contextual public val parent: ById)
                        @Resource("{userId}")
                        public class ReactorById(public val userId: Snowflake,@Contextual public val parent: ById)

                    }
                }
            }
        }
        @Resource("permissions")
        public class Permissions(public val parent: Messages) {
            public class ById(public val permissions: Permissions, public val overrideId: Snowflake)
        }

        @Resource("webhooks")
        public class Webhooks(public val parent: ById)

    }
}
@Resource("/invites")
public class Invites {
    @Resource("{inviteCode}")
    public class ById(public val inviteCode: String, public val parent: Invites = Invites())
}

@Resource("/stickers")
public class Stickers {
    public class ById(public val stickerId: Snowflake, public val parent: Stickers = Stickers())
}

@Resource("/webhooks")
public class Webhooks {
    @Resource("{webhookId}")
    public class ById(public val webhookId: Snowflake, public val parent: Webhooks = Webhooks()){
        @Resource("{token}")
        public class WithToken(public val parent: ById, public val token: String) {
            @Resource("github")
            public class Github(public val parent: WithToken)
            @Resource("slack")
            public class Slack(public val parent: WithToken)

            @Resource("messages")
            public class Messages(public val parent: WithToken) {
                @Resource("{messageId}")
                public class ById(public val parent: Messages, public val messageId: Snowflake)
                @Resource("@original")
                public class Original(public val parent: Messages)
            }
        }
    }
}

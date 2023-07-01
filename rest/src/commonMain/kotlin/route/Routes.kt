package dev.kord.rest.route

import dev.kord.common.entity.Snowflake
import io.ktor.resources.*

public class Routes {
    @Resource("/gateway")
    public class Gateway {
        @Resource("bot")
        public class Bot
    }
    @Resource("/users")
    public class Users {
        @Resource("@me")
        public class Me(public val parent: Users) {
            @Resource("guilds")
            public class Guilds(public val parent: Me) {
                @Resource("{guildId}")
                public class ById(public val guildId: Snowflake, public val parent: Guilds)
            }

            @Resource("channels")
            public class Channels(public val parent: Me)

            @Resource("connections")
            public class Connections(public val parent: Me)


        }

        @Resource("{userId}")
        public class ById(public val userId: Snowflake, public val parent: Users)
    }

    @Resource("/guilds")
    public class Guilds {

        @Resource("{guildId}")
        public class ById(public val guildId: Snowflake, public val parent: Guilds) {
            @Resource("audit-log")
            public class AuditLog(public val parent: Guilds.ById)

            @Resource("mfa")
            public class MFA(public val parent: Guilds.ById)

            @Resource("prune")
            public class Prune(public val parent: Guilds.ById)

            @Resource("regions")
            public class Regions(public val parent: Guilds.ById)

            @Resource("widget")
            public class Widget(public val parent: Guilds.ById)

            @Resource("vanity-url")
            public class VanityUrl(public val parent: Guilds.ById)

            @Resource("welcome-screen")
            public class WelcomeScreen(public val parent: Guilds.ById)

            @Resource("preview")
            public class Preview(public val parent: Guilds.ById)

            @Resource("onboarding")
            public class OnBoarding(public val parent: Guilds.ById)

            @Resource("roles")
            public class Roles(public val parent: Guilds.ById) {
                @Resource("{roleId}")
                public class ById(public val roleId: Snowflake, public val parent: Roles)
            }

            @Resource("members")
            public class Members(public val parent: Guilds.ById) {
                @Resource("{userId}")
                public class ById(public val userId: Snowflake, public val parent: Members) {
                    @Resource("roles")
                    public class Roles(public val parent: Members.ById) {
                        @Resource("{roleId}")
                        public class ById(public val roleId: Snowflake, public val parent: Roles)
                    }
                }

                @Resource("search")
                public class Search(public val parent: Members)

                @Resource("me")
                public class Me(public val parent: Members) {
                    @Resource("nick")
                    public class Nick(public val parent: Me)
                }
            }

            @Resource("scheduled-events")
            public class ScheduledEvents(public val parent: Guilds.ById) {
                @Resource("{scheduledEventId}")
                public class ById(public val scheduledEventId: Snowflake, public val parent: ScheduledEvents) {
                    @Resource("users")
                    public class Users(public val parent: ScheduledEvents.ById)
                }
            }

            @Resource("stickers")
            public class Stickers(public val parent: Guilds.ById) {
                @Resource("{stickerId}")
                public class ById(public val stickerId: Snowflake, public val parent: Stickers)
            }

            @Resource("bans")
            public class Bans(public val parent: Guilds.ById) {
                @Resource("{userId}")
                public class ById(public val userId: Snowflake, public val parent: Bans)
            }

            @Resource("channels")
            public class Channels(public val parent: Guilds.ById)

            @Resource("threads")
            public class Threads(public val parent: Guilds.ById) {
                @Resource("active")
                public class Active(public val parent: Threads)
            }

            @Resource("webhooks")
            public class Webhooks(public val parent: Guilds.ById)

            @Resource("templates")
            public class Templates(public val parent: Guilds.ById) {
                @Resource("{templateCode}")
                public class ById(public val templateCode: String, public val parent: Templates)
            }

            @Resource("invites")
            public class Invites(public val parent: Guilds.ById) {
                @Resource("{inviteId}")
                public class ById(public val inviteId: Snowflake, public val parent: Invites)
            }

            @Resource("integrations")
            public class Integrations(public val parent: Guilds.ById) {
                @Resource("{integrationId}")
                public class ById(public val integrationId: Snowflake, public val parent: Integrations) {
                    @Resource("sync")
                    public class Sync(public val parent: Integrations.ById)
                }
            }

            @Resource("voice-states")
            public class VoiceStates(public val parent: Guilds.ById) {
                @Resource("{voiceStateId}")
                public class ById(public val id: Snowflake, public val parent: VoiceStates)
                @Resource("@me")
                public class Me(public val parent: VoiceStates)
            }

            @Resource("emojis")
            public class Emojis(public val parent: Guilds.ById) {
                @Resource("{emojiId}")
                public class ById(public val emojiId: Snowflake, public val parent: Emojis)
            }

            @Resource("auto-moderation")
            public class AutoModeration(public val parent: Guilds.ById) {
                @Resource("rules")
                public class Rules(public val parent: AutoModeration) {
                    @Resource("{autoModerationRuleId}")
                    public class ById(public val autoModerationRuleId: Snowflake, public val parent: Rules)
                }
            }
        }

        @Resource("templates")
        public class Templates(public val parent: Guilds) {
            @Resource("{templateCode}")
            public class ById(public val templateCode: String, public val parent: Templates)
        }

    }

    @Resource("/channels")
    public class Channels {
        @Resource("{channelId}")
        public class ById(public val channelId: Snowflake, public val parent: Channels) {
            @Resource("typing")
            public class Typing(public val parent: Channels.ById)
            @Resource("followers")
            public class Followers(public val parent: Channels.ById)
            @Resource("recipients")
            public class Recipients(public val parent: Channels.ById) {
                @Resource("{userId}")
                public class ById(public val userId: Snowflake, public val parent: Recipients)
            }

            @Resource("threads")
            public class Threads(public val parent: Channels.ById) {
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
            public class ThreadMembers(public val parent: Channels.ById) {
                @Resource("@me")
                public class Me(public val parent: ThreadMembers)

                @Resource("{userId}")
                public class ById(public val userId: Snowflake, public val parent: ThreadMembers)

            }
            @Resource("users")
            public class Users(public val parent: Channels.ById) {
                @Resource("@me")
                public class Me(public val parent: Users) {
                    @Resource("threads")
                    public class Threads(public val parent: Me) {
                        @Resource("archived")
                        public class Archived(public val parent: Threads) {
                            @Resource("private")
                            public class Private(public val parent: Archived)
                        }
                    }
                }
            }
            @Resource("invites")
            public class Invites(public val parent: Channels.ById)

            @Resource("pins")
            public class Pins(public val parent: Channels.ById) {
                @Resource("{messageId}")
                public class ById(public val messageId: Snowflake, public val parent: Pins)
            }

            @Resource("messages")
            public class Messages(public val parent: Channels.ById) {
                @Resource("bulk-delete")
                public class BulkDelete(public val parent: Messages)

                @Resource("{messageId}")
                public class ById(public val messageId: Snowflake, public val parent: Messages) {
                    @Resource("threads")
                    public class Threads(public val parent: Messages.ById)

                    @Resource("crosspost")
                    public class CrossPost(public val parent: Messages.ById)

                    @Resource("reactions")
                    public class Reactions(public val parent: Messages.ById) {
                        @Resource("{emojiId}")
                        public class ById(public val emojiId: String, public val parent: Reactions) {
                            @Resource("@me")
                            public class Me(public val parent: Reactions.ById)

                            @Resource("{userId}")
                            public class ReactorById(public val userId: Snowflake, public val parent: Reactions.ById)

                        }
                    }
                }
            }

            @Resource("permissions")
            public class Permissions(public val parent: Messages) {
                @Resource("{overwriteId}")
                public class ById(public val overwriteId: Snowflake, public val parent: Permissions)
            }

            @Resource("webhooks")
            public class Webhooks(public val parent: Channels.ById)

        }
    }

    @Resource("/invites")
    public class Invites {
        @Resource("{inviteCode}")
        public class ById(public val inviteCode: String, public val parent: Invites)
    }

    @Resource("/stickers")
    public class Stickers {
        @Resource("{stickerId}")
        public class ById(public val stickerId: Snowflake, public val parent: Stickers)
    }

    @Resource("/webhooks")
    public class Webhooks {
        @Resource("{webhookId}")
        public class ById(public val webhookId: Snowflake, public val parent: Webhooks) {
            @Resource("{token}")
            public class WithToken(public val token: String, public val parent: Channels.ById) {
                @Resource("github")
                public class Github(public val parent: WithToken)

                @Resource("slack")
                public class Slack(public val parent: WithToken)

                @Resource("messages")
                public class Messages(public val parent: WithToken) {
                    @Resource("{messageId}")
                    public class ById(public val messageId: Snowflake, public val parent: Messages)

                    @Resource("@original")
                    public class Original(public val parent: Messages)
                }
            }
        }
    }

    @Resource("/sticker-packs")
    public class NitroStickerPacks

    @Resource("/voice")
    public class Voice {
        @Resource("regions")
        public class Regions(public val parent: Voice)
    }

    @Resource("/applications")
    public class Applications {

        @Resource("{applicationId}")
        public class ById(public val applicationId: Snowflake, public val parent: Applications) {

            @Resource("commands")
            public class Commands(public val parent: Applications.ById) {
                @Resource("{commandId}")
                public class ById(public val commandId: Snowflake, public val parent: Commands)
            }

            @Resource("guilds")
            public class Guilds(public val parent: Applications.ById) {

                @Resource("{guildId}")
                public class ById(public val guildId: Snowflake, public val parent: Guilds) {

                    @Resource("permissions")
                    public class Permissions(public val parent: Guilds.ById)

                    @Resource("commands")
                    public class Commands(public val parent: Guilds.ById) {

                        @Resource("{commandId}")
                        public class ById(public val commandId: Snowflake, public val parent: Commands) {

                            @Resource("permissions")
                            public class Permissions(public val parent: Commands.ById)
                        }
                    }

                }
            }
        }

    }

    @Resource("/interactions")
    public class Interactions {
        @Resource("{interactionId}")
        public class ById(public val interactionId: Snowflake, public val parent: Interactions) {
            @Resource("{interactionToken}")
            public class Token(public val interactionToken: String, public val parent: Interactions.ById) {
                @Resource("callback")
                public class Callback(public val parent: Token)
            }
        }
    }

    @Resource("/oauth2")
    public class OAuth2 {
        @Resource("applications")
        public class Applications {
            @Resource("@me")
            public class Me
        }
    }

}
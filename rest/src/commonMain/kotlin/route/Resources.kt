            package dev.kord.rest.route

            import dev.kord.common.entity.Snowflake
            import io.ktor.resources.*

            @Resource("/users")
            public class Users {
                    @Resource("@me")
                    public class Me {
                        @Resource("guilds")
                        public class Guilds {
                            @Resource("{guildId}")
                            public class ById(public val guildId: Snowflake)
                        }
                        @Resource("channels")
                        public class Channels
                        @Resource("connections")
                        public class Connections


                    }
                    @Resource("/{userId}")
                    public class Get
                }

            @Resource("/guilds")
            public class Guilds {
                @Resource("{guildId}")
                public class ById(public val guildId: Snowflake) {
                    @Resource("audit-log")
                    public class AuditLog
                    @Resource("mfa")
                    public class MFA
                    @Resource("prune")
                    public class Prune
                    @Resource("regions")
                    public class Regions
                    @Resource("widget")
                    public class Widget
                    @Resource("vanity-url")
                    public class VanityUrl
                    @Resource("welcome-screen")
                    public class WelcomeScreen
                    @Resource("preview")
                    public class Preview
                    @Resource("members")
                    public class Members {
                        @Resource("{userId}")
                        public class ById(public val userId: Snowflake) {
                            @Resource("roles")
                            public class Roles {
                                @Resource("{roleId}")
                                public class ById(public val roleId: Snowflake)
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
                    public class ScheduledEvents  {
                        @Resource("{scheduledEventId}")
                        public class ById(public val scheduledEventId: Snowflake) {
                            @Resource("users")
                            public class Users
                        }
                    }
                    @Resource("stickers")
                    public class Stickers {
                        @Resource("{stickerId}")
                        public class ById(public val stickerId: Snowflake)
                    }
                    @Resource("bans")
                    public class Bans {
                        @Resource("{userId}")
                        public class ById(public val userId: Snowflake)
                    }
                    @Resource("channels")
                    public class Channels
                    @Resource("threads")
                    public class Threads {
                        @Resource("active")
                        public class Active
                    }
                    @Resource("webhooks")
                    public class Webhooks
                    @Resource("templates")
                    public class Templates {
                        @Resource("templateCode")
                        public class ById(public val templateCode: String)
                    }
                    @Resource("invites")
                    public class Invites {
                        @Resource("{inviteId}")
                        public class ById(public val inviteId: Snowflake)
                    }
                    @Resource("integrations")
                    public class Integrations {
                        @Resource("{integrationId}")
                        public class ById(public val integrationId: Snowflake) {
                            @Resource("sync")
                            public class Sync
                        }
                    }
                    @Resource("voice-states")
                    public class VoiceStates {
                        @Resource("{voiceStateId}")
                        public class ById(public val id: Snowflake)
                    }
                    @Resource("emojis")
                    public class Emojis {
                        public class ById(public val emojiId: Snowflake)
                    }

                    @Resource("auto-moderation")
                    public class AutoModeration {
                        @Resource("rules")
                        public class Rules {
                            public class ById(public val autoModerationRuleId: Snowflake)
                        }
                    }
                }
            }
            @Resource("/channels")
            public class Channels {
                @Resource("{channelId}")
                public class ById(public val channelId: Snowflake) {
                    @Resource("typing")
                    public class Typing
                    @Resource("recipients")
                    public class Recipients {
                        @Resource("{userId}")
                        public class ById(public val userId: Snowflake)
                    }

                    @Resource("threads")
                    public class Threads {
                        @Resource("private")
                        public class Private
                        @Resource("archived")
                        public class Archived {
                            @Resource("private")
                            public class Private
                            @Resource("public")
                            public class Public
                        }
                    }

                    @Resource("thread-members")
                    public class ThreadMembers {
                        @Resource("@me")
                        public class Me

                        @Resource("{userId}")
                        public class ById(public val userId: Snowflake)

                    }
                    @Resource("invites")
                    public class Invites

                    @Resource("pins")
                    public class Pins {
                        @Resource("{messageId}")
                        public class ById(public val messageId: Snowflake)
                    }
                    @Resource("messages")
                    public class Messages {
                        @Resource("bulk-delete")
                        public class BulkDelete

                        @Resource("{messageId}")
                        public class ById(public val messageId: Snowflake) {
                            @Resource("threads")
                            public class Threads
                            @Resource("crosspost")
                            public class CrossPost
                            @Resource("reactions")
                            public class Reactions {
                                public class ById(public val emojiId: Snowflake) {
                                    @Resource("@me")
                                    public class Me
                                    @Resource("{userId}")
                                    public class ReactorById(public val userId: Snowflake)

                                }
                            }
                        }
                    }
                    @Resource("permissions")
                    public class Permissions {
                        public class ById(public val overrideId: Snowflake)
                    }

                    @Resource("webhooks")
                    public class Webhooks

                }
            }
            @Resource("/invites")
            public class Invites {
                @Resource("{inviteCode}")
                public class ById(public val inviteCode: String)
            }

            @Resource("/stickers")
            public class Stickers {
                public class ById(public val stickerId: Snowflake)
            }

            @Resource("/webhooks")
            public class Webhooks {
                @Resource("{webhookId}")
                public class ById(public val webhookId: Snowflake){
                    @Resource("{token}")
                    public class WithToken(public val token: String) {
                        @Resource("github")
                        public class Github
                        @Resource("slack")
                        public class Slack

                        @Resource("messages")
                        public class Messages {
                            @Resource("{messageId}")
                            public class ById(public val messageId: Snowflake)
                            @Resource("@original")
                            public class Original
                        }
                    }
                }
            }

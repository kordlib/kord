package dev.kord.rest.route

import io.ktor.resources.*

    @Resource("/users")
    public class Users {
        @Resource("@me")
        public class Me {
            @Resource("guilds")
            public class Guilds {
                @Resource("{guildId}")
                public class ById
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
    public class ById {
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
            public class ById {
                @Resource("roles")
                public class Roles {
                    @Resource("roleId")
                    public class ById
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
            public class ById {
                @Resource("users")
                public class Users
            }
        }
        @Resource("stickers")
        public class Stickers {
            @Resource("{stickerId}")
            public class ById
        }
        @Resource("bans")
        public class Bans {
            @Resource("{userId}")
            public class ById
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
            public class ById
        }
        @Resource("invites")
        public class Invites {
            @Resource("{inviteId}")
            public class ById
        }
        @Resource("integrations")
        public class Integrations {
            @Resource("integrationId")
            public class ById {
                @Resource("sync")
                public class Sync
            }
        }
        @Resource("voice-states")
        public class VoiceStates {
            @Resource("voiceStateId")
            public class ById
        }
    }
}
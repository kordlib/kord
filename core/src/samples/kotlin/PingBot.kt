import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.builder.components.emoji
import dev.kord.core.cache.data.ApplicationComponentInteractionData
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.interaction.ComponentInteraction
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on

@KordPreview
suspend fun main(args: Array<String>) {
    val kord = Kord(args.firstOrNull() ?: error("token required"))

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot == true) return@on
        if (message.content == "!ping") message.channel.createMessage("pong")
        if (message.content == "!buttons") {
            message.channel.createMessage {
                content = "Ich mag ~~Züge~~ Buttons"

                components {
                    actionRow {
                        interactionButton(ButtonStyle.Primary, "eliteblog") {
                            emoji(ReactionEmoji.Unicode("\uD83D\uDE38"))
                            disabled = true
                        }
//                        interactionButton(
//                            "eliteblog2"
//                        ) {
//                            emoji(ReactionEmoji.Unicode("\uD83D\uDE38"))
//                        }
                    }
//                    actionRow {
//                        linkButton("DuckDuckGo but gud", "https://google.com")
//                    }
                }
            }

        }
    }

    kord.on<InteractionCreateEvent> {
        val interaction1 = interaction
        if (interaction1 is ComponentInteraction) {
            if ((interaction1.data.data as ApplicationComponentInteractionData).customId.value == "eliteblog2") {
                interaction.respondPublic {
                    content = "Discord sucks!"
                }
            }
        }
    }

    kord.login { playing("!ping to pong") }
}

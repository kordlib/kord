@file:Generate(
    INT_KORD_ENUM, name = "ComponentType",
    docUrl = "https://discord.com/developers/docs/interactions/message-components#component-object-component-types",
    entries = [
        Entry("ActionRow", intValue = 1, kDoc = "A container for other components."),
        Entry("Button", intValue = 2, kDoc = "A button object."),
        Entry("StringSelect", intValue = 3, kDoc = "A select menu for picking from defined text options."),
        Entry("TextInput", intValue = 4, kDoc = "A text input object."),
        Entry("UserSelect", intValue = 5, kDoc = "Select menu for users."),
        Entry("RoleSelect", intValue = 6, kDoc = "Select menu for roles."),
        Entry("MentionableSelect", intValue = 7, kDoc = "Select menu for mentionables (users and roles)."),
        Entry("ChannelSelect", intValue = 8, kDoc = "Select menu for channels."),
        // v2
        Entry("Section", intValue = 9, kDoc = "Container to display text alongside an accessory component"),
        Entry("TextDisplay", intValue = 10, kDoc = "Markdown text"),
        Entry("Thumbnail", intValue = 11, kDoc = "Small image that can be used as an accessory"),
        Entry("MediaGallery", intValue = 12, kDoc = "Display images and other media"),
        Entry("File", intValue = 13, kDoc = "Displays an attached file"),
        Entry("Separator", intValue = 14, kDoc = "Component to add vertical padding between other components"),
        Entry("Container", intValue = 17, kDoc = "Container that visually groups a set of components"),
    ],
)

@file:Generate(
    INT_KORD_ENUM, name = "ButtonStyle",
    kDoc = "Style of a [button][dev.kord.common.entity.ComponentType.Button].",
    docUrl = "https://discord.com/developers/docs/interactions/message-components#button-object-button-styles",
    entries = [
        Entry("Primary", intValue = 1, kDoc = "Blurple."),
        Entry("Secondary", intValue = 2, kDoc = "Grey."),
        Entry("Success", intValue = 3, kDoc = "Green."),
        Entry("Danger", intValue = 4, kDoc = "Red."),
        Entry("Link", intValue = 5, kDoc = "Grey, navigates to a URL."),
        Entry("Premium", intValue = 6, kDoc = "Blurple, prompts to purchase a premium offering."),
    ],
)

@file:Generate(
    INT_KORD_ENUM, name = "TextInputStyle",
    kDoc = "Style of a [text·input][dev.kord.common.entity.ComponentType.TextInput].",
    docUrl = "https://discord.com/developers/docs/interactions/message-components#text-input-object-text-input-styles",
    entries = [
        Entry("Short", intValue = 1, kDoc = "A single-line input."),
        Entry("Paragraph", intValue = 2, kDoc = "A multi-line input."),
    ],
)

@file:Generate(
    INT_KORD_ENUM, name = "SeparatorSpacingSize",
    docUrl = "https://discord.com/developers/docs/components/reference#separator-separator-structure",
    entries = [
        Entry("Small", intValue = 1),
        Entry("Large", intValue = 2),
    ]
)

package dev.kord.common.entity

import dev.kord.common.Color
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * Represent a [interactable component within a message sent in Discord](https://discord.com/developers/docs/interactions/message-components#what-are-components).
 *
 * @property type the [ComponentType] of the component
 * @property emoji an [DiscordPartialEmoji] that appears on the button (if the component is a button)
 * @property customId a developer-defined identifier for the button, max 100 characters
 * @property url a url for link-style buttons
 * @property disabled whether the button is disabled, default `false`
 * @property components a list of child components (for action rows)
 * @property options the select menu options
 * @property placeholder the placeholder text for the select menu
 * @property minValues the minimum amount of [options] allowed
 * @property maxValues the maximum amount of [options] allowed
 * @property minLength the minimum input length for a text input, min 0, max 4000.
 * @property maxLength the maximum input length for a text input, min 1, max 4000.
 * @property required whether this component is required to be filled, default false.
 * @property value a pre-filled value for this component, max 4000 characters.
 * @property channelTypes List of channel types to include in the channel select component ([ComponentType.ChannelSelect])
 */
@Serializable(with = DiscordComponent.Serializer::class)
public sealed class DiscordComponent {
    public abstract val type: ComponentType
    public abstract val label: Optional<String>
    public abstract val emoji: Optional<DiscordPartialEmoji>

    @SerialName("custom_id")
    public abstract val customId: Optional<String>
    public abstract val url: Optional<String>
    public abstract val disabled: OptionalBoolean
    public abstract val components: Optional<List<DiscordComponent>>
    public abstract val options: Optional<List<DiscordSelectOption>>
    public abstract val placeholder: Optional<String>

    @SerialName("default_values")
    public abstract val defaultValues: Optional<List<DiscordSelectDefaultValue>>

    @SerialName("min_values")
    public abstract val minValues: OptionalInt

    @SerialName("max_values")
    public abstract val maxValues: OptionalInt

    @SerialName("min_length")
    public abstract val minLength: OptionalInt

    @SerialName("max_length")
    public abstract val maxLength: OptionalInt
    public abstract val required: OptionalBoolean
    public abstract val value: Optional<String>

    @SerialName("channel_types")
    public abstract val channelTypes: Optional<List<ChannelType>>

    internal object Serializer : JsonContentPolymorphicSerializer<DiscordComponent>(DiscordComponent::class) {
        override fun selectDeserializer(element: JsonElement): KSerializer<out DiscordComponent> {
            val componentType =
                element.jsonObject["type"]?.jsonPrimitive?.intOrNull ?: error("Missing component type ID!")

            return if (componentType == ComponentType.TextInput.value) {
                DiscordTextInputComponent.serializer()
            } else {
                DiscordChatComponent.serializer()
            }
        }
    }
}

@Serializable
public data class MediaGalleryItem(
    val media: UnfurledMediaItem,
    val description: Optional<String?> = Optional.Missing(),
    val spoiler: OptionalBoolean = OptionalBoolean.Missing,
)

@Serializable
public data class UnfurledMediaItem(
    val url: String,
    @SerialName("proxy_url")
    val proxyUrl: Optional<String> = Optional.Missing(),
    val height: OptionalInt? = OptionalInt.Missing,
    val width: OptionalInt? = OptionalInt.Missing,
    val contentType: Optional<String> = Optional.Missing()
)

@Serializable
public data class DiscordChatComponent(
    override val type: ComponentType,
    val style: Optional<ButtonStyle> = Optional.Missing(),
    override val label: Optional<String> = Optional.Missing(),
    override val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    @SerialName("custom_id")
    override val customId: Optional<String> = Optional.Missing(),
    override val url: Optional<String> = Optional.Missing(),
    override val disabled: OptionalBoolean = OptionalBoolean.Missing,
    override val components: Optional<List<DiscordComponent>> = Optional.Missing(),
    override val options: Optional<List<DiscordSelectOption>> = Optional.Missing(),
    override val placeholder: Optional<String> = Optional.Missing(),
    @SerialName("default_values")
    override val defaultValues: Optional<List<DiscordSelectDefaultValue>> = Optional.Missing(),
    @SerialName("min_values")
    override val minValues: OptionalInt = OptionalInt.Missing,
    @SerialName("max_values")
    override val maxValues: OptionalInt = OptionalInt.Missing,
    @SerialName("min_length")
    override val minLength: OptionalInt = OptionalInt.Missing,
    @SerialName("max_length")
    override val maxLength: OptionalInt = OptionalInt.Missing,
    override val required: OptionalBoolean = OptionalBoolean.Missing,
    override val value: Optional<String> = Optional.Missing(),
    @SerialName("channel_types")
    override val channelTypes: Optional<List<ChannelType>> = Optional.Missing(),
    @SerialName("sku_id")
    val skuId: OptionalSnowflake = OptionalSnowflake.Missing,
    val accessory: Optional<DiscordComponent> = Optional.Missing(),
    val content: Optional<String> = Optional.Missing(),
    val media: Optional<UnfurledMediaItem> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    val spoiler: OptionalBoolean = OptionalBoolean.Missing,
    val items: Optional<List<MediaGalleryItem>> = Optional.Missing(),
    val divider: OptionalBoolean = OptionalBoolean.Missing,
    val spacing: Optional<SeparatorSpacingSize> = Optional.Missing(),
    val file: Optional<UnfurledMediaItem> = Optional.Missing(),
    @SerialName("accent_color")
    val accentColor: Optional<Color?> = Optional.Missing(),
) : DiscordComponent()

@Serializable
public data class DiscordTextInputComponent(
    override val type: ComponentType,
    public val style: Optional<TextInputStyle> = Optional.Missing(),
    override val label: Optional<String> = Optional.Missing(),
    override val emoji: Optional<DiscordPartialEmoji> = Optional.Missing(),
    @SerialName("custom_id")
    override val customId: Optional<String> = Optional.Missing(),
    override val url: Optional<String> = Optional.Missing(),
    override val disabled: OptionalBoolean = OptionalBoolean.Missing,
    override val components: Optional<List<DiscordComponent>> = Optional.Missing(),
    override val options: Optional<List<DiscordSelectOption>> = Optional.Missing(),
    override val placeholder: Optional<String> = Optional.Missing(),
    @SerialName("default_values")
    override val defaultValues: Optional<List<DiscordSelectDefaultValue>> = Optional.Missing(),
    @SerialName("min_values")
    override val minValues: OptionalInt = OptionalInt.Missing,
    @SerialName("max_values")
    override val maxValues: OptionalInt = OptionalInt.Missing,
    @SerialName("min_length")
    override val minLength: OptionalInt = OptionalInt.Missing,
    @SerialName("max_length")
    override val maxLength: OptionalInt = OptionalInt.Missing,
    override val required: OptionalBoolean = OptionalBoolean.Missing,
    override val value: Optional<String> = Optional.Missing(),
    @SerialName("channel_types")
    override val channelTypes: Optional<List<ChannelType>> = Optional.Missing(),
) : DiscordComponent()

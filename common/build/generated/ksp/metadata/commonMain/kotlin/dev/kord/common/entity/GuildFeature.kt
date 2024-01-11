// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * See [GuildFeature]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-object-guild-features).
 */
@Serializable(with = GuildFeature.Serializer::class)
public sealed class GuildFeature(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is GuildFeature && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "GuildFeature.Unknown(value=$value)"
            else "GuildFeature.${this::class.simpleName}"

    /**
     * An unknown [GuildFeature].
     *
     * This is used as a fallback for [GuildFeature]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: String,
    ) : GuildFeature(value)

    /**
     * Guild has access to set an animated guild banner image.
     */
    public object AnimatedBanner : GuildFeature("ANIMATED_BANNER")

    /**
     * Guild has access to set an animated guild icon.
     */
    public object AnimatedIcon : GuildFeature("ANIMATED_ICON")

    /**
     * Guild is using the old permissions configuration behavior.
     */
    public object ApplicationCommandPermissionsV2 :
            GuildFeature("APPLICATION_COMMAND_PERMISSIONS_V2")

    /**
     * Guild has set up auto moderation rules.
     */
    public object AutoModeration : GuildFeature("AUTO_MODERATION")

    /**
     * Guild has access to set a guild banner image.
     */
    public object Banner : GuildFeature("BANNER")

    /**
     * Guild can enable welcome screen, Membership Screening, stage channels and discovery, and
     * receives community updates.
     */
    public object Community : GuildFeature("COMMUNITY")

    /**
     * Guild has enabled monetization.
     */
    public object CreatorMonetizableProvisional : GuildFeature("CREATOR_MONETIZABLE_PROVISIONAL")

    /**
     * Guild has enabled the role subscription promo page.
     */
    public object CreatorStorePage : GuildFeature("CREATOR_STORE_PAGE")

    /**
     * Guild has been set as a support server on the App Directory.
     */
    public object DeveloperSupportServer : GuildFeature("DEVELOPER_SUPPORT_SERVER")

    /**
     * Guild is able to be discovered in the directory.
     */
    public object Discoverable : GuildFeature("DISCOVERABLE")

    /**
     * Guild is able to be featured in the directory.
     */
    public object Featurable : GuildFeature("FEATURABLE")

    /**
     * Guild has paused invites, preventing new users from joining.
     */
    public object InvitesDisabled : GuildFeature("INVITES_DISABLED")

    /**
     * Guild has access to set an invite splash background.
     */
    public object InviteSplash : GuildFeature("INVITE_SPLASH")

    /**
     * Guild has enabled Membership Screening.
     */
    public object MemberVerificationGateEnabled : GuildFeature("MEMBER_VERIFICATION_GATE_ENABLED")

    /**
     * Guild has increased custom sticker slots.
     */
    public object MoreStickers : GuildFeature("MORE_STICKERS")

    /**
     * Guild has access to create announcement channels.
     */
    public object News : GuildFeature("NEWS")

    /**
     * Guild is partnered.
     */
    public object Partnered : GuildFeature("PARTNERED")

    /**
     * Guild can be previewed before joining via Membership Screening or the directory.
     */
    public object PreviewEnabled : GuildFeature("PREVIEW_ENABLED")

    /**
     * Guild has disabled alerts for join raids in the configured safety alerts channel.
     */
    public object RaidAlertsDisabled : GuildFeature("RAID_ALERTS_DISABLED")

    /**
     * Guild is able to set role icons.
     */
    public object RoleIcons : GuildFeature("ROLE_ICONS")

    /**
     * Guild has role subscriptions that can be purchased.
     */
    public object RoleSubscriptionsAvailableForPurchase :
            GuildFeature("ROLE_SUBSCRIPTIONS_AVAILABLE_FOR_PURCHASE")

    /**
     * Guild has enabled role subscriptions.
     */
    public object RoleSubscriptionsEnabled : GuildFeature("ROLE_SUBSCRIPTIONS_ENABLED")

    /**
     * Guild has enabled ticketed events.
     */
    public object TicketedEventsEnabled : GuildFeature("TICKETED_EVENTS_ENABLED")

    /**
     * Guild has access to set a vanity URL.
     */
    public object VanityUrl : GuildFeature("VANITY_URL")

    /**
     * Guild is verified.
     */
    public object Verified : GuildFeature("VERIFIED")

    /**
     * Guild has access to set 384kbps bitrate in voice (previously VIP voice servers).
     */
    public object VIPRegions : GuildFeature("VIP_REGIONS")

    /**
     * Guild has enabled the welcome screen.
     */
    public object WelcomeScreenEnabled : GuildFeature("WELCOME_SCREEN_ENABLED")

    internal object Serializer : KSerializer<GuildFeature> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.GuildFeature",
                PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: GuildFeature) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): GuildFeature = from(decoder.decodeString())
    }

    public companion object {
        /**
         * A [List] of all known [GuildFeature]s.
         */
        public val entries: List<GuildFeature> by lazy(mode = PUBLICATION) {
            listOf(
                AnimatedBanner,
                AnimatedIcon,
                ApplicationCommandPermissionsV2,
                AutoModeration,
                Banner,
                Community,
                CreatorMonetizableProvisional,
                CreatorStorePage,
                DeveloperSupportServer,
                Discoverable,
                Featurable,
                InvitesDisabled,
                InviteSplash,
                MemberVerificationGateEnabled,
                MoreStickers,
                News,
                Partnered,
                PreviewEnabled,
                RaidAlertsDisabled,
                RoleIcons,
                RoleSubscriptionsAvailableForPurchase,
                RoleSubscriptionsEnabled,
                TicketedEventsEnabled,
                VanityUrl,
                Verified,
                VIPRegions,
                WelcomeScreenEnabled,
            )
        }


        /**
         * Returns an instance of [GuildFeature] with [GuildFeature.value] equal to the specified
         * [value].
         */
        public fun from(`value`: String): GuildFeature = when (value) {
            "ANIMATED_BANNER" -> AnimatedBanner
            "ANIMATED_ICON" -> AnimatedIcon
            "APPLICATION_COMMAND_PERMISSIONS_V2" -> ApplicationCommandPermissionsV2
            "AUTO_MODERATION" -> AutoModeration
            "BANNER" -> Banner
            "COMMUNITY" -> Community
            "CREATOR_MONETIZABLE_PROVISIONAL" -> CreatorMonetizableProvisional
            "CREATOR_STORE_PAGE" -> CreatorStorePage
            "DEVELOPER_SUPPORT_SERVER" -> DeveloperSupportServer
            "DISCOVERABLE" -> Discoverable
            "FEATURABLE" -> Featurable
            "INVITES_DISABLED" -> InvitesDisabled
            "INVITE_SPLASH" -> InviteSplash
            "MEMBER_VERIFICATION_GATE_ENABLED" -> MemberVerificationGateEnabled
            "MORE_STICKERS" -> MoreStickers
            "NEWS" -> News
            "PARTNERED" -> Partnered
            "PREVIEW_ENABLED" -> PreviewEnabled
            "RAID_ALERTS_DISABLED" -> RaidAlertsDisabled
            "ROLE_ICONS" -> RoleIcons
            "ROLE_SUBSCRIPTIONS_AVAILABLE_FOR_PURCHASE" -> RoleSubscriptionsAvailableForPurchase
            "ROLE_SUBSCRIPTIONS_ENABLED" -> RoleSubscriptionsEnabled
            "TICKETED_EVENTS_ENABLED" -> TicketedEventsEnabled
            "VANITY_URL" -> VanityUrl
            "VERIFIED" -> Verified
            "VIP_REGIONS" -> VIPRegions
            "WELCOME_SCREEN_ENABLED" -> WelcomeScreenEnabled
            else -> Unknown(value)
        }
    }
}

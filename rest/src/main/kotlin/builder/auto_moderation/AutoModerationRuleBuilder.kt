package dev.kord.rest.builder.auto_moderation

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.AutoModerationRuleEventType
import dev.kord.common.entity.AutoModerationRuleKeywordPresetType
import dev.kord.common.entity.AutoModerationRuleTriggerType
import dev.kord.common.entity.AutoModerationRuleTriggerType.*
import dev.kord.common.entity.Snowflake
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.time.Duration

@KordDsl
public sealed interface AutoModerationRuleBuilder {
    public var reason: String?
    public val name: String?
    public fun assignName(name: String)
    public val eventType: AutoModerationRuleEventType?
    public fun assignEventType(eventType: AutoModerationRuleEventType)
    public val triggerType: AutoModerationRuleTriggerType
    public val actions: MutableList<AutoModerationActionBuilder>?
    public fun assignActions(actions: MutableList<AutoModerationActionBuilder>)
    public var enabled: Boolean?
    public var exemptRoles: MutableList<Snowflake>?
    public fun exemptRole(roleId: Snowflake) {
        exemptRoles?.add(roleId) ?: run { exemptRoles = mutableListOf(roleId) }
    }

    public var exemptChannels: MutableList<Snowflake>?
    public fun exemptChannel(channelId: Snowflake) {
        exemptChannels?.add(channelId) ?: run { exemptChannels = mutableListOf(channelId) }
    }
}

public inline fun AutoModerationRuleBuilder.blockMessage(
    builder: BlockMessageAutoModerationActionBuilder.() -> Unit = {},
) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val action = BlockMessageAutoModerationActionBuilder().apply(builder)
    actions?.add(action) ?: assignActions(mutableListOf(action))
}

public inline fun AutoModerationRuleBuilder.sendAlertMessage(
    channelId: Snowflake,
    builder: SendAlertMessageAutoModerationActionBuilder.() -> Unit = {},
) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val action = SendAlertMessageAutoModerationActionBuilder(channelId).apply(builder)
    actions?.add(action) ?: assignActions(mutableListOf(action))
}

@KordDsl
public sealed interface KeywordAutoModerationRuleBuilder : AutoModerationRuleBuilder {
    override val triggerType: Keyword get() = Keyword
    public val keywords: MutableList<String>?
    public fun assignKeywords(keywords: MutableList<String>)
    public fun keyword(keyword: String) {
        keywords?.add(keyword) ?: assignKeywords(mutableListOf(keyword))
    }
}

public inline fun KeywordAutoModerationRuleBuilder.timeout(
    duration: Duration,
    builder: TimeoutAutoModerationActionBuilder.() -> Unit = {},
) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val action = TimeoutAutoModerationActionBuilder(duration).apply(builder)
    actions?.add(action) ?: assignActions(mutableListOf(action))
}

@KordDsl
public sealed interface HarmfulLinkAutoModerationRuleBuilder : AutoModerationRuleBuilder {
    override val triggerType: HarmfulLink get() = HarmfulLink
}

@KordDsl
public sealed interface SpamAutoModerationRuleBuilder : AutoModerationRuleBuilder {
    override val triggerType: Spam get() = Spam
}

@KordDsl
public sealed interface KeywordPresetAutoModerationRuleBuilder : AutoModerationRuleBuilder {
    override val triggerType: KeywordPreset get() = KeywordPreset
    public val presets: MutableList<AutoModerationRuleKeywordPresetType>?
    public fun assignPresets(presets: MutableList<AutoModerationRuleKeywordPresetType>)
    public fun preset(preset: AutoModerationRuleKeywordPresetType) {
        presets?.add(preset) ?: assignPresets(mutableListOf(preset))
    }

    public var allowList: MutableList<String>?
    public fun allow(substring: String) {
        allowList?.add(substring) ?: run { allowList = mutableListOf(substring) }
    }
}

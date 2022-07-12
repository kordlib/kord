package dev.kord.rest.service

import dev.kord.common.entity.AutoModerationRuleEventType
import dev.kord.common.entity.DiscordAutoModerationRule
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.automoderation.*
import dev.kord.rest.json.request.AutoModerationRuleCreateRequest
import dev.kord.rest.json.request.AutoModerationRuleModifyRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Route
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

public class AutoModerationService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun listAutoModerationRulesForGuild(guildId: Snowflake): List<DiscordAutoModerationRule> =
        call(Route.AutoModerationRulesForGuildList) {
            keys[Route.GuildId] = guildId
        }

    public suspend fun getAutoModerationRule(guildId: Snowflake, ruleId: Snowflake): DiscordAutoModerationRule =
        call(Route.AutoModerationRuleGet) {
            keys[Route.GuildId] = guildId
            keys[Route.AutoModerationRuleId] = ruleId
        }

    public suspend fun createAutoModerationRule(
        guildId: Snowflake,
        request: AutoModerationRuleCreateRequest,
        reason: String? = null,
    ): DiscordAutoModerationRule = call(Route.AutoModerationRuleCreate) {
        keys[Route.GuildId] = guildId
        body(AutoModerationRuleCreateRequest.serializer(), request)
        auditLogReason(reason)
    }

    public suspend inline fun createKeywordAutoModerationRule(
        guildId: Snowflake,
        name: String,
        eventType: AutoModerationRuleEventType,
        builder: KeywordAutoModerationRuleCreateBuilder.() -> Unit,
    ): DiscordAutoModerationRule {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        val request = KeywordAutoModerationRuleCreateBuilder(name, eventType).apply(builder)
        return createAutoModerationRule(guildId, request.toRequest(), request.reason)
    }

    public suspend inline fun createHarmfulLinkAutoModerationRule(
        guildId: Snowflake,
        name: String,
        eventType: AutoModerationRuleEventType,
        builder: HarmfulLinkAutoModerationRuleCreateBuilder.() -> Unit,
    ): DiscordAutoModerationRule {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        val request = HarmfulLinkAutoModerationRuleCreateBuilder(name, eventType).apply(builder)
        return createAutoModerationRule(guildId, request.toRequest(), request.reason)
    }

    public suspend inline fun createSpamAutoModerationRule(
        guildId: Snowflake,
        name: String,
        eventType: AutoModerationRuleEventType,
        builder: SpamAutoModerationRuleCreateBuilder.() -> Unit,
    ): DiscordAutoModerationRule {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        val request = SpamAutoModerationRuleCreateBuilder(name, eventType).apply(builder)
        return createAutoModerationRule(guildId, request.toRequest(), request.reason)
    }

    public suspend inline fun createKeywordPresetAutoModerationRule(
        guildId: Snowflake,
        name: String,
        eventType: AutoModerationRuleEventType,
        builder: KeywordPresetAutoModerationRuleCreateBuilder.() -> Unit,
    ): DiscordAutoModerationRule {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        val request = KeywordPresetAutoModerationRuleCreateBuilder(name, eventType).apply(builder)
        return createAutoModerationRule(guildId, request.toRequest(), request.reason)
    }

    public suspend fun modifyAutoModerationRule(
        guildId: Snowflake,
        ruleId: Snowflake,
        request: AutoModerationRuleModifyRequest,
        reason: String? = null,
    ): DiscordAutoModerationRule = call(Route.AutoModerationRuleModify) {
        keys[Route.GuildId] = guildId
        keys[Route.AutoModerationRuleId] = ruleId
        body(AutoModerationRuleModifyRequest.serializer(), request)
        auditLogReason(reason)
    }

    public suspend inline fun modifyUntypedAutoModerationRule(
        guildId: Snowflake,
        ruleId: Snowflake,
        builder: UntypedAutoModerationRuleModifyBuilder.() -> Unit,
    ): DiscordAutoModerationRule {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        val request = UntypedAutoModerationRuleModifyBuilder().apply(builder)
        return modifyAutoModerationRule(guildId, ruleId, request.toRequest(), request.reason)
    }

    public suspend inline fun modifyKeywordAutoModerationRule(
        guildId: Snowflake,
        ruleId: Snowflake,
        builder: KeywordAutoModerationRuleModifyBuilder.() -> Unit,
    ): DiscordAutoModerationRule {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        val request = KeywordAutoModerationRuleModifyBuilder().apply(builder)
        return modifyAutoModerationRule(guildId, ruleId, request.toRequest(), request.reason)
    }

    public suspend inline fun modifyHarmfulLinkAutoModerationRule(
        guildId: Snowflake,
        ruleId: Snowflake,
        builder: HarmfulLinkAutoModerationRuleModifyBuilder.() -> Unit,
    ): DiscordAutoModerationRule {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        val request = HarmfulLinkAutoModerationRuleModifyBuilder().apply(builder)
        return modifyAutoModerationRule(guildId, ruleId, request.toRequest(), request.reason)
    }

    public suspend inline fun modifySpamAutoModerationRule(
        guildId: Snowflake,
        ruleId: Snowflake,
        builder: SpamAutoModerationRuleModifyBuilder.() -> Unit,
    ): DiscordAutoModerationRule {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        val request = SpamAutoModerationRuleModifyBuilder().apply(builder)
        return modifyAutoModerationRule(guildId, ruleId, request.toRequest(), request.reason)
    }

    public suspend inline fun modifyKeywordPresetAutoModerationRule(
        guildId: Snowflake,
        ruleId: Snowflake,
        builder: KeywordPresetAutoModerationRuleModifyBuilder.() -> Unit,
    ): DiscordAutoModerationRule {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        val request = KeywordPresetAutoModerationRuleModifyBuilder().apply(builder)
        return modifyAutoModerationRule(guildId, ruleId, request.toRequest(), request.reason)
    }

    public suspend fun deleteAutoModerationRule(
        guildId: Snowflake,
        ruleId: Snowflake,
        reason: String? = null,
    ): Unit = call(Route.AutoModerationRuleDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.AutoModerationRuleId] = ruleId
        auditLogReason(reason)
    }
}

package dev.kord.rest.service

import dev.kord.common.entity.AutoModerationRuleEventType
import dev.kord.common.entity.DiscordAutoModerationRule
import dev.kord.common.entity.Snowflake
import dev.kord.rest.AutoModeration
import dev.kord.rest.ById
import dev.kord.rest.Rules
import dev.kord.rest.builder.automoderation.*
import dev.kord.rest.json.request.AutoModerationRuleCreateRequest
import dev.kord.rest.json.request.AutoModerationRuleModifyRequest
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Routes
import dev.kord.rest.route.Route
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

public class AutoModerationService(public val client: HttpClient) {

    public suspend fun listAutoModerationRulesForGuild(guildId: Snowflake): List<DiscordAutoModerationRule> =
        client.get(Routes.Guilds.ById.AutoModeration.Rules(guildId)).body()

    public suspend fun getAutoModerationRule(guildId: Snowflake, ruleId: Snowflake): DiscordAutoModerationRule =
        client.get(Routes.Guilds.ById.AutoModeration.Rules.ById(guildId, ruleId)).body()

    public suspend fun createAutoModerationRule(
        guildId: Snowflake,
        request: AutoModerationRuleCreateRequest,
        reason: String? = null,
    ): DiscordAutoModerationRule =
        client.post(Routes.Guilds.ById.AutoModeration(guildId)) {
            setBody(request)
            auditLogReason(reason)
        }.body()

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

    public suspend inline fun createMentionSpamAutoModerationRule(
        guildId: Snowflake,
        name: String,
        eventType: AutoModerationRuleEventType,
        mentionLimit: Int,
        builder: MentionSpamAutoModerationRuleCreateBuilder.() -> Unit,
    ): DiscordAutoModerationRule {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        val request = MentionSpamAutoModerationRuleCreateBuilder(name, eventType, mentionLimit).apply(builder)
        return createAutoModerationRule(guildId, request.toRequest(), request.reason)
    }

    public suspend fun modifyAutoModerationRule(
        guildId: Snowflake,
        ruleId: Snowflake,
        request: AutoModerationRuleModifyRequest,
        reason: String? = null,
    ): DiscordAutoModerationRule =
        client.patch(Routes.Guilds.ById.AutoModeration.Rules.ById(guildId, ruleId)) {
            auditLogReason(reason)
            setBody(request)
        }.body()

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

    public suspend inline fun modifyMentionSpamAutoModerationRule(
        guildId: Snowflake,
        ruleId: Snowflake,
        builder: MentionSpamAutoModerationRuleModifyBuilder.() -> Unit,
    ): DiscordAutoModerationRule {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        val request = MentionSpamAutoModerationRuleModifyBuilder().apply(builder)
        return modifyAutoModerationRule(guildId, ruleId, request.toRequest(), request.reason)
    }

    public suspend fun deleteAutoModerationRule(
        guildId: Snowflake,
        ruleId: Snowflake,
        reason: String? = null,
    ): Unit =
        client.delete(Routes.Guilds.ById.AutoModeration.Rules.ById(guildId, ruleId)) {
            auditLogReason(reason)
        }.body()
}

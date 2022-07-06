package dev.kord.rest.service

import dev.kord.common.entity.DiscordAutoModerationRule
import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.request.AutoModerationRuleCreateRequest
import dev.kord.rest.json.request.AutoModerationRuleModifyRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Route

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

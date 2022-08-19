package dev.kord.core.gateway.handler

import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.cache.api.remove
import dev.kord.core.Kord
import dev.kord.core.cache.data.AutoModerationRuleData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.automoderation.AutoModerationRule
import dev.kord.core.event.automoderation.*
import dev.kord.core.event.automoderation.data.AutoModerationActionExecutionEventData
import dev.kord.gateway.*

internal class AutoModerationEventHandler : BaseGatewayEventHandler() {

    override suspend fun handle(
        event: Event,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): AutoModerationEvent? = when (event) {
        is AutoModerationRuleCreate -> handleRuleCreate(event, shard, kord, context)
        is AutoModerationRuleUpdate -> handleRuleUpdate(event, shard, kord, context)
        is AutoModerationRuleDelete -> handleRuleDelete(event, shard, kord, context)
        is AutoModerationActionExecution -> AutoModerationActionExecutionEvent(
            data = AutoModerationActionExecutionEventData.from(event.actionExecution),
            kord = kord,
            shard = shard,
            customContext = context?.get(),
        )
        else -> null
    }


    private suspend fun handleRuleCreate(
        event: AutoModerationRuleCreate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): AutoModerationRuleCreateEvent {
        val data = AutoModerationRuleData.from(event.rule)
        kord.cache.put(data)

        val rule = AutoModerationRule(data, kord)
        return AutoModerationRuleCreateEvent(rule = rule, kord = kord, shard = shard, customContext = context?.get())
    }

    private suspend fun handleRuleUpdate(
        event: AutoModerationRuleUpdate,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): AutoModerationRuleUpdateEvent {
        val data = AutoModerationRuleData.from(event.rule)
        val oldData = kord.cache.query {
            idEq(AutoModerationRuleData::id, data.id)
            idEq(AutoModerationRuleData::guildId, data.guildId)
        }.singleOrNull()
        kord.cache.put(data)

        val rule = AutoModerationRule(data, kord)
        val old = oldData?.let { AutoModerationRule(it, kord) }
        return AutoModerationRuleUpdateEvent(
            rule = rule,
            old = old,
            kord = kord,
            shard = shard,
            customContext = context?.get(),
        )
    }

    private suspend fun handleRuleDelete(
        event: AutoModerationRuleDelete,
        shard: Int,
        kord: Kord,
        context: LazyContext?,
    ): AutoModerationRuleDeleteEvent {
        val data = AutoModerationRuleData.from(event.rule)
        kord.cache.remove {
            idEq(AutoModerationRuleData::id, data.id)
            idEq(AutoModerationRuleData::guildId, data.guildId)
        }

        val rule = AutoModerationRule(data, kord)
        return AutoModerationRuleDeleteEvent(rule = rule, kord = kord, shard = shard, customContext = context?.get())
    }
}

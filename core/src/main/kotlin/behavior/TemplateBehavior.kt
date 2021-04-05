package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Template
import dev.kord.rest.builder.template.GuildFromTemplateCreateBuilder
import dev.kord.rest.builder.template.GuildTemplateModifyBuilder
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface TemplateBehavior : KordObject {
    val guildId: Snowflake
    val code: String

    suspend fun sync(): Template {
        val response = kord.rest.template.syncGuildTemplate(guildId, code)
        val data = response.toData()
        return Template(data, kord)
    }

    suspend fun delete(): Template {
        val response = kord.rest.template.deleteGuildTemplate(guildId, code)
        val data = response.toData()
        return Template(data, kord)
    }

}


@OptIn(ExperimentalContracts::class)
suspend fun TemplateBehavior.edit(builder: GuildTemplateModifyBuilder.() -> Unit): Template {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.template.modifyGuildTemplate(guildId, code, builder)
    val data = response.toData()
    return Template(data, kord)
}

@OptIn(ExperimentalContracts::class)
suspend fun TemplateBehavior.createGuild(name: String, builder: GuildFromTemplateCreateBuilder.() -> Unit): Guild {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.template.createGuildFromTemplate(code, name, builder)
    val data = response.toData()
    return Guild(data, kord)
}


 fun TemplateBehavior(guildId: Snowflake, code: String, kord: Kord): TemplateBehavior =
    object : TemplateBehavior {
        override val code: String = code
        override val guildId: Snowflake = guildId
        override val kord: Kord = kord

        override fun hashCode(): Int = Objects.hash(code)

        override fun equals(other: Any?): Boolean =
            other is TemplateBehavior && other.code == code


        override fun toString(): String {
            return "TemplateBehavior(code=$code, guildId=$guildId)"
        }
    }
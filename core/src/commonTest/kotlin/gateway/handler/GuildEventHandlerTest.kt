package dev.kord.core.gateway.handler

import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.common.annotation.KordInternal
import dev.kord.common.entity.DiscordRemovedGuildMember
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.GuildMemberFlags
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.cache.KordCacheBuilder
import dev.kord.core.cache.data.MemberData
import dev.kord.core.cache.data.UserData
import dev.kord.core.cache.idEq
import dev.kord.core.cache.registerKordData
import dev.kord.core.event.guild.MemberLeaveEvent
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.core.gateway.ShardEvent
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Gateway
import dev.kord.gateway.GuildMemberRemove
import dev.kord.gateway.builder.Shards
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.service.RestClient
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Instant

internal class GuildEventHandlerTest {

    @Test
    @OptIn(KordInternal::class)
    @JsName("test1")
    fun `member leaving one guild keeps cached user and members in other guilds`() = runTest {
        val cache = KordCacheBuilder().build().also { it.registerKordData() }
        val interceptor = DefaultGatewayEventInterceptor()
        val kord = Kord(
            ClientResources("", Snowflake(0u), Shards(0), maxConcurrency = 1, HttpClient(), EntitySupplyStrategy.cache),
            cache,
            DefaultMasterGateway(mapOf(0 to Gateway.none())),
            RestClient(KtorRequestHandler("")),
            Snowflake(0u),
            MutableSharedFlow(),
            Dispatchers.Default,
            interceptor,
        )

        val userId = Snowflake(100u)
        val guildA = Snowflake(200u)
        val guildB = Snowflake(300u)

        val userData = UserData(id = userId, username = "alice")
        val memberA = MemberData(
            userId = userId,
            guildId = guildA,
            roles = emptyList(),
            joinedAt = Instant.fromEpochMilliseconds(0),
            flags = GuildMemberFlags(),
        )
        val memberB = MemberData(
            userId = userId,
            guildId = guildB,
            roles = emptyList(),
            joinedAt = Instant.fromEpochMilliseconds(0),
            flags = GuildMemberFlags(),
        )

        cache.put(userData)
        cache.put(memberA)
        cache.put(memberB)

        val gatewayEvent = GuildMemberRemove(
            DiscordRemovedGuildMember(
                guildId = guildA,
                user = DiscordUser(id = userId, username = "alice", avatar = null),
            ),
            sequence = 1,
        )
        val coreEvent = interceptor.handle(ShardEvent(gatewayEvent, Gateway.none(), shard = 0), kord)
        val leaveEvent = assertIs<MemberLeaveEvent>(coreEvent)
        assertEquals(guildA, leaveEvent.guildId)
        assertEquals(userId, leaveEvent.user.id)
        val oldMember = assertNotNull(leaveEvent.old)
        assertEquals(guildA, oldMember.guildId)

        assertNull(
            cache.query {
                idEq(MemberData::userId, userId)
                idEq(MemberData::guildId, guildA)
            }.singleOrNull(),
        )
        assertNull(kord.defaultSupplier.getMemberOrNull(guildA, userId))

        assertNotNull(cache.query { idEq(UserData::id, userId) }.singleOrNull())
        assertNotNull(kord.defaultSupplier.getUserOrNull(userId))

        assertNotNull(
            cache.query {
                idEq(MemberData::userId, userId)
                idEq(MemberData::guildId, guildB)
            }.singleOrNull(),
        )
        assertNotNull(kord.defaultSupplier.getMemberOrNull(guildB, userId))
    }
}

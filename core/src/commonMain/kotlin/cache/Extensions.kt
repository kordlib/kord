package dev.kord.core.cache

import dev.kord.cache.api.observables.Cache
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.BanData
import dev.kord.core.cache.data.MemberData
import dev.kord.core.cache.data.VoiceStateData
import io.ktor.http.*

public suspend fun Cache<UnionSnowflake, MemberData>.get(guildId: Snowflake, userId: Snowflake): MemberData? {
    val key = UnionSnowflake {
        put("guildId", guildId)
        put("userId", userId)
    }
    return get(key)
}


public suspend fun Cache<UnionSnowflake, MemberData>.set(guildId: Snowflake, userId: Snowflake, value: MemberData) {
    val key = UnionSnowflake {
        put("guildId", guildId)
        put("userId", userId)
    }
    set(key, value)
}


public suspend fun Cache<UnionSnowflake, VoiceStateData>.get(guildId: Snowflake, userId: Snowflake): VoiceStateData? {
    val key = UnionSnowflake {
        put("guildId", guildId)
        put("userId", userId)
    }
    return get(key)
}


public suspend fun Cache<UnionSnowflake, VoiceStateData>.set(guildId: Snowflake, userId: Snowflake, value: VoiceStateData) {
    val key = UnionSnowflake {
        put("guildId", guildId)
        put("userId", userId)
    }
    set(key, value)
}


public suspend fun Cache<UnionSnowflake, BanData>.set(guildId: Snowflake, userId: Snowflake, value: BanData) {
    val key = UnionSnowflake {
        put("guildId", guildId)
        put("userId", userId)
    }
    set(key, value)
}

public suspend fun Cache<UnionSnowflake, BanData>.get(guildId: Snowflake, userId: Snowflake): BanData? {
    val key = UnionSnowflake {
        put("guildId", guildId)
        put("userId", userId)
    }
    return get(key)
}

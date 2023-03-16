package dev.kord.core

import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.*
import kotlinx.coroutines.flow.Flow


public interface DataRetriever {
    public suspend fun getChannelsCount(): Long

    public suspend fun getChannelsInGuildCount(guildId: Snowflake): Long

    public suspend fun getStickersCount(): Long

    public suspend fun getStickersInGuildCount(guildId: Snowflake): Long

    public suspend fun getEmojisCount(): Long

    public suspend fun getEmojisInGuildCount(guildId: Snowflake): Long

    public suspend fun getGuildsCount(): Long

    public suspend fun getMembersCount(): Long

    public suspend fun getMembersInGuildCount(guildId: Snowflake): Long

    public suspend fun getMessagesCount(): Long

    public suspend fun getMessagesInChannelCount(channelId: Snowflake): Long

    public suspend fun getPresencesCount(): Long

    public suspend fun getPresenceInGuildCount(guildId: Snowflake): Long

    public suspend fun getRolesCount(): Long

    public suspend fun getRolesInGuildCount(guildId: Snowflake): Long

    public suspend fun getUsersCount(): Long

    public suspend fun getVoiceStatesInGuildCount(guildId: Snowflake): Long

    public suspend fun getVoiceStatesInChannelCount(guildId: Snowflake, channelId: Snowflake): Long

    public suspend fun getVoiceStates(): Long

    public val channels: Flow<ChannelData>

    public suspend fun getChannelsInGuild(guildId: Snowflake): Flow<ChannelData>

    public suspend fun getChannel(channelId: Snowflake): ChannelData

    public val stickers: Flow<StickerData>

    public suspend fun getStickersInGuild(guildId: Snowflake): Flow<StickerData>

    public suspend fun getSticker(guildId: Snowflake, stickerId: Snowflake): StickerData

    public val emojis: Flow<EmojiData>

    public suspend fun getEmojisInGuild(guildId: Snowflake): Flow<EmojiData>

    public suspend fun getEmoji(guildId: Snowflake, emojiId: Snowflake): EmojiData

    public val guilds: Flow<EmojiData>

    public suspend fun getGuild(guildId: Snowflake): GuildData

    public val members: Flow<MemberData>

    public suspend fun getMembersInGuild(guildId: Snowflake): Flow<MemberData>

    public suspend fun getExactMembersInGuild(guildId: Snowflake): Flow<MemberData>

    public suspend fun getMember(guildId: Snowflake, userId: Snowflake): MemberData

    public val messages: Flow<MessageData>

    public suspend fun getMessagesInChannel(channelId: Snowflake): Flow<MessageData>

    public suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): MessageData

    public val presences: Flow<PresenceData>

    public suspend fun getPresencesInGuild(guildId: Snowflake): Flow<PresenceData>

    public suspend fun getPresence(guildId: Snowflake, userId: Snowflake): PresenceData

    public val roles: Flow<RoleData>

    public suspend fun getRolesInGuild(guildId: Snowflake): Flow<RoleData>

    public suspend fun getRole(roleId: Snowflake): RoleData

    public val users: Flow<UserData>

    public suspend fun getUser(userId: Snowflake): UserData

    public val voiceStates: Flow<VoiceStateData>

    public suspend fun getVoiceStatesInChannel(guildId: Snowflake, channelId: Snowflake): Flow<VoiceStateData>

    public suspend fun getVoiceStatesInGuild(guildId: Snowflake): Flow<VoiceStateData>

    public suspend fun getThreadMember(threadId: Snowflake, userId: Snowflake): ThreadMemberData

    public suspend fun getMembersInThread(threadId: Snowflake): Flow<ThreadMemberData>

}


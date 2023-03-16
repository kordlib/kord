package dev.kord.core

import dev.kord.core.cache.data.*
import dev.kord.gateway.*

public interface GatewayDataUpdater {
    public suspend fun onChannelCreate(shardIndex: Int, event: ChannelCreate?)
    public suspend fun onChannelDelete(shardIndex: Int, event: ChannelDelete?): ChannelData
    public suspend fun onChannelUpdate(shardIndex: Int, event: ChannelUpdate?): ChannelData
    public suspend fun onGuildCreate(shardIndex: Int, event: GuildCreate?)
    public suspend fun onGuildDelete(shardIndex: Int, event: GuildDelete?): GuildData
    public suspend fun onGuildEmojisUpdate(shardIndex: Int, event: GuildEmojisUpdate?): Set<EmojiData>?
    public suspend fun onGuildMemberAdd(shardIndex: Int, event: GuildMemberAdd?)
    public suspend fun onGuildMemberRemove(shardIndex: Int, event: GuildMemberRemove?): MemberData
    public suspend fun onGuildMembersChunk(shardIndex: Int, event: GuildMembersChunk?)
    public suspend fun onGuildMemberUpdate(shardIndex: Int, event: GuildMemberUpdate?): MemberData
    public suspend fun onGuildRoleCreate(shardIndex: Int, event: GuildRoleCreate?)
    public suspend fun onGuildRoleDelete(shardIndex: Int, event: GuildRoleDelete?): RoleData
    public suspend fun onGuildRoleUpdate(shardIndex: Int, event: GuildRoleUpdate?): RoleData
    public suspend fun onGuildUpdate(shardIndex: Int, event: GuildUpdate?): GuildData
    public suspend fun onMessageCreate(shardIndex: Int, event: MessageCreate?)
    public suspend fun onMessageDelete(shardIndex: Int, event: MessageDelete?): MessageData
    public suspend fun onMessageDeleteBulk(shardIndex: Int, event: MessageDeleteBulk?): Set<MessageData>?
    public suspend fun onMessageReactionAdd(shardIndex: Int, event: MessageReactionAdd?)
    public suspend fun onMessageReactionRemove(shardIndex: Int, event: MessageReactionRemove?)
    public suspend fun onMessageReactionRemoveAll(shardIndex: Int, event: MessageReactionRemoveAll?)
    public suspend fun onMessageReactionRemoveEmoji(shardIndex: Int, event: MessageReactionRemoveEmoji?)
    public suspend fun onMessageUpdate(shardIndex: Int, event: MessageUpdate?): MessageData
    public suspend fun onPresenceUpdate(shardIndex: Int, event: PresenceUpdate?): PresenceData
    public suspend fun onReady(event: Ready?)
    public suspend fun onStageInstanceCreate(shardIndex: Int, event: ChannelCreate)
    public suspend fun onStageInstanceDelete(shardIndex: Int, event: ChannelDelete?): StageInstanceData
    public suspend fun onUserUpdate(shardIndex: Int, event: UserUpdate?): UserData
    public suspend fun onVoiceStateUpdateevent(shardIndex: Int, event: VoiceStateUpdate?): VoiceStateData
    public suspend fun onGuildMembersCompletion(guildId: Long)
    public suspend fun onThreadCreate(shardIndex: Int, event: ThreadCreate?)
    public suspend fun onThreadUpdate(shardIndex: Int, event: ThreadUpdate?): ChannelData
    public suspend fun onThreadDelete(shardIndex: Int, event: ThreadDelete?)
    public suspend fun onThreadListSync(shardIndex: Int, event: ThreadListSync?)
    public suspend fun onThreadMemberUpdate(shardIndex: Int, event: ThreadMemberUpdate?): ThreadMemberData
}
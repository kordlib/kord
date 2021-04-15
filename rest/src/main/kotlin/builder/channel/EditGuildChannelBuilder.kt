package dev.kord.rest.builder.channel

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.json.request.ChannelModifyPatchRequest

@KordDsl
class TextChannelModifyBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()

    private var _position: OptionalInt? = OptionalInt.Missing
    var position: Int? by ::_position.delegate()

    private var _topic: Optional<String?> = Optional.Missing()
    var topic: String? by ::_topic.delegate()

    private var _nsfw: OptionalBoolean? = OptionalBoolean.Missing
    var nsfw: Boolean? by ::_nsfw.delegate()

    private var _parentId: OptionalSnowflake? = OptionalSnowflake.Missing
    var parentId: Snowflake? by ::_parentId.delegate()

    private var _rateLimitPerUser: OptionalInt? = OptionalInt.Missing
    var rateLimitPerUser: Int? by ::_rateLimitPerUser.delegate()

    private var _permissionOverwrites: Optional<MutableSet<Overwrite>?> = Optional.Missing()
    var permissionOverwrites: MutableSet<Overwrite>? by ::_permissionOverwrites.delegate()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
        name = _name,
        position = _position,
        topic = _topic,
        nsfw = _nsfw,
        rateLimitPerUser = _rateLimitPerUser,
        permissionOverwrites = _permissionOverwrites,
        parentId = _parentId
    )

}

@KordDsl
class VoiceChannelModifyBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()

    private var _position: OptionalInt? = OptionalInt.Missing
    var position: Int? by ::_position.delegate()

    private var _topic: Optional<String?> = Optional.Missing()
    var topic: String? by ::_topic.delegate()

    private var _parentId: OptionalSnowflake? = OptionalSnowflake.Missing
    var parentId: Snowflake? by ::_parentId.delegate()

    private var _permissionOverwrites: Optional<MutableSet<Overwrite>?> = Optional.Missing()
    var permissionOverwrites: MutableSet<Overwrite>? by ::_permissionOverwrites.delegate()

    private var _bitrate: OptionalInt? = OptionalInt.Missing
    var bitrate: Int? by ::_bitrate.delegate()

    private var _userLimit: OptionalInt? = OptionalInt.Missing
    var userLimit: Int? by ::_userLimit.delegate()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
        name = _name,
        position = _position,
        parentId = _parentId,
        bitrate = _bitrate,
        userLimit = _userLimit,
        topic = _topic,
        permissionOverwrites = _permissionOverwrites
    )

}

@KordDsl
class NewsChannelModifyBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()

    private var _position: OptionalInt? = OptionalInt.Missing
    var position: Int? by ::_position.delegate()

    private var _topic: Optional<String?> = Optional.Missing()
    var topic: String? by ::_topic.delegate()

    private var _nsfw: OptionalBoolean? = OptionalBoolean.Missing
    var nsfw: Boolean? by ::_nsfw.delegate()

    private var _parentId: OptionalSnowflake? = OptionalSnowflake.Missing
    var parentId: Snowflake? by ::_parentId.delegate()

    private var _rateLimitPerUser: OptionalInt? = OptionalInt.Missing
    var rateLimitPerUser: Int? by ::_rateLimitPerUser.delegate()

    private var _permissionOverwrites: Optional<MutableSet<Overwrite>?> = Optional.Missing()
    var permissionOverwrites: MutableSet<Overwrite>? by ::_permissionOverwrites.delegate()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
        name = _name,
        position = _position,
        topic = _topic,
        nsfw = _nsfw,
        permissionOverwrites = _permissionOverwrites,
        parentId = _parentId
    )
}

@KordDsl
class StoreChannelModifyBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()

    private var _position: OptionalInt? = OptionalInt.Missing
    var position: Int? by ::_position.delegate()

    private var _permissionOverwrites: Optional<MutableSet<Overwrite>?> = Optional.Missing()
    var permissionOverwrites: MutableSet<Overwrite>? by ::_permissionOverwrites.delegate()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
        name = _name,
        position = _position,
        permissionOverwrites = _permissionOverwrites
    )

}
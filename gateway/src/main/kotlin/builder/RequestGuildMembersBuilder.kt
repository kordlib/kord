package dev.kord.gateway.builder

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.gateway.GuildMembersChunkData
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.gateway.RequestGuildMembers

/**
 * A builder for a [RequestGuildMembers] command.
 *
 * @param guildId The id of the guild on which to execute the command.
 */
@OptIn(PrivilegedIntent::class)
public class RequestGuildMembersBuilder(public var guildId: Snowflake) {

    private var _query: Optional<String> = Optional.Missing()

    /**
     * The prefix to match usernames against. Use an empty string to match against all members.
     * [Intent.GuildMembers] is required when setting the [query] to `""` and [limit] to `0`.
     */
    public var query: String? by ::_query.delegate()

    private var _limit: OptionalInt = OptionalInt.Missing

    /**
     * The maximum number of members to match against when using a [query].
     * Use `0` to request all members.
     * [Intent.GuildMembers] is required when setting the [query] to `""` and [limit] to `0`.
     */
    public var limit: Int? by ::_limit.delegate()

    private var _presences: OptionalBoolean = OptionalBoolean.Missing

    /**
     * Whether [GuildMembersChunkData.presences] should be present in the response.
     * [Intent.GuildPresences] is required to enable [presences].
     */
    public var presences: Boolean? by ::_presences.delegate()

    /**
     * The ids of the user to match against.
     */
    public var userIds: MutableSet<Snowflake> = mutableSetOf()

    private var _nonce: Optional<String> = Optional.Missing()

    /**
     * A nonce to identify the [GuildMembersChunkData.nonce] responses.
     */
    public var nonce: String? by ::_nonce.delegate()

    /**
     * Utility function that sets the required fields for requesting all members.
     */
    public fun requestAllMembers() {
        limit = 0
        query = ""
        userIds.clear()
    }

    public fun toRequest(): RequestGuildMembers = RequestGuildMembers(
        guildId, _query, _limit, _presences, Optional.missingOnEmpty(userIds), _nonce
    )

}

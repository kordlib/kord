package dev.kord.core.builder.kord

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord

/**
 * The rest only Kord builder. You probably want to invoke the [DSL builder][Kord.restOnly] instead.
 */
@KordExperimental
public class KordRestOnlyBuilder(public override var token: String) : RestOnlyBuilder() {

    private var id: Snowflake? = null

    override var applicationId: Snowflake
        get() = id ?: getBotIdFromToken(token)
        set(value) {
            id = value
        }
}

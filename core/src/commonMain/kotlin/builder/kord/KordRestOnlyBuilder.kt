package dev.kord.core.builder.kord

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord

/**
 * The rest only Kord builder. You probably want to invoke the [DSL builder][Kord.restOnly] instead.
 */
@KordExperimental
public class KordRestOnlyBuilder(override var token: String) : RestOnlyBuilder() {
    override var applicationId: Snowflake? = null
}

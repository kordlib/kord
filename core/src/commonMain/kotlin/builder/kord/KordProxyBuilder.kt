package dev.kord.core.builder.kord

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.Snowflake

/**
 * The proxy Kord builder. You probably want to invoke the [DSL builder][Kord.proxy] instead.
 */
@KordExperimental
public class KordProxyBuilder(override var applicationId: Snowflake) : RestOnlyBuilder() {

    override val token: String get() = ""
}

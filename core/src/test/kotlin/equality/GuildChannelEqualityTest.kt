package equality

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity

interface GuildChannelEqualityTest<T: KordEntity> :
        ChannelEqualityTest<T>, GuildEntityEqualityTest<T> {


    companion object {
        operator fun<T: KordEntity> invoke(supplier: (id: Snowflake, guildId: Snowflake) -> T) = object: GuildChannelEqualityTest<T> {
            override fun newEntity(id: Snowflake, guildId: Snowflake): T = supplier(id, guildId)
        }
    }

}
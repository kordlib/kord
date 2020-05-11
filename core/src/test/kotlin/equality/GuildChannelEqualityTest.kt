package equality

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.Entity

interface GuildChannelEqualityTest<T: Entity> :
        ChannelEqualityTest<T>, GuildEntityEqualityTest<T> {


    companion object {
        operator fun<T: Entity> invoke(supplier: (id: Snowflake, guildId: Snowflake) -> T) = object: GuildChannelEqualityTest<T> {
            override fun newEntity(id: Snowflake, guildId: Snowflake): T = supplier(id, guildId)
        }
    }

}
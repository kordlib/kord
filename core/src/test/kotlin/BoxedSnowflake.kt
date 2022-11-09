import dev.kord.common.entity.Snowflake

/**
 * A class that wraps a Snowflake, because you can't use `lateinit` with value classes
 */
data class BoxedSnowflake(val value: Snowflake)
package dev.kord.core.cache

import dev.kord.cache.api.QueryBuilder
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.optionalSnowflake
import kotlin.reflect.KProperty1

public fun <T : Any> QueryBuilder<T>.idEq(property: KProperty1<T, Snowflake?>, value: Snowflake?) {
    property.eq(value)
}

@JvmName("optionalIdEq")
public fun <T : Any> QueryBuilder<T>.idEq(property: KProperty1<T, OptionalSnowflake>, value: Snowflake?) {
    property.eq(value.optionalSnowflake())
}

@JvmName("optionalNullableIdEq")
public fun <T : Any> QueryBuilder<T>.idEq(property: KProperty1<T, OptionalSnowflake?>, value: Snowflake?) {
    property.eq(value.optionalSnowflake())
}

public fun <T : Any> QueryBuilder<T>.idEq(property: KProperty1<T, Optional<String>>, value: String?) {
    property.eq(Optional(value))
}

public fun <T : Any> QueryBuilder<T>.idGt(property: KProperty1<T, Snowflake>, value: Snowflake) {
    property.gt(value)
}

public fun <T : Any> QueryBuilder<T>.idLt(property: KProperty1<T, Snowflake>, value: Snowflake) {
    property.lt(value)
}

@JvmName("stringEq")
public fun <T : Any> QueryBuilder<T>.idEq(property: KProperty1<T, String?>, value: String?) {
    property.eq(value)
}

@JvmName("booleanEq")
public fun <T : Any> QueryBuilder<T>.idEq(property: KProperty1<T, Boolean?>, value: Boolean?) {
    property.eq(value)
}

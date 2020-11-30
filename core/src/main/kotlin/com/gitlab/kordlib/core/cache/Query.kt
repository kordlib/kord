package dev.kord.core.cache

import com.gitlab.kordlib.cache.api.QueryBuilder
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.optionalSnowflake
import kotlin.reflect.KProperty1

fun <T : Any> QueryBuilder<T>.idEq(property: KProperty1<T, Snowflake?>, value: Snowflake?) {
    property.eq(value)
}

@JvmName("optionalIdEq")
fun <T : Any> QueryBuilder<T>.idEq(property: KProperty1<T, OptionalSnowflake>, value: Snowflake?) {
    property.eq(value.optionalSnowflake())
}

fun <T : Any> QueryBuilder<T>.idEq(property: KProperty1<T, Optional<String>>, value: String?) {
    property.eq(Optional(value))
}

fun <T : Any> QueryBuilder<T>.idGt(property: KProperty1<T, Snowflake>, value: Snowflake) {
    property.gt(value)
}

@JvmName("stringEq")
fun <T : Any> QueryBuilder<T>.idEq(property: KProperty1<T, String?>, value: String?) {
    property.eq(value)
}

@JvmName("booleanEq")
fun <T : Any> QueryBuilder<T>.idEq(property: KProperty1<T, Boolean?>, value: Boolean?) {
    property.eq(value)
}
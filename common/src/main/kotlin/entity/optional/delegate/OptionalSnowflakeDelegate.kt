package dev.kord.common.entity.optional.delegate

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.common.entity.optional.value
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty


public fun KMutableProperty0<OptionalSnowflake>.delegate(): ReadWriteProperty<Any?, Snowflake?> =
    object : ReadWriteProperty<Any?, Snowflake?> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): Snowflake? {
            return this@delegate.get().value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Snowflake?) {
            val optional = if (value == null) OptionalSnowflake.Missing
            else OptionalSnowflake.Value(Snowflake(value.value))
            this@delegate.set(optional)
        }
    }

@JvmName("delegateOptional")
public fun KMutableProperty0<OptionalSnowflake?>.delegate(): ReadWriteProperty<Any?, Snowflake?> =
    object : ReadWriteProperty<Any?, Snowflake?> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): Snowflake? {
            return this@delegate.get().value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Snowflake?) {
            this@delegate.set(value?.optionalSnowflake())
        }

    }

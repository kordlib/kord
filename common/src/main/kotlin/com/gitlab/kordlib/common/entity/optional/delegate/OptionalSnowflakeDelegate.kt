package com.gitlab.kordlib.common.entity.optional.delegate

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import com.gitlab.kordlib.common.entity.optional.optionalSnowflake
import com.gitlab.kordlib.common.entity.optional.value
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty


fun KMutableProperty0<OptionalSnowflake>.delegate() : ReadWriteProperty<Any?, Snowflake?> = object : ReadWriteProperty<Any?, Snowflake?> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): Snowflake? {
        return this@delegate.get().value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Snowflake?) {
        val optional = if(value == null) OptionalSnowflake.Missing
        else OptionalSnowflake.Value(value.value)
        this@delegate.set(optional)
    }
}

@JvmName("delegateOptional")
fun KMutableProperty0<OptionalSnowflake?>.delegate() : ReadWriteProperty<Any?, Snowflake?> = object : ReadWriteProperty<Any?, Snowflake?> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): Snowflake? {
        return this@delegate.get().value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Snowflake?) {
        this@delegate.set(value?.optionalSnowflake())
    }

}

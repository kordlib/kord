package com.gitlab.kordlib.common.entity.optional.delegate

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import com.gitlab.kordlib.common.entity.optional.optionalSnowflake
import com.gitlab.kordlib.common.entity.optional.value
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

operator fun <T> KMutableProperty0<OptionalSnowflake>.provideDelegate(
        thisRef: T, property: KProperty<*>,
): ReadWriteProperty<T, Snowflake?> = object : ReadWriteProperty<T, Snowflake?> {

    override fun getValue(thisRef: T, property: KProperty<*>): Snowflake? {
        return this@provideDelegate.get().value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: Snowflake?) {
        val optional = if(value == null) OptionalSnowflake.Missing
        else OptionalSnowflake.Value(value.value)
        this@provideDelegate.set(optional)
    }

}
@JvmName("provideNullableDelegate")
operator fun <T> KMutableProperty0<OptionalSnowflake?>.provideDelegate(
        thisRef: T, property: KProperty<*>,
): ReadWriteProperty<T, Snowflake?> = object : ReadWriteProperty<T, Snowflake?> {

    override fun getValue(thisRef: T, property: KProperty<*>): Snowflake? {
        return this@provideDelegate.get().value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: Snowflake?) {
        this@provideDelegate.set(value?.optionalSnowflake())
    }

}

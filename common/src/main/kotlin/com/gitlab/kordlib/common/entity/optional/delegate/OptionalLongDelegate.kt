package com.gitlab.kordlib.common.entity.optional.delegate

import com.gitlab.kordlib.common.entity.optional.OptionalLong
import com.gitlab.kordlib.common.entity.optional.optional
import com.gitlab.kordlib.common.entity.optional.value
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

operator fun <T> KMutableProperty0<OptionalLong>.provideDelegate(
        thisRef: T, property: KProperty<*>,
): ReadWriteProperty<T, Long?> = object : ReadWriteProperty<T, Long?> {

    override fun getValue(thisRef: T, property: KProperty<*>): Long? {
        return this@provideDelegate.get().value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: Long?) {
        val optional = if (value == null) OptionalLong.Missing
        else OptionalLong.Value(value)
        this@provideDelegate.set(optional)
    }

}

@JvmName("provideNullableDelegate")
operator fun <T> KMutableProperty0<OptionalLong?>.provideDelegate(
        thisRef: T, property: KProperty<*>,
): ReadWriteProperty<T, Long?> = object : ReadWriteProperty<T, Long?> {

    override fun getValue(thisRef: T, property: KProperty<*>): Long? {
        return this@provideDelegate.get().value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: Long?) {
        this@provideDelegate.set(value?.optional())
    }

}

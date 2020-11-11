package com.gitlab.kordlib.common.entity.optional.delegate

import com.gitlab.kordlib.common.entity.optional.OptionalInt
import com.gitlab.kordlib.common.entity.optional.optionalInt
import com.gitlab.kordlib.common.entity.optional.value
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

operator fun <T> KMutableProperty0<OptionalInt>.provideDelegate(
        thisRef: T, property: KProperty<*>,
): ReadWriteProperty<T, Int?> = object : ReadWriteProperty<T, Int?> {

    override fun getValue(thisRef: T, property: KProperty<*>): Int? {
        return this@provideDelegate.get().value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: Int?) {
        val optional = if (value == null) OptionalInt.Missing
        else OptionalInt.Value(value)
        this@provideDelegate.set(optional)
    }

}

@JvmName("provideNullableDelegate")
operator fun <T> KMutableProperty0<OptionalInt?>.provideDelegate(
        thisRef: T, property: KProperty<*>,
): ReadWriteProperty<T, Int?> = object : ReadWriteProperty<T, Int?> {

    override fun getValue(thisRef: T, property: KProperty<*>): Int? {
        return this@provideDelegate.get().value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: Int?) {
        this@provideDelegate.set(value?.optionalInt())
    }

}

package com.gitlab.kordlib.common.entity.optional.delegate

import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import com.gitlab.kordlib.common.entity.optional.optional
import com.gitlab.kordlib.common.entity.optional.value
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

operator fun <T> KMutableProperty0<OptionalBoolean>.provideDelegate(
        thisRef: T, property: KProperty<*>,
): ReadWriteProperty<T, Boolean?> = object : ReadWriteProperty<T, Boolean?> {

    override fun getValue(thisRef: T, property: KProperty<*>): Boolean? {
        return this@provideDelegate.get().value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: Boolean?) {
        val optional = if (value == null) OptionalBoolean.Missing
        else OptionalBoolean.Value(value)
        this@provideDelegate.set(optional)
    }

}

@JvmName("provideNullableDelegate")
operator fun <T> KMutableProperty0<OptionalBoolean?>.provideDelegate(
        thisRef: T, property: KProperty<*>,
): ReadWriteProperty<T, Boolean?> = object : ReadWriteProperty<T, Boolean?> {

    override fun getValue(thisRef: T, property: KProperty<*>): Boolean? {
        return this@provideDelegate.get().value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: Boolean?) {
        this@provideDelegate.set(value?.optional())
    }

}

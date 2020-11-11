package com.gitlab.kordlib.common.entity.optional.delegate

import com.gitlab.kordlib.common.entity.optional.Optional
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

operator fun <T, V : Any> KMutableProperty0<Optional<V>>.provideDelegate(
        thisRef: T, property: KProperty<*>,
): ReadWriteProperty<T, V?> = object : ReadWriteProperty<T, V?> {

    override fun setValue(thisRef: T, property: KProperty<*>, value: V?) {
        val optional = if (value == null) Optional.Missing()
        else Optional.Value(value)

        this@provideDelegate.set(optional)
    }

    override fun getValue(thisRef: T, property: KProperty<*>): V? {
        return when (val optional = this@provideDelegate.get()) {
            is Optional.Value -> optional.value
            else -> null
        }
    }
}

@JvmName("provideNullableDelegate")
operator fun <T, V : Any> KMutableProperty0<Optional<V?>>.provideDelegate(
        thisRef: T, property: KProperty<*>,
): ReadWriteProperty<T, V?> = object : ReadWriteProperty<T, V?> {

    override fun getValue(thisRef: T, property: KProperty<*>): V? {
        return this@provideDelegate.get().value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V?) {
        this@provideDelegate.set(Optional(value))
    }

}

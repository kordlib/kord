package dev.kord.common.entity.optional.delegate

import dev.kord.common.entity.optional.Optional
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

public fun <V : Any> KMutableProperty0<Optional<V>>.delegate(): ReadWriteProperty<Any?, V?> =
    object : ReadWriteProperty<Any?, V?> {

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: V?) {
            val optional = if (value == null) Optional.Missing()
            else Optional.Value(value)

            this@delegate.set(optional)
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): V? {
            return when (val optional = this@delegate.get()) {
                is Optional.Value -> optional.value
                else -> null
            }
        }
    }

public fun <V : Any> KMutableProperty0<Optional<List<V>>>.delegateList(): ReadWriteProperty<Any?, List<V>> =
    object : ReadWriteProperty<Any?, List<V>> {

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: List<V>) {
            val optional = if (value.isEmpty()) Optional.Missing()
            else Optional.Value(value)

            this@delegateList.set(optional)
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): List<V> {
            return when (val optional = this@delegateList.get()) {
                is Optional.Value -> optional.value
                else -> emptyList()
            }
        }
    }

@JvmName("provideNullableDelegate")
public fun <V : Any> KMutableProperty0<Optional<V?>>.delegate(): ReadWriteProperty<Any?, V?> =
    object : ReadWriteProperty<Any?, V?> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): V? {
            return this@delegate.get().value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: V?) {
            this@delegate.set(Optional(value))
        }

    }

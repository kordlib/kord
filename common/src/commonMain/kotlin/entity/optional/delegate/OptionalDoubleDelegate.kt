package dev.kord.common.entity.optional.delegate

import dev.kord.common.entity.optional.OptionalDouble
import dev.kord.common.entity.optional.optionalDouble
import dev.kord.common.entity.optional.value
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

@JsName("doubleDelegate")
public fun KMutableProperty0<OptionalDouble>.delegate(): ReadWriteProperty<Any?, Double?> =
    object : ReadWriteProperty<Any?, Double?> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): Double? {
            return this@delegate.get().value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double?) {
            val optional = if (value == null) OptionalDouble.Missing
            else OptionalDouble.Value(value)
            this@delegate.set(optional)
        }
    }

@JvmName("provideNullableDelegate")
public fun KMutableProperty0<OptionalDouble?>.delegate(): ReadWriteProperty<Any?, Double?> =
    object : ReadWriteProperty<Any?, Double?> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): Double? {
            return this@delegate.get().value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double?) {
            this@delegate.set(value?.optionalDouble())
        }
    }

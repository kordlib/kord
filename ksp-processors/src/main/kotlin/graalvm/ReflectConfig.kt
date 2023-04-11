@file:OptIn(ExperimentalSerializationApi::class)

package dev.kord.ksp.graalvm

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.ALWAYS
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    encodeDefaults = false
    prettyPrint = true
    prettyPrintIndent = "  "
}

fun List<ReflectConfigEntry>.encodeToReflectConfigJson() = json.encodeToString(this)

/**
 * An entry of a
 * [`reflect-config.json`](https://www.graalvm.org/latest/reference-manual/native-image/metadata/#specifying-reflection-metadata-in-json)
 * file.
 */
@Serializable
data class ReflectConfigEntry(
    val condition: Condition? = null,
    val name: String,
    val methods: List<Method> = emptyList(),
    val queriedMethods: List<Method> = emptyList(),
    val fields: List<Field> = emptyList(),
    val allDeclaredMethods: Boolean = false,
    val allDeclaredFields: Boolean = false,
    val allDeclaredConstructors: Boolean = false,
    val allPublicMethods: Boolean = false,
    val allPublicFields: Boolean = false,
    val allPublicConstructors: Boolean = false,
    val queryAllDeclaredMethods: Boolean = false,
    val queryAllDeclaredConstructors: Boolean = false,
    val queryAllPublicMethods: Boolean = false,
    val queryAllPublicConstructors: Boolean = false,
    val unsafeAllocated: Boolean = false,
) {
    @Serializable
    data class Condition(val typeReachable: String)

    @Serializable
    data class Method(val name: String, @EncodeDefault(ALWAYS) val parameterTypes: List<String> = emptyList())

    @Serializable
    data class Field(val name: String)
}

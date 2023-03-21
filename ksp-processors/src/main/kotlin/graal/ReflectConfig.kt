@file:OptIn(ExperimentalSerializationApi::class)

package dev.kord.ksp.graal

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    encodeDefaults = false
    prettyPrint = true
    prettyPrintIndent = "  "
}

fun ReflectConfig.encode() = json.encodeToString(this)


// Reference: https://www.graalvm.org/latest/reference-manual/native-image/metadata/#specifying-reflection-metadata-in-json
@Serializable
@JvmInline
value class ReflectConfig(val entries: List<ReflectConfigEntry>) : List<ReflectConfigEntry> by entries

@Serializable
data class ReflectConfigEntry(
    val name: String,
    val condition: Condition? = null,
    val queryAllDeclaredConstructors: Boolean = false,
    val queryAllPublicConstructors: Boolean = false,
    val queryAllDeclaredMethods: Boolean = false,
    val queryAllPublicMethods: Boolean = false,
    val allDeclaredClasses: Boolean = false,
    val allPublicClasses: Boolean = false,
    val fields: List<Field> = emptyList(),
    val methods: List<Method> = emptyList(),
    val queriedMethods: List<Method> = emptyList(),
) {
    @Serializable
    data class Field(val name: String)

    @Serializable
    data class Method(
        val name: String,
        @EncodeDefault val parameterTypes: List<String> = emptyList(),
    )

    @Serializable
    data class Condition(val typeReachable: String)
}

package dev.kord.ksp.graal

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    encodeDefaults = false
}

fun ReflectConfig.encode() = json.encodeToString(this)


// Reference: https://www.graalvm.org/22.0/reference-manual/native-image/Reflection/#manual-configuration
@Serializable
@JvmInline
value class ReflectConfig(val entries: List<ReflectConfigEntry>) : List<ReflectConfigEntry> by entries

@Serializable
data class ReflectConfigEntry(
    val name: String,
    val queryAllDeclaredConstructors: Boolean = false,
    val queryAllPublicConstructors: Boolean = false,
    val queryAllDeclaredMethods: Boolean = false,
    val queryAllPublicMethods: Boolean = false,
    val allDeclaredClasses: Boolean = false,
    val allPublicClasses: Boolean = false,
    val fields: List<Field> = emptyList(),
    val methods: List<Method> = emptyList(),
    val queriedMethods: List<QueriedMethod> = emptyList()
) {
    @Serializable
    data class Field(val name: String)

    @Serializable
    data class QueriedMethod(val name: String)

    @Serializable
    data class Method(
        val name: String,
        @EncodeDefault val parameterTypes: List<String> = emptyList(),
    )
}

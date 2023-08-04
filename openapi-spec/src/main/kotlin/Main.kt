import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*

private class ListOrSingleSerializer<T>(elementSerializer: KSerializer<T>) : JsonTransformingSerializer<List<T>>(
    tSerializer = ListSerializer(elementSerializer),
) {
    override fun transformDeserialize(element: JsonElement): JsonElement =
        if (element is JsonArray) element else JsonArray(listOf(element))
}

@Serializable
private data class Schema(
    val title: String? = null,
    val description: String? = null,
    @SerialName("\$ref") val ref: String? = null,
    val type: @Serializable(with = ListOrSingleSerializer::class) List<String>? = null,
    val enum: List<JsonPrimitive>? = null,
    val const: JsonPrimitive? = null,
    val properties: Map<String, Schema>? = null,
    val additionalProperties: JsonElement? = null, // TODO Schema or Boolean
    val maxProperties: Int? = null,
    val items: Schema? = null,
    val minItems: Int? = null,
    val maxItems: Int? = null,
    val uniqueItems: Boolean? = null,
    val oneOf: List<Schema>? = null,
    val anyOf: List<Schema>? = null,
    val allOf: List<Schema>? = null,
    @SerialName("x-discord-union") val xDiscordUnion: String? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val minimum: Long? = null,
    val maximum: Long? = null,
    val format: String? = null,
    val required: List<String>? = null,
    val contentEncoding: String? = null,
    val pattern: String? = null,
)

@Serializable
private data class Spec(
    val openapi: String,
    val info: Info,
    val externalDocs: ExternalDocs,
    val servers: List<Server>,
    val paths: Map<String, Path>,
    val components: Components,
) {
    @Serializable
    data class Info(
        val title: String,
        val description: String,
        val termsOfService: String,
        val license: License,
        val version: String,
    ) {
        @Serializable
        data class License(val name: String, val identifier: String)
    }

    @Serializable
    data class ExternalDocs(val url: String, val description: String)

    @Serializable
    data class Server(val url: String)

    @Serializable
    data class Path(
        val get: Operation? = null,
        val post: Operation? = null,
        val put: Operation? = null,
        val delete: Operation? = null,
        val patch: Operation? = null,
        val parameters: List<Parameter>? = null,
    ) {
        @Serializable
        data class Operation(
            val operationId: String,
            val parameters: List<Parameter>? = null,
            val requestBody: RequestBody? = null,
            val responses: Map<String, Response>,
            val security: List<Security>,
        ) {
            @Serializable
            data class RequestBody(val content: Content, val required: Boolean)

            @Serializable
            data class Response(
                val description: String? = null,
                val content: Content? = null,
                @SerialName("\$ref") val ref: String? = null,
            )

            @Serializable
            data class Content(
                @SerialName("application/json") val json: Variant? = null,
                @SerialName("application/x-www-form-urlencoded") val formUrlencoded: Variant? = null,
                @SerialName("multipart/form-data") val formData: Variant? = null,
                @SerialName("image/png") val png: Variant? = null,
            ) {
                @Serializable
                data class Variant(val schema: Schema)
            }

            @Serializable
            data class Security(
                @SerialName("BotToken") val botToken: List<Nothing>? = null,
                @SerialName("OAuth2") val oAuth2: List<String>? = null,
            )
        }

        @Serializable
        data class Parameter(val name: String, val `in`: String, val schema: Schema, val required: Boolean? = null)
    }

    @Serializable
    data class Components(
        val schemas: Map<String, Schema>,
        val securitySchemes: Map<String, SecurityScheme>,
        val responses: Map<String, Path.Operation.Response>,
    ) {
        @Serializable
        data class SecurityScheme(
            val type: String,
            val description: String? = null,
            val name: String? = null,
            val `in`: String? = null,
            val flows: Map<String, Flow>? = null,
        ) {
            @Serializable
            data class Flow(
                val authorizationUrl: String? = null,
                val tokenUrl: String? = null,
                val refreshUrl: String,
                val scopes: Map<String, String>,
            )
        }
    }
}

fun main() {
    val openapi = Spec::class.java.getResourceAsStream("openapi.json")!!.reader().use { it.readText() }
    val openapiPreview = Spec::class.java.getResourceAsStream("openapi_preview.json")!!.reader().use { it.readText() }
    val spec = Json.decodeFromString<Spec>(openapi)
    val specPreview = Json.decodeFromString<Spec>(openapiPreview)
    println(spec)
    println(specPreview)
}

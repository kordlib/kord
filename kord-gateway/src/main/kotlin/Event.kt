import Cache.sequance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

@Serializable
open class Event(
        @SerialName("op")
        val opCode: Int,
        @SerialName("d")
        val data: JsonElement?,
        @SerialName("s")
        val sequence: Int? = null,
        @SerialName("t")
        val name: String? = null)

@Serializable
internal class HeartBeat : Event(10, JsonPrimitive(sequance))

object Cache {
    var sequance: Int? = null
}

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.Serializable

@Serializable
open class Event(
        @SerialName("op")
        open val opCode: Int,
        @SerialName("d")
        open val data: JsonElement?,
        @Optional
        @SerialName("s")
        open val sequence: Int? = null,
        @Optional
        @SerialName("t")
        open val name: String? = null)
package dev.kord.common.serialization

import com.github.jershell.kbson.KBson
import dev.kord.common.entity.Permission.*
import dev.kord.common.entity.Permissions
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class LongOrStringSerializerBsonTest {

    @Serializable
    private data class SomeObject(
        @Serializable(with = LongOrStringSerializer::class) val someLong: String,
        @Serializable(with = LongOrStringSerializer::class) val someString: String,
        val somePermissions: Permissions,
    )

    private val someObject = SomeObject(
        someLong = Random.nextLong().toString(),
        someString = "some totally random string",
        somePermissions = Permissions(DeafenMembers, ManageThreads, SendTTSMessages, ModerateMembers),
    )

    @Test
    fun `test Bson serialization and deserialization with LongOrStringSerializer`() {
        val kBson = KBson.default
        assertEquals(
            expected = someObject,
            actual = kBson.load(SomeObject.serializer(), kBson.stringify(SomeObject.serializer(), someObject)),
        )
        assertEquals(
            expected = someObject,
            actual = kBson.load(SomeObject.serializer(), kBson.dump(SomeObject.serializer(), someObject)),
        )
    }
}

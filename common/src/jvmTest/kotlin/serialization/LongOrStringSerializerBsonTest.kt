package dev.kord.common.serialization

import dev.kord.common.entity.Permission.*
import dev.kord.common.entity.Permissions
import kotlinx.serialization.Serializable
import org.mongodb.kbson.ExperimentalKBsonSerializerApi
import org.mongodb.kbson.serialization.EJson
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

    @OptIn(ExperimentalKBsonSerializerApi::class)
    @Test
    fun `test Bson serialization and deserialization with LongOrStringSerializer`() {
        val kBson = EJson.Default
        assertEquals(
            expected = someObject,
            actual = kBson.decodeFromString(
                SomeObject.serializer(),
                kBson.encodeToString(SomeObject.serializer(), someObject)
            ),
        )
        assertEquals(
            expected = someObject,
            actual = kBson.decodeFromBsonValue(
                SomeObject.serializer(),
                kBson.encodeToBsonValue(SomeObject.serializer(), someObject)
            ),
        )
    }
}
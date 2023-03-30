package dev.kord.common.json

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import kotlin.test.assertEquals

infix fun String?.shouldBe(value: String?) =
    assertEquals(value, this)

infix fun Optional<String?>.shouldBe(value: String?) =
    assertEquals(value, this.value)

infix fun OptionalBoolean.shouldBe(value: Boolean) =
    assertEquals(value, this.value)

infix fun Snowflake?.shouldBe(value: String?) =
    assertEquals(value, this?.toString())

infix fun OptionalSnowflake?.shouldBe(value: String?) =
    assertEquals(value, this?.value?.toString())

infix fun VerificationLevel?.shouldBe(value: VerificationLevel?) =
    assertEquals(value, this)

infix fun DefaultMessageNotificationLevel?.shouldBe(value: DefaultMessageNotificationLevel?) =
    assertEquals(value, this)

infix fun MFALevel?.shouldBe(value: MFALevel?) =
    assertEquals(value, this)

infix fun ExplicitContentFilter?.shouldBe(value: ExplicitContentFilter?) =
    assertEquals(value, this)

infix fun PremiumTier?.shouldBe(value: PremiumTier?) =
    assertEquals(value, this)

infix fun SystemChannelFlags?.shouldBe(value: SystemChannelFlags?) =
    assertEquals(value, this)

infix fun <T> List<T>?.shouldBe(value: List<T>?) =
    assertEquals(value, this)

infix fun Int?.shouldBe(value: Int?) =
    assertEquals(value, this)

infix fun OptionalInt?.shouldBe(value: Int?) =
    assertEquals(value, this.value)

infix fun <T> Optional<T>.shouldBe(that: T?) =
    assertEquals(that, this.value)

infix fun <T> T.shouldBe(that: T) =
    assertEquals(that, this)

package json

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import org.junit.jupiter.api.Assertions

infix fun String?.shouldBe(value: String?){
    Assertions.assertEquals(value, this)
}

infix fun Optional<String?>.shouldBe(value: String?){
    Assertions.assertEquals(value, this.value)
}

infix fun OptionalBoolean.shouldBe(value: Boolean){
    Assertions.assertEquals(value, this.value)
}

infix fun Snowflake?.shouldBe(value: String?){
    Assertions.assertEquals(value, this?.toString())
}

infix fun OptionalSnowflake?.shouldBe(value: String?){
    Assertions.assertEquals(value, this?.value?.toString())
}

infix fun VerificationLevel?.shouldBe(value: VerificationLevel?){
    Assertions.assertEquals(value, this)
}

infix fun DefaultMessageNotificationLevel?.shouldBe(value: DefaultMessageNotificationLevel?){
    Assertions.assertEquals(value, this)
}

infix fun MFALevel?.shouldBe(value: MFALevel?){
    Assertions.assertEquals(value, this)
}

infix fun ExplicitContentFilter?.shouldBe(value: ExplicitContentFilter?){
    Assertions.assertEquals(value, this)
}

infix fun PremiumTier?.shouldBe(value: PremiumTier?){
    Assertions.assertEquals(value, this)
}

infix fun SystemChannelFlags?.shouldBe(value: SystemChannelFlags?){
    Assertions.assertEquals(value, this)
}

infix fun<T> List<T>?.shouldBe(value: List<T>?){
    Assertions.assertEquals(value, this)
}

infix fun Int?.shouldBe(value: Int?){
    Assertions.assertEquals(value, this)
}

infix fun OptionalInt?.shouldBe(value: Int?){
    Assertions.assertEquals(value, this.value)
}

infix fun <T> Optional<T>.shouldBe(that: T?) {
    Assertions.assertEquals(that, this.value)
}

infix fun <T> T.shouldBe(that: T) {
    Assertions.assertEquals(that, this)
}

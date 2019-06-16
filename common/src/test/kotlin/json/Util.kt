package json

import org.junit.jupiter.api.Assertions

infix fun <T> T.shouldBe(that: T) {
    Assertions.assertEquals(that, this)
}
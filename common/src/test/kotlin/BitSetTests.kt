import com.gitlab.kordlib.common.BitSet
import com.gitlab.kordlib.common.bitSetOf
import org.junit.jupiter.api.Test

class BitSetTests {
    @Test
    fun `or`() {
        val zero = bitSetOf(0)
        val one = bitSetOf(1)
        one.or(zero)
        assert(one == bitSetOf(0b1))

    }
    @Test
    fun `and`() {
        val zero = bitSetOf(0)
        val one = bitSetOf(1)
        one.and(zero)
        assert(one == BitSet(0b0))

    }
    @Test
    fun `andNot`() {
        val zero = bitSetOf(0)
        val one = bitSetOf(1)
        one.andNot(zero)
        assert(one == BitSet(0b1))

    }

    @Test
    fun `xor`() {
        val zero = bitSetOf(0)
        val one = bitSetOf(1)
        one.xor(zero)
        assert(one == bitSetOf(0b1))
    }

    @Test
    fun `flip`() {
        val one = bitSetOf(1)
        one.flip()
        assert(one == BitSet(0b0))
    }


    @Test
    fun `empty set`() {
        val zero = bitSetOf(0,0,0,0)
        assert(zero.isEmpty)
    }

    @Test
    fun `set a bit`() {
        val zero = bitSetOf(0)
        zero[0] = true
        assert(zero == BitSet(0b1))
    }
    @Test
    fun `operate with longer set`() {
        val first = bitSetOf(0,0,0,0)
        val second = bitSetOf(0)
        assert(first == second)
        first[3] = 1000
        first.and(second)
        assert(first == bitSetOf(0))
    }

    @Test
    fun `not equal sets`() {
        val a = bitSetOf(2)
        val b = bitSetOf(3)
        assert(a != b)
    }
    @Test
    fun `contains`() {
        val a = bitSetOf(0b111,0)
        val b = bitSetOf(0b001)
        assert(b in a)
    }

    @Test
    fun `contains nothing`() {
        val a = bitSetOf(0b111,0)
        val b = bitSetOf(0b0)
        assert(b in a)
    }

}
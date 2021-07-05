@file:Suppress("ObjectPropertyName", "FunctionName")
package me.uport.knacl

import java.security.SecureRandom
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor
import kotlin.math.floor
import kotlin.math.roundToLong

/**
 * This is a port of the TweetNaCl library
 * Ported from the original C by Mircea Nistor
 *
 * **DISCLAIMER:
 * This port is not complete and has not gone through a complete audit.
 * Use at your own risk.**
 */
@Suppress("unused")
internal object NaClLowLevel {

    private val _0: ByteArray = ByteArray(16) { 0 }

    private val _9: ByteArray = ByteArray(32).apply { this[0] = 9 }

    private val gf0: LongArray = LongArray(16) { 0 }
    private val gf1: LongArray = LongArray(16).apply { this[0] = 1 }
    private val _121665: LongArray = longArrayOf(0xDB41, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private val D: LongArray = longArrayOf(
        0x78a3, 0x1359, 0x4dca, 0x75eb, 0xd8ab, 0x4141, 0x0a4d, 0x0070,
        0xe898, 0x7779, 0x4079, 0x8cc7, 0xfe73, 0x2b6f, 0x6cee, 0x5203)
    private val D2: LongArray = longArrayOf(
        0xf159, 0x26b2, 0x9b94, 0xebd6, 0xb156, 0x8283, 0x149a, 0x00e0,
        0xd130, 0xeef3, 0x80f2, 0x198e, 0xfce7, 0x56df, 0xd9dc, 0x2406)
    private val X: LongArray = longArrayOf(
        0xd51a, 0x8f25, 0x2d60, 0xc956, 0xa7b2, 0x9525, 0xc760, 0x692c,
        0xdc5c, 0xfdd6, 0xe231, 0xc0a4, 0x53fe, 0xcd6e, 0x36d3, 0x2169)
    private val Y: LongArray = longArrayOf(
        0x6658, 0x6666, 0x6666, 0x6666, 0x6666, 0x6666, 0x6666, 0x6666,
        0x6666, 0x6666, 0x6666, 0x6666, 0x6666, 0x6666, 0x6666, 0x6666)
    private val I: LongArray = longArrayOf(
        0xa0b0, 0x4a0e, 0x1b27, 0xc4ee, 0xe478, 0xad2f, 0x1806, 0x2f43,
        0xd7a7, 0x3dfb, 0x0099, 0x2b4d, 0xdf0b, 0x4fc1, 0x2480, 0x2b83)
    private fun L32(x: Int, c: Int): Int = ((x shl c) or (x ushr (32 - c)))

    //FIXME: this should be set by a higher level API
    fun randombytes(size: Int): ByteArray {
        val arr = ByteArray(size)
        randombytes(arr, size)
        return arr
    }

    private fun randombytes(x: ByteArray, size: Int) {
        require(x.size >= size) { "array must be of size>=`$size` but it is of size=${x.size}" }
        SecureRandom().nextBytes(x)
    }

    private fun ld32(x: ByteArray, off: Int = 0): Int {
        var u: Int = x[off + 3].toInt() and 0xff
        u = u shl 8 or (x[off + 2].toInt() and 0xff)
        u = u shl 8 or (x[off + 1].toInt() and 0xff)
        return u shl 8 or (x[off + 0].toInt() and 0xff)
    }

    private fun dl64(x: ByteArray, xi: Int): Long {
        require(x.size >= 8 + xi) { "array must have at least 8 elements for `Byte`s to `Long` conversion" }
        var u: Long = 0
        for (i in 0 until 8) {
            u = (u shl 8) or x[i + xi].toLong()
        }
        return u
    }

    private fun st32(x: ByteArray, off: Int = 0, u: Int) {
        require(x.size >= 4 + off) { "`x` output array is too small to fit 4 bytes starting from $off" }
        var uu = u
        for (i in 0 until 4) {
            x[i + off] = uu.toByte()
            uu = uu shr 8
        }
    }

    private fun ts64(x: ByteArray, xi: Int = 0, u: Long) {
        var uu = u
        for (i in 7 downTo 0) {
            x[i + xi] = (uu and 0xff).toByte()
            uu = uu shr 8
        }
    }

    private fun vn(x: ByteArray, xi: Int = 0, y: ByteArray, yi: Int, n: Int): Int {
        var d = 0
        for (i in 0 until n) {
            d = d or (0xff and (x[i + xi] xor y[i + yi]).toInt())
        }
        return ((1 and ((d - 1) shr 8)) - 1)
    }

    private fun crypto_verify_16(x: ByteArray, xi: Int = 0, y: ByteArray, yi: Int = 0): Int {
        return vn(x, xi, y, yi, 16)
    }

    private fun crypto_verify_32(x: ByteArray, xi: Int = 0, y: ByteArray, yi: Int = 0): Int {
        return vn(x, xi, y, yi, 32)
    }

    private fun core(outArr: ByteArray, inArr: ByteArray, k: ByteArray, c: ByteArray, h: Int) {
        val w = IntArray(16)
        val x = IntArray(16)
        val y = IntArray(16)
        val t = IntArray(4)

        for (i in 0 until 4) {
            x[5 * i] = ld32(c, 4 * i)
            x[1 + i] = ld32(k, 4 * i)
            x[6 + i] = ld32(inArr, 4 * i)
            x[11 + i] = ld32(k, 16 + 4 * i)
        }

        for (i in 0 until 16) {
            y[i] = x[i]
        }

        for (i in 0 until 20) {
            for (j in 0 until 4) {
                for (m in 0 until 4) {
                    t[m] = x[(5 * j + 4 * m) % 16]
                }
                t[1] = t[1] xor L32(t[0] + t[3], 7)
                t[2] = t[2] xor L32(t[1] + t[0], 9)
                t[3] = t[3] xor L32(t[2] + t[1], 13)
                t[0] = t[0] xor L32(t[3] + t[2], 18)
                for (m in 0 until 4) {
                    w[4 * j + (j + m) % 4] = t[m]
                }
            }
            for (m in 0 until 16) {
                x[m] = w[m]
            }
        }

        if (h != 0) {
            for (i in 0 until 16) {
                x[i] += y[i]
            }
            for (i in 0 until 4) {
                x[5 * i] -= ld32(c, 4 * i)
                x[6 + i] -= ld32(inArr, 4 * i)
            }
            for (i in 0 until 4) {
                st32(outArr, 4 * i, x[5 * i])
                st32(outArr, 16 + 4 * i, x[6 + i])
            }
        } else {
            for (i in 0 until 16) {
                st32(outArr, 4 * i, x[i] + y[i])
            }
        }
    }

    fun crypto_core_salsa20(outArr: ByteArray, inArr: ByteArray, k: ByteArray, c: ByteArray): Int {
        core(outArr, inArr, k, c, 0)
        return 0
    }

    fun crypto_core_hsalsa20(outArr: ByteArray, inArr: ByteArray, k: ByteArray, c: ByteArray): Int {
        core(outArr, inArr, k, c, 1)
        return 0
    }

    private val sigma: ByteArray = "expand 32-byte k".toByteArray(Charsets.UTF_8)

    fun crypto_stream_salsa20_xor(c: ByteArray, m: ByteArray?, bIn: Long, n: ByteArray, nOff: Int = 0, k: ByteArray): Int {
        val z = ByteArray(16) { 0 }
        val x = ByteArray(64)
        var u: Int
        if (bIn == 0L) return 0
        for (i in 0 until 8) {
            z[i] = n[i + nOff]
        }
        var b = bIn
        var cOff = 0
        var mOff = 0
        while (b >= 64) {
            crypto_core_salsa20(x, z, k, sigma)
            for (i in 0 until 64) {
                c[cOff + i] = (if (m != null) m[mOff + i] else 0) xor x[i]
            }
            u = 1
            for (i in 8 until 16) {
                u += 0xff and z[i].toInt()
                z[i] = u.toByte()
                u = u shr 8
            }
            b -= 64
            cOff += 64
            if (m != null) mOff += 64
        }
        if (b != 0L) {
            crypto_core_salsa20(x, z, k, sigma)
            for (i in 0 until b.toInt()) {
                c[cOff + i] = (if (m != null) m[mOff + i] else 0) xor x[i]
            }
        }
        return 0
    }

    fun crypto_stream_salsa20(c: ByteArray, d: Long, n: ByteArray, k: ByteArray, nStart: Int = 0): Int {
        return crypto_stream_salsa20_xor(c, null, d, n, nStart, k)
    }

    fun crypto_stream(c: ByteArray, d: Long, n: ByteArray, k: ByteArray): Int {
        val s = ByteArray(32)
        crypto_core_hsalsa20(s, n, k, sigma)
        return crypto_stream_salsa20(c, d, n, s, 16)
    }

    fun crypto_stream_xor(c: ByteArray, m: ByteArray, d: Long, n: ByteArray, k: ByteArray): Int {
        val s = ByteArray(32)
        crypto_core_hsalsa20(s, n, k, sigma)
        return crypto_stream_salsa20_xor(c, m, d, n, 16, s)
    }

    private fun add1305(h: IntArray, c: IntArray) {
        var u = 0
        for (j in 0 until 17) {
            u += h[j] + c[j]
            h[j] = u and 255
            u = u shr 8
        }
    }

    private val minusp: IntArray = intArrayOf(5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 252)

    fun crypto_onetimeauth(out: ByteArray, outStart: Int, m: ByteArray, mStart: Int, n: Long, k: ByteArray): Int {
        var mpos = mStart
        var nn = n
        val x = IntArray(17)
        val r = IntArray(17)
        val h = IntArray(17)
        val c = IntArray(17)
        val g = IntArray(17)

        for (j in 0 until 17) {
            r[j] = 0; h[j] = 0
        }
        for (j in 0 until 16) {
            r[j] = (0xff and k[j].toInt())
        }
        r[3] = r[3] and 15
        r[4] = r[4] and 252
        r[7] = r[7] and 15
        r[8] = r[8] and 252
        r[11] = r[11] and 15
        r[12] = r[12] and 252
        r[15] = r[15] and 15

        while (nn > 0) {
            for (j in 0 until 17) {
                c[j] = 0
            }
            var jj = 0
            while (jj < 16 && jj < nn.toInt()) {
                c[jj] = 0xff and m[mpos + jj].toInt()
                jj++
            }
            c[jj] = 1
            mpos += jj
            nn -= jj
            add1305(h, c)
            for (i in 0 until 17) {
                x[i] = 0
                for (j in 0 until 17) {
                    x[i] += h[j] * if (j <= i) r[i - j] else (320 * r[i + 17 - j])
                }
            }
            for (i in 0 until 17) {
                h[i] = x[i]
            }
            var u = 0
            for (j in 0 until 16) {
                u += h[j]
                h[j] = u and 255
                u = u shr 8
            }
            u += h[16]
            h[16] = u and 3
            u = (5 * (u shr 2))
            for (j in 0 until 16) {
                u += h[j]
                h[j] = u and 255
                u = u shr 8
            }
            u += h[16]
            h[16] = u
        }
        for (j in 0 until 17) {
            g[j] = h[j]
        }
        add1305(h, minusp)
        val s = 0 - (h[16] shr 7)
        for (j in 0 until 17) {
            h[j] = h[j] xor (s and (g[j] xor h[j]))
        }

        for (j in 0 until 16) {
            c[j] = 0xff and k[j + 16].toInt()
        }
        c[16] = 0
        add1305(h, c)
        for (j in 0 until 16) out[outStart + j] = h[j].toByte()
        return 0
    }

    private fun crypto_onetimeauth_verify(h: ByteArray, hi: Int, m: ByteArray, mi: Int, n: Long, k: ByteArray): Int {
        val x = ByteArray(16)
        crypto_onetimeauth(x, 0, m, mi, n, k)
        return crypto_verify_16(h, hi, x, 0)
    }

    fun crypto_secretbox(c: ByteArray, m: ByteArray, d: Long, n: ByteArray, k: ByteArray): Int {
        if (d < 32) return -1
        crypto_stream_xor(c, m, d, n, k)
        crypto_onetimeauth(c, 16, c, 32, d - 32, c)
        for (i in 0 until 16) c[i] = 0
        return 0
    }

    fun crypto_secretbox_open(m: ByteArray, c: ByteArray, d: Long, n: ByteArray, k: ByteArray): Int {
        val x = ByteArray(32)
        if (d < 32) return -1
        crypto_stream(x, 32, n, k)
        if (crypto_onetimeauth_verify(c, 16, c, 32, d - 32, x) != 0) return -1
        crypto_stream_xor(m, c, d, n, k)
        for (i in 0 until 32) m[i] = 0
        return 0
    }

///////////////////////////////////////////////////////////////////
// curve 25519
///////////////////////////////////////////////////////////////////

    private fun set25519(r: LongArray, a: LongArray) {
        for (i in 0 until 16) r[i] = a[i]
    }

    private fun car25519(/*gf*/ o: LongArray, oOff: Int = 0) {
        for (i in 0 until 16) {
            o[oOff + i] += (1 shl 16).toLong()
            val c = o[oOff + i] shr 16
            o[oOff + (i + 1) * (if (i < 15) 1 else 0)] += c - 1 + 37 * (c - 1) * (if (i == 15) 1 else 0).toLong()
            o[oOff + i] -= c shl 16
        }
    }

    private fun sel25519(p: LongArray, q: LongArray, b: Int) {
        var t: Long
        val c = (b - 1).inv().toLong()
        for (i in 0 until 16) {
            t = c and (p[i] xor q[i])
            p[i] = p[i] xor t
            q[i] = q[i] xor t
        }
    }

    private fun pack25519(o: ByteArray, n: LongArray, nOff: Int = 0) {
        var b: Int
        val m = LongArray(16)
        val t = LongArray(16)
        for (i in 0 until 16) t[i] = n[i + nOff]
        car25519(t)
        car25519(t)
        car25519(t)
        for (j in 0 until 2) {
            m[0] = t[0] - 0xffed
            for (i in 1 until 15) {
                m[i] = t[i] - 0xffff - ((m[i - 1] shr 16) and 1)
                m[i - 1] = m[i - 1] and 0xffff
            }
            m[15] = t[15] - 0x7fff - ((m[14] shr 16) and 1)
            b = ((m[15] shr 16) and 1).toInt()
            m[14] = m[14] and 0xffff
            sel25519(t, m, 1 - b)
        }
        for (i in 0 until 16) {
            o[2 * i] = t[i].toByte()
            o[2 * i + 1] = (t[i] shr 8).toByte()
        }
    }

    private fun neq25519(a: LongArray, b: LongArray): Int {
        val c = ByteArray(32)
        val d = ByteArray(32)
        pack25519(c, a)
        pack25519(d, b)
        return crypto_verify_32(c, 0, d, 0)
    }

    private fun par25519(a: LongArray): Byte {
        val d = ByteArray(32)
        pack25519(d, a)
        return (d[0] and 1)
    }

    private fun unpack25519(o: LongArray, n: ByteArray) {
        for (i in 0 until 16) {
            o[i] = (0xff and n[2 * i].toInt()) + (0xffL and n[2 * i + 1].toLong() shl 8)
        }
        o[15] = o[15] and 0x7fff
    }

    private fun A(o: LongArray, a: LongArray, b: LongArray) {
        for (i in 0 until 16) o[i] = a[i] + b[i]
    }

    private fun Z(o: LongArray, a: LongArray, b: LongArray) {
        for (i in 0 until 16) o[i] = a[i] - b[i]
    }

    private fun M(o: LongArray, a: LongArray, b: LongArray) {
        val t = LongArray(31)

        for (i in 0 until 16) {
            for (j in 0 until 16) {
                t[i + j] += a[i] * b[j]
            }
        }
        for (i in 0 until 15) {
            t[i] += 38 * t[i + 16]
        }
        for (i in 0 until 16) {
            o[i] = t[i]
        }
        car25519(o)
        car25519(o)
    }

    private fun S(o: LongArray, a: LongArray) = M(o, a, a)

    private fun inv25519(o: LongArray, i: LongArray) {
        val c = LongArray(16)
        for (a in 0 until 16) c[a] = i[a]
        for (a in 253 downTo 0) {
            S(c, c)
            if (a != 2 && a != 4) M(c, c, i)
        }
        for (a in 0 until 16) o[a] = c[a]
    }

    private fun pow2523(o: LongArray, i: LongArray) {
        val c = LongArray(16)
        for (a in 0 until 16) c[a] = i[a]
        for (a in 250 downTo 0) {
            S(c, c)
            if (a != 1) M(c, c, i)
        }
        for (a in 0 until 16) o[a] = c[a]
    }

    fun crypto_scalarmult(q: ByteArray, n: ByteArray, p: ByteArray): Int {
        val z = ByteArray(32)
        val x = LongArray(80)
        var r: Int
        val a = LongArray(16)
        val b = LongArray(16)
        val c = LongArray(16)
        val d = LongArray(16)
        val e = LongArray(16)
        val f = LongArray(16)
        for (i in 0 until 31) {
            z[i] = n[i]
        }
        z[31] = (n[31] and 127) or 64
        z[0] = z[0] and 248.toByte()
        unpack25519(x, p)
        for (i in 0 until 16) {
            b[i] = x[i]
            d[i] = 0
            a[i] = 0
            c[i] = 0
        }
        a[0] = 1
        d[0] = 1
        for (i in 254 downTo 0) {
            r = ((z[i shr 3].toLong() shr (i and 7)) and 1).toInt()
            sel25519(a, b, r)
            sel25519(c, d, r)
            A(e, a, c)
            Z(a, a, c)
            A(c, b, d)
            Z(b, b, d)
            S(d, e)
            S(f, a)
            M(a, c, a)
            M(c, b, e)
            A(e, a, c)
            Z(a, a, c)
            S(b, a)
            Z(c, d, f)
            M(a, c, _121665)
            A(a, a, d)
            M(c, c, a)
            M(a, d, f)
            M(d, b, x)
            S(b, e)
            sel25519(a, b, r)
            sel25519(c, d, r)
        }
        for (i in 0 until 16) {
            x[i + 16] = a[i]
            x[i + 32] = c[i]
            x[i + 48] = b[i]
            x[i + 64] = d[i]
        }

        val x32 = x.copyOfRange(32, x.size)
        val x16 = x.copyOfRange(16, x.size)
        inv25519(x32, x32)
        M(x16, x16, x32)
        pack25519(q, x16)

        return 0
    }

    fun crypto_scalarmult_base(q: ByteArray, n: ByteArray): Int {
        return crypto_scalarmult(q, n, _9)
    }

    fun crypto_box_keypair(y: ByteArray, x: ByteArray): Int {
        randombytes(x, 32)
        return crypto_scalarmult_base(y, x)
    }

    fun crypto_box_beforenm(k: ByteArray, y: ByteArray, x: ByteArray): Int {
        val s = ByteArray(32)
        crypto_scalarmult(s, x, y)
        return crypto_core_hsalsa20(k, _0, s, sigma)
    }

    private fun crypto_box_afternm(c: ByteArray, m: ByteArray, d: Long, n: ByteArray, k: ByteArray): Int {
        return crypto_secretbox(c, m, d, n, k)
    }

    private fun crypto_box_open_afternm(m: ByteArray, c: ByteArray, d: Long, n: ByteArray, k: ByteArray): Int {
        return crypto_secretbox_open(m, c, d, n, k)
    }

    fun crypto_box(c: ByteArray, m: ByteArray, d: Long, n: ByteArray, y: ByteArray, x: ByteArray): Int {
        val k = ByteArray(32)
        crypto_box_beforenm(k, y, x)
        return crypto_box_afternm(c, m, d, n, k)
    }

    fun crypto_box_open(m: ByteArray, c: ByteArray, d: Long, n: ByteArray, y: ByteArray, x: ByteArray): Int {
        val k = ByteArray(32)
        crypto_box_beforenm(k, y, x)
        return crypto_box_open_afternm(m, c, d, n, k)
    }


    /////////////////////////////////////////////////////
    // hash
    /////////////////////////////////////////////////////


    private fun R(x: Long, c: Int): Long = ((x shr c) or (x shl (64 - c)))

    private fun Ch(x: Long, y: Long, z: Long): Long = (x and y) xor (x.inv() and z)

    private fun Maj(x: Long, y: Long, z: Long): Long = ((x and y) xor (x xor z) xor (y and z))

    private fun Sigma0(x: Long): Long = (R(x, 28) xor R(x, 34) xor R(x, 39))

    private fun Sigma1(x: Long): Long = (R(x, 14) xor R(x, 18) xor R(x, 41))

    private fun sigma0(x: Long): Long = (R(x, 1) xor R(x, 8) xor (x shr 7))

    private fun sigma1(x: Long): Long = (R(x, 19) xor R(x, 61) xor (x shr 6))

    //    private val K = ulongArrayOf(
//            0x428a2f98d728ae22UL, 0x7137449123ef65cdUL, 0xb5c0fbcfec4d3b2fUL, 0xe9b5dba58189dbbcUL,
//            0x3956c25bf348b538UL, 0x59f111f1b605d019UL, 0x923f82a4af194f9bUL, 0xab1c5ed5da6d8118UL,
//            0xd807aa98a3030242UL, 0x12835b0145706fbeUL, 0x243185be4ee4b28cUL, 0x550c7dc3d5ffb4e2UL,
//            0x72be5d74f27b896fUL, 0x80deb1fe3b1696b1UL, 0x9bdc06a725c71235UL, 0xc19bf174cf692694UL,
//            0xe49b69c19ef14ad2UL, 0xefbe4786384f25e3UL, 0x0fc19dc68b8cd5b5UL, 0x240ca1cc77ac9c65UL,
//            0x2de92c6f592b0275UL, 0x4a7484aa6ea6e483UL, 0x5cb0a9dcbd41fbd4UL, 0x76f988da831153b5UL,
//            0x983e5152ee66dfabUL, 0xa831c66d2db43210UL, 0xb00327c898fb213fUL, 0xbf597fc7beef0ee4UL,
//            0xc6e00bf33da88fc2UL, 0xd5a79147930aa725UL, 0x06ca6351e003826fUL, 0x142929670a0e6e70UL,
//            0x27b70a8546d22ffcUL, 0x2e1b21385c26c926UL, 0x4d2c6dfc5ac42aedUL, 0x53380d139d95b3dfUL,
//            0x650a73548baf63deUL, 0x766a0abb3c77b2a8UL, 0x81c2c92e47edaee6UL, 0x92722c851482353bUL,
//            0xa2bfe8a14cf10364UL, 0xa81a664bbc423001UL, 0xc24b8b70d0f89791UL, 0xc76c51a30654be30UL,
//            0xd192e819d6ef5218UL, 0xd69906245565a910UL, 0xf40e35855771202aUL, 0x106aa07032bbd1b8UL,
//            0x19a4c116b8d2d0c8UL, 0x1e376c085141ab53UL, 0x2748774cdf8eeb99UL, 0x34b0bcb5e19b48a8UL,
//            0x391c0cb3c5c95a63UL, 0x4ed8aa4ae3418acbUL, 0x5b9cca4f7763e373UL, 0x682e6ff3d6b2b8a3UL,
//            0x748f82ee5defb2fcUL, 0x78a5636f43172f60UL, 0x84c87814a1f0ab72UL, 0x8cc702081a6439ecUL,
//            0x90befffa23631e28UL, 0xa4506cebde82bde9UL, 0xbef9a3f7b2c67915UL, 0xc67178f2e372532bUL,
//            0xca273eceea26619cUL, 0xd186b8c721c0c207UL, 0xeada7dd6cde0eb1eUL, 0xf57d4f7fee6ed178UL,
//            0x06f067aa72176fbaUL, 0x0a637dc5a2c898a6UL, 0x113f9804bef90daeUL, 0x1b710b35131c471bUL,
//            0x28db77f523047d84UL, 0x32caab7b40c72493UL, 0x3c9ebe0a15c9bebcUL, 0x431d67c49c100d4cUL,
//            0x4cc5d4becb3e42b6UL, 0x597f299cfc657e2aUL, 0x5fcb6fab3ad6faecUL, 0x6c44198c4a475817UL
//    ).asLongArray()
    private val K = longArrayOf(
        4794697086780616226, 8158064640168781261, -5349999486874862801, -1606136188198331460,
        4131703408338449720, 6480981068601479193, -7908458776815382629, -6116909921290321640,
        -2880145864133508542, 1334009975649890238, 2608012711638119052, 6128411473006802146,
        8268148722764581231, -9160688886553864527, -7215885187991268811, -4495734319001033068,
        -1973867731355612462, -1171420211273849373, 1135362057144423861, 2597628984639134821,
        3308224258029322869, 5365058923640841347, 6679025012923562964, 8573033837759648693,
        -7476448914759557205, -6327057829258317296, -5763719355590565569, -4658551843659510044,
        -4116276920077217854, -3051310485924567259, 489312712824947311, 1452737877330783856,
        2861767655752347644, 3322285676063803686, 5560940570517711597, 5996557281743188959,
        7280758554555802590, 8532644243296465576, -9096487096722542874, -7894198246740708037,
        -6719396339535248540, -6333637450476146687, -4446306890439682159, -4076793802049405392,
        -3345356375505022440, -2983346525034927856, -860691631967231958, 1182934255886127544,
        1847814050463011016, 2177327727835720531, 2830643537854262169, 3796741975233480872,
        4115178125766777443, 5681478168544905931, 6601373596472566643, 7507060721942968483,
        8399075790359081724, 8693463985226723168, -8878714635349349518, -8302665154208450068,
        -8016688836872298968, -6606660893046293015, -4685533653050689259, -4147400797238176981,
        -3880063495543823972, -3348786107499101689, -1523767162380948706, -757361751448694408,
        500013540394364858, 748580250866718886, 1242879168328830382, 1977374033974150939,
        2944078676154940804, 3659926193048069267, 4368137639120453308, 4836135668995329356,
        5532061633213252278, 6448918945643986474, 6902733635092675308, 7801388544844847127
    )

    private fun crypto_hashblocks(x: ByteArray, m: ByteArray, n: Long): Int {
        val z = LongArray(8)
        val b = LongArray(8)
        val a = LongArray(8)
        val w = LongArray(16)

        var t: Long

        for (i in 0 until 8) {
            z[i] = dl64(x, 8 * i)
            a[i] = dl64(x, 8 * i)
        }
        var nn = n
        var mi = 0

        while (nn >= 128) {
            for (i in 0 until 16) {
                w[i] = dl64(m, mi + 8 * i)
            }

            for (i in 0 until 80) {
                for (j in 0 until 8) {
                    b[j] = a[j]
                }
                t = a[7] + Sigma1(a[4]) + Ch(a[4], a[5], a[6]) + K[i] + w[i % 16]
                b[7] = t + Sigma0(a[0]) + Maj(a[0], a[1], a[2])
                b[3] += t
                for (j in 0 until 8) {
                    a[(j + 1) % 8] = b[j]
                }
                if (i % 16 == 15) {
                    for (j in 0 until 16) {
                        w[j] += w[(j + 9) % 16] + sigma0(w[(j + 1) % 16]) + sigma1(w[(j + 14) % 16])
                    }
                }
            }


            for (i in 0 until 8) {
                a[i] += z[i]; z[i] = a[i]; }

            mi += 128
            nn -= 128
        }

        for (i in 0 until 8) ts64(x, 8 * i, z[i])

        return n.toInt()
    }

    private val iv = byteArrayOf(
        0x6a.toByte(), 0x09.toByte(), 0xe6.toByte(), 0x67.toByte(), 0xf3.toByte(), 0xbc.toByte(), 0xc9.toByte(), 0x08.toByte(),
        0xbb.toByte(), 0x67.toByte(), 0xae.toByte(), 0x85.toByte(), 0x84.toByte(), 0xca.toByte(), 0xa7.toByte(), 0x3b.toByte(),
        0x3c.toByte(), 0x6e.toByte(), 0xf3.toByte(), 0x72.toByte(), 0xfe.toByte(), 0x94.toByte(), 0xf8.toByte(), 0x2b.toByte(),
        0xa5.toByte(), 0x4f.toByte(), 0xf5.toByte(), 0x3a.toByte(), 0x5f.toByte(), 0x1d.toByte(), 0x36.toByte(), 0xf1.toByte(),
        0x51.toByte(), 0x0e.toByte(), 0x52.toByte(), 0x7f.toByte(), 0xad.toByte(), 0xe6.toByte(), 0x82.toByte(), 0xd1.toByte(),
        0x9b.toByte(), 0x05.toByte(), 0x68.toByte(), 0x8c.toByte(), 0x2b.toByte(), 0x3e.toByte(), 0x6c.toByte(), 0x1f.toByte(),
        0x1f.toByte(), 0x83.toByte(), 0xd9.toByte(), 0xab.toByte(), 0xfb.toByte(), 0x41.toByte(), 0xbd.toByte(), 0x6b.toByte(),
        0x5b.toByte(), 0xe0.toByte(), 0xcd.toByte(), 0x19.toByte(), 0x13.toByte(), 0x7e.toByte(), 0x21.toByte(), 0x79.toByte()
    )

    private fun crypto_hash(outArr: ByteArray, m: ByteArray, n: Long): Int {
        require(outArr.size >= 64) { "outArr size(${outArr.size}) needs to be at least 64" }
        val h = iv.copyOf()
        val x = ByteArray(256)
        val b: Long = n

        crypto_hashblocks(h, m, n)

        var mi = 0
        var nn = n.toInt()

        mi += nn
        nn = nn and 127
        mi -= nn

        for (i in 0 until nn) {
            x[i] = m[i + mi]
        }
        x[nn] = 128.toByte()

        nn = 256 - 128 * (if (nn < 112) 1 else 0)
        x[nn - 9] = ((b shr 61) and 0xff).toByte()
        ts64(x, nn - 8, b shl 3)
        crypto_hashblocks(h, x, nn.toLong())

        for (i in 0 until 64) outArr[i] = h[i]

        return 0
    }

    private fun add(p: Array<LongArray>, q: Array<LongArray>) {
        val a = LongArray(16)
        val b = LongArray(16)
        val c = LongArray(16)
        val d = LongArray(16)
        val e = LongArray(16)
        val f = LongArray(16)
        val g = LongArray(16)
        val h = LongArray(16)
        val t = LongArray(16)

        Z(a, p[1], p[0])
        Z(t, q[1], q[0])
        M(a, a, t)
        A(b, p[0], p[1])
        A(t, q[0], q[1])
        M(b, b, t)
        M(c, p[3], q[3])
        M(c, c, D2)
        M(d, p[2], q[2])
        A(d, d, d)
        Z(e, b, a)
        Z(f, d, c)
        A(g, d, c)
        A(h, b, a)

        M(p[0], e, f)
        M(p[1], h, g)
        M(p[2], g, f)
        M(p[3], e, h)
    }

    private fun cswap(p: Array<LongArray>, q: Array<LongArray>, b: Byte) {
        for (i in 0 until 4) {
            sel25519(p[i], q[i], b.toInt())
        }
    }

    private fun pack(r: ByteArray, p: Array<LongArray>) {
        val tx = LongArray(16)
        val ty = LongArray(16)
        val zi = LongArray(16)
        inv25519(zi, p[2])
        M(tx, p[0], zi)
        M(ty, p[1], zi)
        pack25519(r, ty)
        r[31] = r[31] xor ((par25519(tx).toInt() shl 7) and 0xff).toByte()
    }

    private fun scalarmult(p: Array<LongArray>, q: Array<LongArray>, s: ByteArray) {
        set25519(p[0], gf0)
        set25519(p[1], gf1)
        set25519(p[2], gf1)
        set25519(p[3], gf0)
        for (i in 255 downTo 0) {
            val b: Byte = ((s[i / 8].toInt() shr (i and 7)) and 1).toByte()
            cswap(p, q, b)
            add(q, p)
            add(p, p)
            cswap(p, q, b)
        }
    }

    private fun scalarbase(p: Array<LongArray>, s: ByteArray) {
        val q = Array(4) { LongArray(16) }
        set25519(q[0], X)
        set25519(q[1], Y)
        set25519(q[2], gf1)
        M(q[3], X, Y)
        scalarmult(p, q, s)
    }

    //XXX: check array sizes (32, 64)?
    private fun crypto_sign_keypair(pk: ByteArray, sk: ByteArray): Int {
        val d = ByteArray(64)
        val p = Array(4) { LongArray(16) }

        randombytes(sk, 32)
        crypto_hash(d, sk, 32)
        d[0] = d[0] and 248.toByte()
        d[31] = d[31] and 127
        d[31] = d[31] or 64

        scalarbase(p, d)
        pack(pk, p)

        for (i in 0 until 32) sk[32 + i] = pk[i]
        return 0
    }

    private val L = longArrayOf(
        0xed, 0xd3, 0xf5, 0x5c,
        0x1a, 0x63, 0x12, 0x58,
        0xd6, 0x9c, 0xf7, 0xa2,
        0xde, 0xf9, 0xde, 0x14,
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0x10)

    private fun modL(r: ByteArray, x: LongArray, ri: Int = 0) {
        var carry: Long
        for (i in 63 downTo 32) {
            carry = 0
            for (j in (i - 32) until (i - 12)) {
                x[j] += carry - 16 * x[i] * L[j - (i - 32)]
                carry = floor((x[j].toDouble() + 128.0) / 256.0).roundToLong()
                x[j] -= carry shl 8
            }
            ///XXX: check index arithmetic
            x[i - 12 - 1] += carry
            x[i] = 0
        }
        carry = 0
        for (j in 0 until 32) {
            x[j] += carry - (x[31] shr 4) * L[j]
            carry = x[j] shr 8
            x[j] = x[j] and 255
        }
        for (j in 0 until 32) {
            x[j] -= carry * L[j]
        }
        for (i in 0 until 32) {
            x[i + 1] += x[i] shr 8
            r[i + ri] = (x[i] and 255).toByte()
        }
    }

    private fun reduce(r: ByteArray) {
        val x = LongArray(64)
        for (i in 0 until 64) {
            x[i] = r[i].toLong()
            r[i] = 0
            //xxx: check result
        }
        modL(r, x)
    }

    private fun crypto_sign(sm: ByteArray, m: ByteArray, n: Long, sk: ByteArray): Int {
        require(sm.size >= n + 64) { "resulting array sm(size=${sm.size}) must be able to fit n+64 bytes (${n + 64})" }
        val d = ByteArray(64)
        val h = ByteArray(64)
        val r = ByteArray(64)
        val x = LongArray(64)
        val p = Array(4) { LongArray(16) }

        crypto_hash(d, sk, 32)
        d[0] = d[0] and 248.toByte()
        d[31] = d[31] and 127
        d[31] = d[31] or 64

        for (i in 0 until n.toInt()) {
            sm[64 + i] = m[i]
        }
        for (i in 0 until 32) {
            sm[32 + i] = d[32 + i]
        }

        crypto_hash(r, sm.copyOfRange(32, sm.size), n + 32)
        reduce(r)
        scalarbase(p, r)
        pack(sm, p)

        for (i in 0 until 32) {
            sm[i + 32] = sk[i + 32]
        }
        crypto_hash(h, sm, n + 64)
        reduce(h)

        for (i in 0 until 64) {
            x[i] = 0
        }
        for (i in 0 until 32) {
            x[i] = r[i].toLong()
        }
        for (i in 0 until 32) for (j in 0 until 32) {
            x[i + j] += (h[i] * d[j]).toLong()
        }
        modL(sm, x, 32)

        return 0
    }

    //check lengths r[4], p[32]
    private fun unpackneg(r: Array<LongArray>, p: ByteArray): Int {
        val t = LongArray(16)
        val chk = LongArray(16)
        val num = LongArray(16)
        val den = LongArray(16)
        val den2 = LongArray(16)
        val den4 = LongArray(16)
        val den6 = LongArray(16)
        set25519(r[2], gf1)
        unpack25519(r[1], p)
        S(num, r[1])
        M(den, num, D)
        Z(num, num, r[2])
        A(den, r[2], den)

        S(den2, den)
        S(den4, den2)
        M(den6, den4, den2)
        M(t, den6, num)
        M(t, t, den)

        pow2523(t, t)
        M(t, t, num)
        M(t, t, den)
        M(t, t, den)
        M(r[0], t, den)

        S(chk, r[0])
        M(chk, chk, den)
        if (neq25519(chk, num) != 0) {
            M(r[0], r[0], I)
        }

        S(chk, r[0])
        M(chk, chk, den)
        if (neq25519(chk, num) != 0) {
            return -1
        }

        if (par25519(r[0]) == ((p[31].toInt() shr 7) and 0xff).toByte()) {
            Z(r[0], gf0, r[0])
        }

        M(r[3], r[0], r[1])
        return 0
    }

    private fun crypto_sign_open(m: ByteArray, sm: ByteArray, n: Long, pk: ByteArray): Int {
        val nn = (n - 64).toInt()
        require(m.size >= nn) { "resulting array `m` size must be at least $nn but is ${m.size}" }
        val t = ByteArray(32)
        val h = ByteArray(64)
        val p = Array(4) { LongArray(16) }
        val q = Array(4) { LongArray(16) }

        if (n < 64) {
            return -1
        }

        if (unpackneg(q, pk) != 0) {
            return -1
        }

        for (i in 0 until n.toInt()) {
            m[i] = sm[i]
        }
        for (i in 0 until 32) m[i + 32] = pk[i]
        crypto_hash(h, m, n)
        reduce(h)
        scalarmult(p, q, h)

        scalarbase(q, sm.copyOfRange(32, sm.size))
        add(p, q)
        pack(t, p)

        if (crypto_verify_32(sm, 0, t, 0) != 0) {
            for (i in 0 until nn) {
                m[i] = 0
            }
            return -1
        }

        for (i in 0 until nn) m[i] = sm[i + 64]
        return 0
    }
}

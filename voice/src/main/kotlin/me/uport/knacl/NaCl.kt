package me.uport.knacl

/**
 * This class exposes the methods from the NaCl library that are used by this SDK
 */
object nacl {

    const val crypto_secretbox_KEYBYTES = 32
    const val crypto_secretbox_NONCEBYTES = 24
    private const val crypto_secretbox_ZEROBYTES = 32
    private const val crypto_secretbox_BOXZEROBYTES = 16
    private const val crypto_scalarmult_BYTES = 32
    private const val crypto_scalarmult_SCALARBYTES = 32
    const val crypto_box_PUBLICKEYBYTES = 32
    const val crypto_box_SECRETKEYBYTES = 32
    const val crypto_box_BEFORENMBYTES = 32
    const val crypto_box_NONCEBYTES = crypto_secretbox_NONCEBYTES
    const val crypto_box_ZEROBYTES = crypto_secretbox_ZEROBYTES
    const val crypto_box_BOXZEROBYTES = crypto_secretbox_BOXZEROBYTES
    private const val crypto_sign_BYTES = 64
    private const val crypto_sign_PUBLICKEYBYTES = 32
    private const val crypto_sign_SECRETKEYBYTES = 64
    private const val crypto_sign_SEEDBYTES = 32
    private const val crypto_hash_BYTES = 64

    private fun checkLengths(k: ByteArray, n: ByteArray) {
        require(k.size == crypto_secretbox_KEYBYTES) { "bad key size" }
        require(n.size == crypto_secretbox_NONCEBYTES) { "bad nonce size" }
    }

    private fun checkBoxLengths(pk: ByteArray, sk: ByteArray) {
        require(pk.size == crypto_box_PUBLICKEYBYTES) { "bad public key size" }
        require(sk.size == crypto_box_SECRETKEYBYTES) { "bad secret key size" }
    }

    //FIXME: this should be outsourced to a higher level API
    fun randomBytes(size: Int) = NaClLowLevel.randombytes(size)

    object secretbox {

        fun seal(msg: ByteArray, nonce: ByteArray, key: ByteArray): ByteArray {
            checkLengths(key, nonce)
            val m = ByteArray(crypto_secretbox_ZEROBYTES + msg.size)
            val c = ByteArray(m.size)
            msg.copyInto(m, crypto_secretbox_ZEROBYTES)
            NaClLowLevel.crypto_secretbox(c, m, m.size.toLong(), nonce, key)
            return c.copyOfRange(crypto_secretbox_BOXZEROBYTES, c.size)
        }

        fun open(box: ByteArray, nonce: ByteArray, key: ByteArray): ByteArray? {
            checkLengths(key, nonce)
            val ciphertext = ByteArray(crypto_secretbox_BOXZEROBYTES + box.size)
            val msg = ByteArray(ciphertext.size)
            box.copyInto(ciphertext, crypto_secretbox_BOXZEROBYTES)
            if (ciphertext.size < 32) {
                return null
            }
            if (NaClLowLevel.crypto_secretbox_open(msg, ciphertext, ciphertext.size.toLong(), nonce, key) != 0) {
                return null
            }
            return msg.copyOfRange(crypto_secretbox_ZEROBYTES, msg.size)
        }
    }


    internal fun scalarMult(n: ByteArray, p: ByteArray): ByteArray {
        require(n.size == crypto_scalarmult_SCALARBYTES) { "bad n size" }
        require(p.size == crypto_scalarmult_BYTES) { "bad p size" }
        val q = ByteArray(crypto_scalarmult_BYTES)
        NaClLowLevel.crypto_scalarmult(q, n, p)
        return q
    }


    object box {

        internal fun before(publicKey: ByteArray, secretKey: ByteArray): ByteArray {
            checkBoxLengths(publicKey, secretKey)
            val k = ByteArray(crypto_box_BEFORENMBYTES)
            NaClLowLevel.crypto_box_beforenm(k, publicKey, secretKey)
            return k
        }

        fun seal(msg: ByteArray, nonce: ByteArray, publicKey: ByteArray, secretKey: ByteArray): ByteArray {
            val k = before(publicKey, secretKey)
            return secretbox.seal(msg, nonce, k)
        }

        fun open(msg: ByteArray, nonce: ByteArray, publicKey: ByteArray, secretKey: ByteArray): ByteArray? {
            val k = before(publicKey, secretKey)
            return secretbox.open(msg, nonce, k)
        }

        /**
         * Generates a new key pair
         */
        fun keyPair(): Pair<ByteArray, ByteArray> {
            val pk = ByteArray(crypto_box_PUBLICKEYBYTES)
            val sk = ByteArray(crypto_box_SECRETKEYBYTES)
            NaClLowLevel.crypto_box_keypair(pk, sk)
            return (pk to sk)
        }

        /**
         * Derives a public key and returns a keypair of the form (publicKey, secretKey)
         */
        fun keyPairFromSecretKey(secretKey: ByteArray): Pair<ByteArray, ByteArray> {
            require(secretKey.size == crypto_box_SECRETKEYBYTES) { "bad secret key size" }
            val pk = ByteArray(crypto_box_PUBLICKEYBYTES)
            NaClLowLevel.crypto_scalarmult_base(pk, secretKey)
            return (pk to secretKey)
        }
    }


    /////////////////////////////
    //
    // TODO: hash and sign functionality from NaCl has not been checked; methods are marked private until tested
    //
    /////////////////////////////

}
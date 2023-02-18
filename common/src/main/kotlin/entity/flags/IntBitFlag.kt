package dev.kord.common.entity.flags

import dev.kord.common.DiscordBitSet

/**
 * [BitFlag] with a [DiscordBitSet] value.
 */
public interface IntBitFlag : BitFlag<Int>


/**
 * Abstract implementation of [BitFlags] for [integers][Int].
 *
 * @param T the [BitFlag] type
 * @param Implementation this class
 * @param Builder the Builder for this class
 *
 * @property code the combined bit flag value
 * @property values A [Set] of all [flags][T] that are in [code]
 */
public abstract class IntBitFlags<T : IntBitFlag,
        Implementation : IntBitFlags<T, Implementation, Builder>,
        Builder : IntBitFlags.Builder<T, Implementation>>(allFlags: List<T>, code: Int = 0) :
    BitFlags<Int, T, Implementation, Builder>(code) {

    public override val values: Set<T> = allFlags.filter { code and it.code != 0 }.toSet()

    public override operator fun contains(flag: T): Boolean = flag.code and this.code == flag.code

    public override operator fun contains(flags: Implementation): Boolean = flags.code and this.code == flags.code

    public override operator fun plus(flags: Implementation): Implementation = Implementation(this.code or flags.code)

    public override operator fun plus(flag: T): Implementation = Implementation(this.code or flag.code)

    public override operator fun minus(flags: Implementation): Implementation = Implementation(this.code xor flags.code)

    public override operator fun minus(flag: T): Implementation = Implementation(this.code xor flag.code)

    /**
     * Abstract implementation of [BitFlags.Builder] for [integers][Int].
     *
     * @param T the [BitFlag] type
     * @param Implementation this class
     *
     * @property code the numeric value of this builder
     */
    public abstract class Builder<T : IntBitFlag, Implementation : IntBitFlags<T, Implementation, *>>(
        override var code: Int = 0
    ) : BitFlags.Builder<Int, T, Implementation> {
        public final override operator fun T.unaryPlus() {
            this@Builder.code = this@Builder.code or code
        }

        public final override operator fun T.unaryMinus() {
            if (this@Builder.code and code == code) {
                this@Builder.code = this@Builder.code xor code
            }
        }

        public final override operator fun Implementation.unaryPlus() {
            this@Builder.code = this@Builder.code or code
        }

        public final override operator fun Implementation.unaryMinus() {
            if (this@Builder.code and code == code) {
                this@Builder.code = this@Builder.code xor code
            }
        }
    }
}

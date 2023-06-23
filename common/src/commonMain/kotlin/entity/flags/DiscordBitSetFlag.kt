package dev.kord.common.entity.flags

import dev.kord.common.DiscordBitSet
import dev.kord.common.EmptyBitSet

/**
 * [BitFlag] with a [DiscordBitSet] value.
 */
public interface DiscordBitSetFlag : BitFlag<DiscordBitSet>

/**
 * Abstract implementation of [BitFlags] for [bit sets][DiscordBitSet].
 *
 * @param T the [BitFlag] type
 * @param Implementation this class
 * @param Builder the Builder for this class
 *
 * @property code the combined bit flag value
 * @property values A [Set] of all [flags][T] that are in [code]
 */
public abstract class DiscordBitSetFlags<T : DiscordBitSetFlag,
        Implementation : DiscordBitSetFlags<T, Implementation, Builder>,
        Builder : DiscordBitSetFlags.Builder<T, Implementation>>(
    private val allValues: List<T>,
    code: DiscordBitSet = EmptyBitSet()
) : BitFlags<DiscordBitSet, T, Implementation, Builder>(code) {

    override val values: Set<T> get() = allValues.filter { it.code in code }.toSet()

    override operator fun plus(flag: T): Implementation = Implementation(code + flag.code)

    override operator fun minus(flag: T): Implementation = Implementation(code - flag.code)

    override operator fun plus(flags: Implementation): Implementation = Implementation(code + flags.code)

    override operator fun minus(flags: Implementation): Implementation = Implementation(code - flags.code)

    override operator fun contains(flag: T): Boolean {
        return flag.code in code
    }

    override operator fun contains(flags: Implementation): Boolean {
        return flags.code in code
    }

    /**
     * Abstract implementation of [BitFlags.Builder] for [bit sets][DiscordBitSet].
     *
     * @param T the [BitFlag] type
     * @param Implementation this class
     *
     * @property code the numeric value of this builder
     */
    public abstract class Builder<T : DiscordBitSetFlag, Implementation : DiscordBitSetFlags<T, Implementation, *>>(
        override val code: DiscordBitSet = EmptyBitSet()
    ) : BitFlags.Builder<DiscordBitSet, T, Implementation> {
        public override operator fun Implementation.unaryPlus() {
            this@Builder.code.add(code)
        }

        public override operator fun Implementation.unaryMinus() {
            this@Builder.code.remove(code)
        }

        public override operator fun T.unaryPlus() {
            this@Builder.code.add(code)
        }

        public override operator fun T.unaryMinus() {
            this@Builder.code.remove(code)
        }
    }

    /**
     * Utility class for builder functions for bit set flags.
     *
     * @param T the [BitFlag] type
     * @param Implementation this class
     * @param Builder the Builder for this class
     */
    public abstract class Companion<T : DiscordBitSetFlag, Implementation : DiscordBitSetFlags<T, Implementation, Builder>, Builder : DiscordBitSetFlags.Builder<T, Implementation>> :
        BitFlags.Companion<DiscordBitSet, T, Implementation, Builder>() {
        protected abstract fun Implementation(flags: DiscordBitSet): Implementation

        /**
         * Creates an [Implementation] from [flags].
         */
        public operator fun invoke(flags: String): Implementation = Implementation(DiscordBitSet(flags))
    }
}

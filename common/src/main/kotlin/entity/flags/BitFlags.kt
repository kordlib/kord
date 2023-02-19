package dev.kord.common.entity.flags

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Representation of a Discord Bit Flag (Like a Permission or a Message flag).
 *
 * @param Type the numeric value type
 * @property code the raw value of the bit flag
 */
public interface BitFlag<Type> {
    public val code: Type
}

/**
 * An convenience container to combine flags into a single flag.
 *
 * @param Type the numeric value type
 * @param T the [BitFlag] type
 * @param Implementation this class
 * @param Builder the Builder for this class
 *
 * @property code the combined bit flag value
 * @property values A [Set] of all [flags][T] that are in [code]
 */
public abstract class BitFlags<Type,
        T : BitFlag<Type>,
        Implementation : BitFlags<Type, T, Implementation, Builder>,
        Builder : BitFlags.Builder<Type, T, Implementation>
        >(public val code: Type) {
    protected abstract val name: String
    public abstract val values: Set<T>

    /**
     * Combines [flag] and this.
     */
    public abstract operator fun plus(flag: T): Implementation

    /**
     * Removes [flag] from this.
     */
    public abstract operator fun minus(flag: T): Implementation

    /**
     * Combines [flags] and this.
     */
    public abstract operator fun plus(flags: Implementation): Implementation

    /**
     * Removes all [flags] from this.
     */
    public abstract operator fun minus(flags: Implementation): Implementation

    /**
     * Checks whether this contains [flag].
     */
    public abstract operator fun contains(flag: T): Boolean

    /**
     * Checks whether this contains [flags].
     */
    public abstract operator fun contains(flags: Implementation): Boolean

    public abstract fun buildUpon(): Builder
    protected abstract fun Implementation(flags: Type): Implementation
    protected fun Implementation(flags: Implementation): Implementation = Implementation(flags.code)

    override fun toString(): String = "$name(values=$values)"

    /**
     * Utility class for builder functions.
     *
     * @param Type the numeric value type
     * @param T the [BitFlag] type
     * @param Implementation this class
     * @param Builder the Builder for this class
     */
    public abstract class Companion<Type,
            T : BitFlag<Type>,
            Implementation : BitFlags<Type, T, Implementation, Builder>,
            Builder : BitFlags.Builder<Type, T, Implementation>
            > {
        public abstract fun Builder(): Builder

        /**
         * Creates a new container and applies [builder] to it.
         *
         * ```kotlin
         * val flags = Flags {
         *  +flag1
         * }
         * ```
         */
        public inline operator fun invoke(builder: Builder.() -> Unit): Implementation =
            Builder().apply(builder).flags()

        /**
         * Creates a new Container from [flags].
         *
         * ```kotlin
         * val flags = Flags(Flag.flag1)
         * ```
         */
        public operator fun invoke(vararg flags: T): Implementation = invoke {
            flags.forEach { +it }
        }

        /**
         * Creates a new Container from [flags].
         *
         * ```kotlin
         * val flags = Flags(Flag.flag1)
         * val flags2 = Flags(flags)
         * ```
         */
        public operator fun invoke(vararg flags: Implementation): Implementation = invoke {
            flags.forEach { +it }
        }

        /**
         * Creates a new Container from [flags].
         *
         * ```kotlin
         * val flags = listOf(Flag.flag1, Flag.flag2)
         * val container = Flags(flags)
         * ```
         */
        public operator fun invoke(flags: Iterable<T>): Implementation = invoke {
            flags.forEach { +it }
        }

        /**
         * Creates a new Container from [flags].
         *
         * ```kotlin
         * val flags = Flags(Flag.flag1)
         * val flags2 = Flags(flags)
         * val flagContainer = Flags(listOf(flags, flags2))
         * ```
         */
        @JvmName("buildWithIterable")
        public operator fun invoke(flags: Iterable<Implementation>): Implementation = invoke {
            flags.forEach { +it }
        }
    }

    /**
     * Interface for a bit flag container builder.
     *
     * @param Type the numeric value type
     * @param T the [BitFlag] type
     * @param Implementation this class
     *
     * @property code the numeric value of this builder
     */
    public interface Builder<
            Type,
            T : BitFlag<Type>,
            Implementation : BitFlags<Type, T, Implementation, *>,
            > {
        public val code: Type

        /**
         * Adds [Implementation] to this.
         */
        public operator fun Implementation.unaryPlus()

        /**
         * Removes [Implementation] from this.
         */
        public operator fun Implementation.unaryMinus()

        /**
         * Adds [T] to this.
         */
        public operator fun T.unaryPlus()

        /**
         * Removes [T] from this.
         */
        public operator fun T.unaryMinus()

        /**
         * Converts this into [Implementation].
         */
        public fun flags(): Implementation
    }

    /**
     * Abstract implementation of a flags serializer.
     *
     * @param Type the numeric value type
     * @param T the [BitFlag] type
     * @param Implementation this class
     *
     * @property primitiveKind the [PrimitiveKind] this gets serialized as
     * @property serialName the serial name
     * @property serializer a [KSerializer] for [Type]
     */
    public abstract class Serializer<Type, T : BitFlag<Type>, Implementation : BitFlags<Type, T, Implementation, *>>(
        private val primitiveKind: PrimitiveKind,
        private val serialName: String = "flags",
        private val serializer: KSerializer<Type>
    ) : KSerializer<Implementation> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor(serialName, primitiveKind)

        override fun deserialize(decoder: Decoder): Implementation {
            val permissions = decoder.decodeSerializableValue(serializer)
            return Implementation(permissions)
        }

        override fun serialize(encoder: Encoder, value: Implementation) {
            val permissionsSet = value.code
            encoder.encodeSerializableValue(serializer, permissionsSet)
        }

        /**
         * Builder function creating an [Implementation] from [code].
         */
        protected abstract fun Implementation(code: Type): Implementation
    }
}

/**
 * Creates a copy of this container and applies [block] to it.
 *
 * ```kotlin
 * flags.copy {
 *  -flag1
 *  +flag2
 *  +flag3
 * }
 * ```
 */
public inline fun <Implementation : BitFlags<*, *, Implementation, Builder>,
        Builder : BitFlags.Builder<*, *, Implementation>
        >
        Implementation.copy(block: Builder.() -> Unit = {}): Implementation {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return buildUpon().apply(block).flags()
}

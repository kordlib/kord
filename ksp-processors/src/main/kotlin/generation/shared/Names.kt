package dev.kord.ksp.generation.shared

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName
import kotlinx.serialization.KSerializer

internal val OPT_IN = ClassName("kotlin", "OptIn")
internal val CONTRACT = MemberName("kotlin.contracts", "contract")
internal val EXACTLY_ONCE = MemberName("kotlin.contracts.InvocationKind", "EXACTLY_ONCE")

internal val PRIMITIVE_SERIAL_DESCRIPTOR = MemberName("kotlinx.serialization.descriptors", "PrimitiveSerialDescriptor")
internal val K_SERIALIZER = KSerializer::class.asClassName()

internal val KORD_UNSAFE = ClassName("dev.kord.common.annotation", "KordUnsafe")
internal val DISCORD_BIT_SET = ClassName("dev.kord.common", "DiscordBitSet")
internal val EMPTY_BIT_SET = MemberName("dev.kord.common", "EmptyBitSet")

@file:OptIn(ExperimentalContracts::class, DelicateKotlinPoetApi::class)

package dev.kord.ksp.generation.shared

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName
import dev.kord.codegen.kotlinpoet.asMemberName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal val OPT_IN = ClassName("kotlin", "OptIn")

internal val CONTRACT = ::contract.asMemberName()
internal val EXACTLY_ONCE = InvocationKind.EXACTLY_ONCE.asMemberName()

internal val PRIMITIVE_SERIAL_DESCRIPTOR = ::PrimitiveSerialDescriptor.asMemberName()
internal val K_SERIALIZER = KSerializer::class.asClassName()

internal val DISCORD_BIT_SET = ClassName("dev.kord.common", "DiscordBitSet")
internal val EMPTY_BIT_SET = MemberName("dev.kord.common", "EmptyBitSet")

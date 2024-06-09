package dev.kord.compiler

import org.jetbrains.kotlin.backend.common.ClassLoweringPass
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.ERROR
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.INFO
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities.PUBLIC
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addConstructor
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOriginImpl
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.symbols.impl.IrClassSymbolImpl
import org.jetbrains.kotlin.ir.types.SimpleTypeNullability.DEFINITELY_NOT_NULL
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.jvm.isJvm

private val DEFAULT_FRAME_INTERCEPTOR_DATA = ClassId.fromString("dev/kord/voice/DefaultFrameInterceptorData")
private val SPEAKING_FLAGS = ClassId.fromString("dev/kord/voice/SpeakingFlags")
private val VALUE_CLASSES_BINARY_COMPATIBILITY_ORIGIN =
    IrDeclarationOriginImpl("VALUE_CLASSES_BINARY_COMPATIBILITY", isSynthetic = true)

@OptIn(ExperimentalCompilerApi::class)
class ValueClassesBinaryCompatibilityCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val supportsK2 get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        IrGenerationExtension.registerExtension(ValueClassesBinaryCompatibilityIrGenerationExtension(messageCollector))
    }
}

private class ValueClassesBinaryCompatibilityIrGenerationExtension(
    private val messageCollector: MessageCollector,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val platform = pluginContext.platform
        if (!pluginContext.platform.isJvm()) {
            messageCollector.report(ERROR, "Plugin shouldn't be applied to non JVM platforms: $platform")
            return
        }
        ValueClassesBinaryCompatibilityClassLoweringPass(messageCollector, pluginContext).lower(moduleFragment)
    }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
private class ValueClassesBinaryCompatibilityClassLoweringPass(
    private val messageCollector: MessageCollector,
    private val context: IrPluginContext,
) : ClassLoweringPass {
    private val speakingFlags = IrSimpleTypeImpl(
        classifier = IrClassSymbolImpl().apply {
            bind(
                owner = context.irFactory
                    .buildClass { name = SPEAKING_FLAGS.shortClassName }
                    .apply { parent = context.referenceClass(SPEAKING_FLAGS)!!.owner.parent }
            )
        },
        nullability = DEFINITELY_NOT_NULL,
        arguments = emptyList(),
        annotations = emptyList(),
    )

    private val speakingState = Name.identifier("speakingState")
    private val component1 = Name.identifier("component1")
    private val copy = Name.identifier("copy")

    private fun IrSymbol.builder() = DeclarationIrBuilder(context, symbol = this)

    override fun lower(irClass: IrClass) {
        if (irClass.classId != DEFAULT_FRAME_INTERCEPTOR_DATA) {
            return
        }
        messageCollector.report(INFO, "lowering ${irClass.classId}")

        val primaryCtor = irClass.primaryConstructor!!

        val ctor1 = irClass.addConstructor {
            origin = VALUE_CLASSES_BINARY_COMPATIBILITY_ORIGIN
            visibility = PUBLIC
        }
        ctor1.body = ctor1.symbol.builder().irBlockBody {
            +irDelegatingConstructorCall(primaryCtor)
        }
        messageCollector.report(INFO, "ctor1: ${ctor1.render()}")

        val ctor2 = irClass.addConstructor {
            origin = VALUE_CLASSES_BINARY_COMPATIBILITY_ORIGIN
            visibility = PUBLIC
        }
        val param = ctor2
            .addValueParameter("speakingState", speakingFlags, VALUE_CLASSES_BINARY_COMPATIBILITY_ORIGIN)
            .apply { defaultValue = primaryCtor.valueParameters.first().defaultValue }
        ctor2.body = ctor2.symbol.builder().irBlockBody {
            +irDelegatingConstructorCall(primaryCtor).apply { putValueArgument(index = 0, irGet(param)) }
        }
        messageCollector.report(INFO, "ctor2: ${ctor2.render()}")

        val inlineSpeakingState = irClass.properties.single { it.name == speakingState }
        val speakingState = irClass.addFunction(
            name = "getSpeakingState",
            returnType = speakingFlags,
            visibility = PUBLIC,
            origin = VALUE_CLASSES_BINARY_COMPATIBILITY_ORIGIN,
        ).apply {
            body = with(symbol.builder()) {
                irExprBody(irCall(inlineSpeakingState.getter!!))
            }
        }
        messageCollector.report(INFO, "speakingState: ${speakingState.render()}")

        val inlineComponent1 = irClass.functions.single { it.name == component1 }
        val component1 = inlineComponent1.deepCopyWithSymbols(irClass).apply {
            origin = VALUE_CLASSES_BINARY_COMPATIBILITY_ORIGIN
            returnType = speakingFlags
            body = with(symbol.builder()) {
                irExprBody(irCall(inlineComponent1).apply { dispatchReceiver = irGet(dispatchReceiverParameter!!) })
            }
        }
        irClass.declarations.add(component1)
        messageCollector.report(INFO, "component1: ${component1.render()}")

        val inlineCopy = irClass.functions.single { it.name == copy }
        val copy = inlineCopy.deepCopyWithSymbols(irClass).apply {
            origin = VALUE_CLASSES_BINARY_COMPATIBILITY_ORIGIN
            val copyParam = valueParameters.single().apply {
                type = speakingFlags
            }
            body = symbol.builder().irBlockBody {
                +irReturn(irCall(inlineCopy).apply {
                    dispatchReceiver = irGet(dispatchReceiverParameter!!)
                    putValueArgument(index = 0, irGet(copyParam))
                })
            }
        }
        irClass.declarations.add(copy)
        messageCollector.report(INFO, "copy: ${copy.render()}")
    }
}

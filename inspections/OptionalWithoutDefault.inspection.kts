import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.idea.base.psi.setDefaultValue
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.*

private val optionalTypes =
    listOf("Optional", "OptionalBoolean", "OptionalInt", "OptionalLong", "OptionalSnowflake")
        .map { "dev.kord.common.entity.optional.$it" }
        .toSet()

@Language("HTML")
val htmlDescription = """
    <html>
    <body>
        This inspection reports misusage of 'Optional' classes, when using them without a default value
        
        Supported classes: ${optionalTypes.map { "<code>${it.substringAfterLast('.')}</code>" }}
    </body>
    </html>
""".trimIndent()

private class AddDefaultQuickfix(private val optionalClassName: String) : LocalQuickFix {
    override fun getFamilyName(): String = "Add '${optionalClassName}.Missing' as a default value"

    override fun applyFix(project: Project, problem: ProblemDescriptor) {
        val factory = KtPsiFactory(project)
        val optionalType = buildString {
            append(optionalClassName)
            append(".Missing")
            if (optionalClassName == "Optional") {
                append("()")
            }
        }
        val initializer = factory.createExpression(optionalType)

        val parameter = problem.psiElement as KtParameter
        parameter.setDefaultValue(initializer)
    }
}

val optionalWithoutDefaultInspection = localInspection { psiFile, inspection ->
    val serializable = ClassId.fromString("kotlinx/serialization/Serializable")

    psiFile
        .descendantsOfType<KtClass>()
        .filter { it.findAnnotation(serializable, withResolve = true) != null }
        .flatMap(KtClass::allConstructors)
        .flatMap(KtConstructor<*>::getValueParameters)
        .filterNot(KtParameter::hasDefaultValue)
        .filter { it.isOptionalTypeParameter() }
        .forEach {
            analyze(it) {
                inspection.registerProblem(
                    it,
                    "This parameter should have a default value",
                    AddDefaultQuickfix(it.getReturnKtType().expandedClassSymbol!!.name!!.asString())
                )
            }
        }
}

fun KtParameter.isOptionalTypeParameter() = analyze(this) {
    getReturnKtType().fullyExpandedType.expandedClassSymbol?.getFQN() in optionalTypes
}

listOf(
    InspectionKts(
        id = "OptionalWithoutDefault",
        localTool = optionalWithoutDefaultInspection,
        name = "Optional without default inspection",
        htmlDescription = htmlDescription,
        level = HighlightDisplayLevel.NON_SWITCHABLE_ERROR,
    )
)
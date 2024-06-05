import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.idea.util.addAnnotation
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtClass

@Language("HTML")
val htmlDescription = """
    <html>
    <body>
        This reports subtypes of <code>dev.kord.rest.builder.RequestBuilder</code> which are not annotated with
        <code>@KordDsl</code>
    </body>
    </html>
""".trimIndent()


val kordDslId = ClassId.fromString("dev/kord/common/annotation/KordDsl")
val requestBuilderId = ClassId.fromString("dev/kord/rest/builder/RequestBuilder")

class AddKordDsl : LocalQuickFix {
    override fun getFamilyName(): String = "Add @KordDsl annotation"

    override fun applyFix(project: Project, problem: ProblemDescriptor) {
        problem.psiElement.parentsOfType<KtAnnotated>().first().addAnnotation(kordDslId, searchForExistingEntry = false)
    }
}

val builderWithoutDslMarkerInspection = localInspection { psiFile, inspection ->

    psiFile.descendantsOfType<KtClass>().forEach {
        analyze(it) {
            val requestBuilderClass = getClassOrObjectSymbolByClassId(requestBuilderId) ?: return@analyze
            if (it.getClassOrObjectSymbol()?.isSubClassOf(requestBuilderClass) == true
                && it.findAnnotation(kordDslId) == null
                && it.hasModifier(KtTokens.PUBLIC_KEYWORD)
            ) {
                inspection.registerProblem(
                    it.nameIdentifier,
                    "This class should be annotated with @KordDsl",
                    AddKordDsl()
                )
            }
        }
    }
}


listOf(
    InspectionKts(
        id = "BuilderDslMarker",
        localTool = builderWithoutDslMarkerInspection,
        name = "Reports builder's without DSL annotations",
        htmlDescription = htmlDescription,
        level = HighlightDisplayLevel.ERROR,
    )
)
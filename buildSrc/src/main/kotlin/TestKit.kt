import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

val KotlinTarget.safeName: String get() = if (name == "metadata") "common" else name

fun KotlinMultiplatformExtension.addTestKit() {
    targets.forEach { target ->
        sourceSets.findByName("${target.safeName}Test")?.apply {
            dependencies {
                implementation(project(":test-kit"))
            }
        }
    }
}

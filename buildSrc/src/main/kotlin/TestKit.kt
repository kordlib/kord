import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

val KotlinTarget.safeName: String get() = if (name == "metadata") "common" else name

fun NamedDomainObjectContainer<KotlinSourceSet>.addTestKit(targets: Iterable<KotlinTarget>) {
    targets.forEach {
        findByName("${it.safeName}Test")?.apply {
            dependencies {
                implementation(project(":test-kit"))
            }
        }
    }
}

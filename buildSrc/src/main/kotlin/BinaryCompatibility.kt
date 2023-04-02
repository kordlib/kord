import kotlinx.validation.ApiValidationExtension

fun ApiValidationExtension.applyKordOptions() {
    val annotations = listOf("KordInternal")
    nonPublicMarkers.addAll(annotations.map { "dev.kord.common.annotation.$it" })
}

import kotlinx.validation.ApiValidationExtension

fun ApiValidationExtension.applyKordBCVOptions() {
    nonPublicMarkers += "dev.kord.common.annotation.KordInternal"
}

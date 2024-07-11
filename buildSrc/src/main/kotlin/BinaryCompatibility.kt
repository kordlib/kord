import kotlinx.validation.ApiValidationExtension
import kotlinx.validation.ExperimentalBCVApi

fun ApiValidationExtension.applyKordBCVOptions() {
    nonPublicMarkers += "dev.kord.common.annotation.KordInternal"
    @OptIn(ExperimentalBCVApi::class)
    klib.enabled = true
}

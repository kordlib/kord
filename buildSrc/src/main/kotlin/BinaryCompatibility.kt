@file:Suppress("DEPRECATION_ERROR")
@file:OptIn(ExperimentalAbiValidation::class)

import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

fun AbiValidationExtension.applyKordBCVOptions() {
    enabled = true
    filters
    applyFilters()
}

private fun AbiValidationExtension.applyFilters() {
    filters {
        exclude {
            annotatedWith.add("dev.kord.common.annotation.KordInternal")
        }
    }
}

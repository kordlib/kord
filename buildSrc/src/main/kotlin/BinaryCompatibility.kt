@file:OptIn(ExperimentalAbiValidation::class)

import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationVariantSpec
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

fun AbiValidationExtension.applyKordBCVOptions() {
    enabled = true
    applyFilters()
}

fun AbiValidationMultiplatformExtension.applyKordBCVOptions() {
    enabled = true
    applyFilters()
}

private fun AbiValidationVariantSpec.applyFilters() {
    filters {
        excluded {
            annotatedWith.add("dev.kord.common.annotation.KordInternal")
        }
    }
}

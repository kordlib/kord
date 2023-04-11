package dev.kord.test

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.condition.DisabledInNativeImage

actual typealias IgnoreOnJvm = Disabled

actual typealias IgnoreOnNativeImage = DisabledInNativeImage

import dev.kord.test.getEnv

val testToken = getEnv("KORD_TEST_TOKEN") ?: error("KORD_TEST_TOKEN is not defined")

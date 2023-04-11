import jetbrains.buildServer.configs.kotlin.RelativeId

val GraalVMNativeImageCI = MultiOSKordBuild("Run GraalVM Native image Tests", RelativeId("Run_Native_Image_tests")) {
    steps {
        debuggableGradle("Run Native Image Tests") {
            tasks = "nativeTest"
        }
    }
}

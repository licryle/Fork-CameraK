plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    id("org.jetbrains.compose")
    alias(libs.plugins.compose.compiler)
    id("maven-publish")
    id("signing")
}

group = "com.kashif.image_saver_plugin"
version = "1.0"

kotlin {
    jvmToolchain(11)
    androidTarget {
        publishLibraryVariants("release", "debug")
    }
    jvm("desktop")

    if (System.getProperty("os.name").contains("Mac")) {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64(),
        ).forEach {
            it.binaries.framework {
                baseName = "imagesaverplugin"
                isStatic = true
            }
        }
    }

    sourceSets {
        val desktopMain by getting
        commonMain.dependencies {
            api(projects.cameraK)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))

        }

        androidMain.dependencies {

        }

    }

    //https://kotlinlang.org/docs/native_objc_interop.html#export_of_kdoc_comments_to_generated_objective_c_headers
//    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
//        compilations["main"].compilerOptions.options.freeCompilerArgs.add("_Xexport_kdoc")
//    }

}

android {
    namespace = "com.kashif.image_saver_plugin"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }

    publishing {
        singleVariant("release") {
            withJavadocJar()
            withSourcesJar()
        }

        // For debug variant, we exclude Javadoc and sources to prevent conflicts
        singleVariant("debug") {
            // Exclude Javadoc and sources JARs for debug variant
        }
    }
}

publishing {
    repositories {
        maven {
            name = "localRepo"
            url = uri("${rootProject.buildDir}/../../repo")
        }
    }
    publications.withType<MavenPublication>().configureEach {
        // you can customize coordinates if needed
        groupId = "com.kashif.cameraK_fork"
        artifactId = "imagesaverplugin"
        version = "1.0"
    }
}

signing {
    // Only sign when keys are configured (like for Maven Central)
    setRequired(false)
}
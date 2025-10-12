import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.compose.compose

var os: OperatingSystem? = OperatingSystem.current()

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    id("org.jetbrains.compose")
    alias(libs.plugins.compose.compiler)
    id("com.vanniktech.maven.publish") version "0.31.0"
    id("maven-publish")
    id("signing")
}

group = "com.kashif.cameraK_fork"
version = "1.0"

kotlin {
    jvmToolchain(11)

    androidTarget {
        publishLibraryVariants("release", "debug")
    }
    jvm("desktop")


    if (os?.isMacOsX == true) {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64(),
        ).forEach {
            it.binaries.framework {
                baseName = "cameraK"
                isStatic = true
            }
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.framework {
            baseName = "cameraK"
        }
    }

    sourceSets {
        val desktopMain by getting {
            dependencies {
                api(libs.javacv.platform)
            }
        }

        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.coroutines.test)
            api(libs.kermit)
            api(compose.ui)
            api(compose.foundation)
            api(libs.coil3.compose)
            api(libs.coil3.ktor)
            api(libs.atomicfu)
        }

        commonTest.dependencies {
            api(kotlin("test"))
        }

        androidMain.dependencies {
            api(libs.kotlinx.coroutines.android)
            api(libs.camera.core)
            api(libs.camera.camera2)
            api(libs.androidx.camera.view)
            api(libs.camera.lifecycle)
            api(libs.camera.extensions)
            api(libs.androidx.activityCompose)
            api(libs.androidx.startup.runtime)
            api(libs.core)
        }
    }
}

android {
    namespace = "com.kashif.cameraK"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }

    publishing {
        singleVariant("release") {
            withJavadocJar()
            withSourcesJar()
        }

        singleVariant("debug")
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        // you can customize coordinates if needed
        groupId = "com.kashif.cameraK_fork"
        artifactId = "cameraK"
        version = "0.0.13"
    }

    repositories {
        mavenLocal()
    }
}

// Disable all signing
afterEvaluate {
    tasks.withType<Sign>().configureEach {
        enabled = false
    }
    publishing {
        publications.withType<MavenPublication>().configureEach {
            val pubName = name
            // Give each target a unique artifactId
            artifactId = when (pubName) {
                "kotlinMultiplatform" -> "camerak"
                else -> "camerak-$pubName"
            }
        }
    }
}

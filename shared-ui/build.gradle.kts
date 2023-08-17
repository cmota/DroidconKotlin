plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose") version "1.5.0-beta02"
}

android {
    compileSdkPreview = libs.versions.androidCompileSdk.get()
    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
    }

    sourceSets {
        val main by getting
        main.java.setSrcDirs(listOf("src/androidMain/kotlin"))
        main.res.setSrcDirs(listOf("src/androidMain/res"))
        main.resources.setSrcDirs(
            listOf(
                "src/androidMain/resources",
                "src/commonMain/resources"
            )
        )
        main.manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }

    namespace = "co.touchlab.droidcon.sharedui"
}

version = "1.0"


kotlin {
    androidTarget()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":shared"))

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.serialization)

                implementation(compose.foundation)
                implementation(compose.runtime)
                implementation(compose.material)
                implementation(compose.ui)

                implementation(libs.kotlinx.datetime)

                implementation(libs.koin.core)

                implementation(libs.image.loader)

                implementation(libs.uuid)

                implementation(libs.kermit)
                implementation(libs.hyperdrive.multiplatformx.api)
            }
        }
    }
}

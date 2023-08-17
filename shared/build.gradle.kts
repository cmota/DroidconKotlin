plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")
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
                "src/commonMain/resources",
            )
        )
        main.manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }

    namespace = "co.touchlab.droidcon.shared"
}

version = "1.0"

kotlin {
    androidTarget()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kermit)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.settings.multiplatform)
                implementation(libs.uuid)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.serialization)

                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines)

                implementation(libs.stately.common)
                implementation(libs.koin.core)
                implementation(libs.korio)
            }
        }

        androidTarget {
            dependencies {
                implementation(libs.sqldelight.driver.android)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.androidx.core)
            }
        }
    }
}

sqldelight {
    database("DroidconDatabase") {
        packageName = "co.touchlab.droidcon.db"
    }
}

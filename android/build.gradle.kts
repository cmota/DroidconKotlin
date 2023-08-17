plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    compileSdkPreview = libs.versions.androidCompileSdk.get()

    defaultConfig {
        applicationId = "co.touchlab.droidcon"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = 34
        versionCode = 20100
        versionName = "2.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources.excludes.add("META-INF/*.kotlin_module")
    }

    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    namespace = "co.touchlab.droidcon"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":shared-ui"))

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.koin.compose)
    implementation(libs.kotlinx.datetime)

    implementation(libs.hyperdrive.multiplatformx.api)

    implementation(libs.imageLoader)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.navigation)

    coreLibraryDesugaring(libs.android.desugar)
}

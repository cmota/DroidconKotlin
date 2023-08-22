plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    compileSdkPreview = libs.versions.androidCompileSdk.get()

    defaultConfig {
        applicationId = "co.touchlab.droidcon"
        minSdk = libs.versions.androidMinSdkAutomotive.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    namespace = "co.touchlab.droidcon"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":shared-ui"))
    implementation(project(":common:car"))

    implementation(libs.androidx.car.app.automotive)

    implementation(libs.koin.compose)

    implementation(libs.kotlinx.datetime)
}
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }

    val kotlinVersion: String by settings
    val sqldelightVersion: String by settings
    plugins {
        kotlin("multiplatform") version kotlinVersion
        kotlin("android") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("native.cocoapods") version kotlinVersion
        id("com.squareup.sqldelight") version sqldelightVersion
    }
}

include(":shared", ":shared-ui", ":android", ":common:car", ":automotiveApp")

rootProject.name = "Droidcon"

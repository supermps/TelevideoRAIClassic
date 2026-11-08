plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "it.televideoraiclassic"
    compileSdk = 35

    defaultConfig {
        applicationId = "it.televideoraiclassic"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}

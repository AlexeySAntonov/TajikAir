plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-android-extensions")
    id("com.google.gms.google-services")
    id("com.google.secrets_gradle_plugin") version "0.6"
}

android {
    signingConfigs {
        create("release") {
            keyAlias = System.getenv()["TAJIK_AIR_KEY_ALIAS"] ?: project.property("TAJIK_AIR_KEY_ALIAS") as String
            keyPassword = System.getenv()["TAJIK_AIR_KEY_PASSWORD"] ?: project.property("TAJIK_AIR_KEY_PASSWORD") as String
            storeFile = file(System.getenv()["TAJIK_AIR_KEYSTORE_PATH"] ?: project.property("TAJIK_AIR_KEYSTORE_PATH") as String)
            storePassword = System.getenv()["TAJIK_AIR_KEYSTORE_PASSWORD"] ?: project.property("TAJIK_AIR_KEYSTORE_PASSWORD") as String
        }
    }

    compileSdkVersion(Versions.compileSdk)

    defaultConfig {
        applicationId("com.aleksejantonov.tajikair")
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.targetSdk)
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(Libs.kotlin)
    implementation(Libs.appCompat)
    implementation(Libs.material)
    implementation(Libs.constraintLayout)

    // Rx
    implementation(Libs.rxAndroid)
    implementation(Libs.rxRelay)
    implementation(Libs.rxKotlin)
    implementation(Libs.rxBinding)

    // Retrofit
    implementation(Libs.retrofit2)
    implementation(Libs.retrofit2ConverterGson)
    implementation(Libs.retrofit2RxAdapter)

    // Search
    implementation(Libs.searchForms)

    // Map
    implementation(Libs.maps)
    implementation(Libs.mapsUtils)

    // Log
    implementation(Libs.timber)
}

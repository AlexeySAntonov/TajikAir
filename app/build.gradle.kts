plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-android-extensions")
    id("com.google.gms.google-services")
    id("com.google.secrets_gradle_plugin") version "0.6"
}

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId("com.aleksejantonov.tajikair")
        minSdkVersion(19)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

object Versions {
    const val kotlin_version = "1.4.32"
    const val moxy_version = "1.5.3"
    const val rxJava_version = "2.1.0"
    const val rxKotlin_version = "2.3.0"
    const val rxBinding_version = "2.0.0"
    const val retrofit_version = "2.4.0"
    const val stetho_version = "1.5.0"
    const val timber_version = "4.7.1"
    const val searchView_version = "2.1.1"
    const val googleMap_version = "16.1.0"
}

dependencies {
    //Support
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("com.google.android.material:material:1.1.0-alpha05")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")

    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin_version}")

    //Moxy
    implementation("com.arello-mobile:moxy:${Versions.moxy_version}")
    kapt("com.arello-mobile:moxy-compiler:${Versions.moxy_version}")

    //Rx
    implementation("io.reactivex.rxjava2:rxandroid:${Versions.rxJava_version}")
    implementation("com.jakewharton.rxrelay2:rxrelay:${Versions.rxJava_version}")
    implementation("com.tbruyelle.rxpermissions2:rxpermissions:0.9.5@aar")
    implementation("io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlin_version}")
    implementation("com.jakewharton.rxbinding2:rxbinding-design-kotlin:${Versions.rxBinding_version}")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit_version}")
    implementation("com.squareup.retrofit2:converter-gson:${Versions.retrofit_version}")
    implementation("com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit_version}")

    // Stetho
    implementation("com.facebook.stetho:stetho:${Versions.stetho_version}")
    implementation("com.facebook.stetho:stetho-okhttp3:${Versions.stetho_version}")

    // Search
    implementation("com.github.arimorty:floatingsearchview:${Versions.searchView_version}")

    // Map
    implementation("com.google.android.gms:play-services-maps:${Versions.googleMap_version}")
    implementation("com.google.maps.android:android-maps-utils:0.5")

    //Log
    implementation("com.jakewharton.timber:timber:${Versions.timber_version}")
}

object Versions {
    const val compileSdk = 30
    const val targetSdk = 30
    const val minSdk = 21

    const val kotlin = "1.5.10"
    const val coroutines = "1.5.0"
    const val retrofit = "2.9.0"
    const val dagger = "2.37"
}

object Libs {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val appCompat = "androidx.appcompat:appcompat:1.3.0"
    const val material = "com.google.android.material:material:1.4.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:1.2.4"

    const val retrofit2 = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofit2ConverterGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val retrofit2RxAdapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"

    const val maps = "com.google.android.gms:play-services-maps:17.0.1"
    const val mapsUtils = "com.google.maps.android:android-maps-utils:0.5"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"

    const val searchForms = "com.github.arimorty:floatingsearchview:2.1.1"

    const val timber = "com.jakewharton.timber:timber:4.7.1"
}

object BuildPlugins {
    const val KGP = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val AGP = "com.android.tools.build:gradle:4.2.1"
    const val googleServices = "com.google.gms:google-services:4.3.8"
}
plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-kapt")
  id("com.google.gms.google-services")
  id("com.google.secrets_gradle_plugin") version "0.6"
}

android {
  signingConfigs {
    create("release") {
      keyAlias = System.getenv()[KEY_ALIAS] ?: project.property(KEY_ALIAS) as String
      keyPassword = System.getenv()[KEY_PASSWORD] ?: project.property(KEY_PASSWORD) as String
      storeFile = file(System.getenv()[KEYSTORE_PATH] ?: project.property(KEYSTORE_PATH) as String)
      storePassword = System.getenv()[KEYSTORE_PASSWORD] ?: project.property(KEYSTORE_PASSWORD) as String
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
  buildFeatures {
    viewBinding = true
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}

dependencies {
  implementation(Libs.kotlin)
  implementation(Libs.coroutines)
  implementation(Libs.appCompat)
  implementation(Libs.material)
  implementation(Libs.constraintLayout)
  implementation(Libs.fragmentKtx)

  // Retrofit
  implementation(Libs.retrofit2)
  implementation(Libs.retrofit2ConverterGson)
  implementation(Libs.retrofit2RxAdapter)

  // Search
  implementation(Libs.searchForms)

  // Map
  implementation(Libs.maps)

  // Dagger
  implementation(Libs.dagger)
  kapt(Libs.daggerCompiler)

  // Log
  implementation(Libs.timber)
}

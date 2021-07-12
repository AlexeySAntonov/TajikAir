plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-kapt")
  id("com.google.gms.google-services")
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
    manifestPlaceholders[MAPS_API_KEY] = System.getenv()[MAPS_API_KEY] ?: project.property(MAPS_API_KEY) as String
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
      if (System.getenv()["CI"] == null) {
        signingConfig = signingConfigs.getByName("release")
      }
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

  // Map
  implementation(Libs.maps)

  // Dagger
  implementation(Libs.dagger)
  kapt(Libs.daggerCompiler)

  // Log
  implementation(Libs.timber)

  // Leaks
  debugImplementation(Libs.leakCanary)
}

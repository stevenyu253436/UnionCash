plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt") // 添加这一行
}

android {
    namespace = "com.example.unioncash"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.unioncash"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val nav_version = "2.5.3" // or the latest version

    // Navigation component dependencies
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    // 添加 Retrofit 和相关依赖项
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 添加 OkHttp 和相关依赖项
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // 添加你提到的新的依赖项
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")  // 版本和上面的4.9.3不冲突，保持最新即可
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.glide)
    implementation(libs.recyclerview)
    implementation(libs.lifecycle.extensions)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // 新增 ZXing 的依赖项
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.google.zxing:android-core:3.3.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")  // 使用双引号添加 ZXing 的依赖

    // 添加新的依赖项
    implementation(libs.google.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)

    // 添加 SwipeRefreshLayout 和 RecyclerView 依賴項
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // 添加 OkHttp 和 kotlinx-coroutines 依赖
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    // BiometricPrompt 依赖
    implementation("androidx.biometric:biometric:1.1.0")

    // 添加 Logging Interceptor 和 Gson
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.2")
    implementation("com.google.code.gson:gson:2.8.8")

    configurations.all {
        resolutionStrategy {
            force("groupId:artifactId:version")}
    }
}
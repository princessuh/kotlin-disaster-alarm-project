plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // ⬇️ 새로 추가 – Kotlin 2.0 전용 Compose Compiler 플러그인
    alias(libs.plugins.kotlin.compose)      // ← toml에 정의(방법은 아래 설명)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.disasteralert"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.disasteralert"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    /** Compose ON (plugin이 컴파일러 버전 자동 매칭) */
    buildFeatures { compose = true }

    /* composeOptions 블록은 Kotlin 2.0+ 에서는 필요 없음 */

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17   // 🔄 17
        targetCompatibility = JavaVersion.VERSION_17

        isCoreLibraryDesugaringEnabled = true

    }
    kotlinOptions { jvmTarget = "17" }                 // 🔄
}

dependencies {
    // — AndroidX & 기본 라이브러리 —
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // — 지도·위치 —
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.android.gms:play-services-maps:17.0.0")
    implementation ("com.google.android.gms:play-services-location:17.0.0")
    implementation ("com.google.maps.android:android-maps-utils:2.2.3")

    // — 네트워킹 —
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // — Firebase (BoM) —
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // — Jetpack Compose (BOM) —
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // — 네비게이션 컴포넌트 —
    implementation ("com.google.android.material:material:1.9.0")

    // 플로팅 버튼
    implementation ("com.google.android.material:material:1.9.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // — 테스트 —
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}

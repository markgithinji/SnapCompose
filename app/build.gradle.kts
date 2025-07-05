plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
    alias(libs.plugins.baselineprofile)
}

val unsplashApiKey: String = project.findProperty("UNSPLASH_API_KEY") as? String
    ?: throw IllegalStateException("UNSPLASH_API_KEY is missing. Add it to local.properties")

android {
    namespace = "com.example.composegallery"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.composegallery"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "UNSPLASH_API_KEY", "\"$unsplashApiKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

hilt {
    // Disables the aggregating task to work around a known issue with JavaPoet:
    // "Unable to find method 'java.lang.String com.squareup.javapoet.ClassName.canonicalName()'"
    enableAggregatingTask = false
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktlint("1.2.1")

        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint()
    }
}

dependencies {

    // Kotlin & Core Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.serialization.json)

// Lifecycle & Activity
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

// Compose BOM
    implementation(platform(libs.androidx.compose.bom))

// Compose UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.profileinstaller)
    "baselineProfile"(project(":baselineprofile"))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

// Material Design
    implementation(libs.androidx.material3)
    implementation(libs.material3) // e.g., LoadingIndicator, PullToRefreshBox
    implementation(libs.androidx.material.icons.extended)

// Navigation
    implementation(libs.androidx.navigation.compose)

// Hilt & DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

// Coil (Image loading)
    implementation(libs.coil.compose)

// Retrofit & Serialization
    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)

// Timber (Logging)
    implementation(libs.jakewharton.timber)

// Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

// Room (Database)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

// ConstraintLayout
    implementation(libs.androidx.constraintlayout.compose)

// Accompanist
    implementation(libs.accompanist.swiperefresh)

// Misc UI Utilities
    implementation(libs.compose.shimmer)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.core.splashscreen)

// Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation (libs.ui.test.manifest)

}
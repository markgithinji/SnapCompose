import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
    alias(libs.plugins.baselineprofile)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

val unsplashApiKey: String = (project.findProperty("UNSPLASH_API_KEY") as? String)
    ?: localProperties.getProperty("UNSPLASH_API_KEY")
    ?: throw IllegalStateException("UNSPLASH_API_KEY is missing. Add it to local.properties")

android {
    namespace = "com.example.composegallery"
    compileSdk = 37

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.composegallery"
        minSdk = 24
        targetSdk = 37
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

hilt {
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
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    implementation(project(":feature:home"))
    implementation(project(":feature:search"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:photodetail"))

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
    implementation(libs.okhttp.logging.interceptor)

// Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

// Room (Database)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.paging)

// ConstraintLayout
    implementation(libs.androidx.constraintlayout.compose)

// Misc UI Utilities
    implementation(libs.compose.shimmer)
    implementation(libs.androidx.material3.window.sizeclass)
    implementation(libs.androidx.core.splashscreen)

// Testing
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation (libs.androidx.paging.testing)


    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.mockito.kotlin)
}
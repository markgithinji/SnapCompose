import java.util.Properties
import java.io.FileInputStream

plugins {
    id("snap.android.library")
    id("snap.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

val unsplashApiKey: String = (project.findProperty("UNSPLASH_API_KEY") as? String)
    ?: localProperties.getProperty("UNSPLASH_API_KEY")
    ?: ""

android {
    namespace = "com.example.composegallery.core.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "UNSPLASH_API_KEY", "\"$unsplashApiKey\"")
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:database"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.serialization.json)
    
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.okhttp.logging.interceptor)
    
    // Paging
    implementation(libs.androidx.paging.runtime)
    
    // Coil
    implementation(libs.coil.compose)
    
    // Timber
    implementation(libs.jakewharton.timber)

    testImplementation(project(":core:testing"))
    testImplementation(libs.truth)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.mockwebserver)
}

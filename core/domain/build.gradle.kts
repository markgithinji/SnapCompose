plugins {
    id("snap.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.composegallery.core.domain"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.paging.common)
}

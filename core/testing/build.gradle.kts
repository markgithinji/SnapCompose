plugins {
    id("snap.android.library")
}

android {
    namespace = "com.example.composegallery.core.testing"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    
    implementation(libs.androidx.paging.common)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.truth)
}

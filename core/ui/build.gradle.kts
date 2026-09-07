plugins {
    id("snap.android.library")
    id("snap.android.compose")
    id("snap.android.hilt")
}

android {
    namespace = "com.example.composegallery.core.ui"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material3.window.sizeclass)
    
    // Coil
    implementation(libs.coil.compose)
    
    // Shimmer
    implementation(libs.compose.shimmer)
    
    // Paging
    implementation(libs.androidx.paging.compose)
    
    // Splashscreen
    implementation(libs.androidx.core.splashscreen)

    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
}

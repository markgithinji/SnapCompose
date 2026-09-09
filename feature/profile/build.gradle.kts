plugins {
    id("snap.android.feature")
}

android {
    namespace = "com.example.composegallery.feature.profile"
}

dependencies {
    implementation(project(":core:network"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material.icons.extended)
    
    // Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // Shimmer
    implementation(libs.compose.shimmer)

    testImplementation(project(":core:testing"))
    testImplementation(libs.androidx.paging.common)
    testImplementation(libs.truth)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
}

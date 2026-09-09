plugins {
    id("snap.android.feature")
}

android {
    namespace = "com.example.composegallery.feature.photodetail"
}

dependencies {
    implementation(project(":core:network"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material.icons.extended)
    
    // Coil
    implementation(libs.coil.compose)

    testImplementation(project(":core:testing"))
    // Paging
    testImplementation(libs.androidx.paging.common)

    testImplementation(libs.truth)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
}

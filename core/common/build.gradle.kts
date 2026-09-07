plugins {
    id("snap.android.library")
    id("snap.android.hilt")
}

android {
    namespace = "com.example.composegallery.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.jakewharton.timber)
    implementation(libs.retrofit)
}

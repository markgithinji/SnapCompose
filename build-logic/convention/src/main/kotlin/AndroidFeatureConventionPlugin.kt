import com.snap.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("snap.android.library")
                apply("snap.android.compose")
                apply("snap.android.hilt")
            }

            dependencies {
                "implementation"(project(":core:domain"))
                "implementation"(project(":core:common"))
                "implementation"(project(":core:ui"))

                "implementation"(libs.findLibrary("androidx-material3").get())
                "implementation"(libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
                "implementation"(libs.findLibrary("androidx-activity-compose").get())
                "implementation"(libs.findLibrary("androidx-hilt-navigation-compose").get())
            }
        }
    }
}

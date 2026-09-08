import com.android.build.api.dsl.LibraryExtension
import com.snap.convention.configureKotlinAndroid
import com.snap.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
            }

            dependencies {
                "androidTestImplementation"(libs.findLibrary("androidx-test-runner").get())
            }
        }
    }
}

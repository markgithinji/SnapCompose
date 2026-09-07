import com.android.build.api.dsl.LibraryExtension
import com.snap.convention.configureAndroidCompose
import com.snap.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)

            dependencies {
                "implementation"(libs.findLibrary("androidx-material3").get())
            }
        }
    }
}

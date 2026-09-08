import com.android.build.api.dsl.ApplicationExtension
import com.snap.convention.configureAndroidCompose
import com.snap.convention.configureKotlinAndroid
import com.snap.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = ProjectConfig.targetSdk
                configureAndroidCompose(this)
            }

            dependencies {
                "androidTestImplementation"(libs.findLibrary("androidx-test-runner").get())
            }
        }
    }
}

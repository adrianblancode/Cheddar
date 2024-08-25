import co.adrianblan.convention.configureAndroidCompose
import co.adrianblan.convention.configureKotlinAndroid
import co.adrianblan.convention.libs
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure


class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure<ApplicationExtension> {
                defaultConfig.targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
                configureKotlinAndroid(this)
                configureAndroidCompose(this)
            }
        }
    }
}
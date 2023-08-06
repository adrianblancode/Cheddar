package co.adrianblan.cheddar

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import co.adrianblan.domain.CustomTabsLauncher
import co.adrianblan.ui.AppTheme
import co.adrianblan.ui.RootView
import co.adrianblan.ui.utils.isNightModeActive
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var customTabsLauncher: CustomTabsLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setupSystemUi()

        setContent {
            AppTheme {
                RootView {
                    Navigation(customTabsLauncher)
                }
            }
        }
    }

    private fun setupSystemUi() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = !isNightModeActive()
        controller.isAppearanceLightNavigationBars = !isNightModeActive()
    }
}
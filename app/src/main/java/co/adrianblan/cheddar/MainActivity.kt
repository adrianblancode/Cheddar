package co.adrianblan.cheddar

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import co.adrianblan.domain.CustomTabsLauncher
import co.adrianblan.ui.AppTheme
import co.adrianblan.ui.InsetsWrapper
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

        setupSystemUi()

        setContent {
            AppTheme {
                InsetsWrapper(window.decorView) {
                    RootView {
                        Navigation(customTabsLauncher)
                    }
                }
            }
        }
    }

    private fun setupSystemUi() {
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        if (!resources.isNightModeActive()) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
    }
}
package co.adrianblan.cheddar

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import co.adrianblan.ui.AppTheme
import co.adrianblan.ui.InsetsWrapper
import co.adrianblan.ui.extensions.Orientation
import co.adrianblan.ui.extensions.isNightModeActive
import co.adrianblan.ui.extensions.orientation

class MainActivity : AppCompatActivity() {

    private val rootViewModel : RootViewModel by viewModels()
    private val rootComposer: RootComposer get() = rootViewModel.rootComposer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentView: View = window.decorView

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        // Only hide nav bar in portrait mode
        if (resources.orientation() == Orientation.PORTRAIT) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

        if (!resources.isNightModeActive()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }

        setContent {
            AppTheme {
                InsetsWrapper(contentView) {
                    rootComposer.composeView()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!rootComposer.onBackPressed()) {
            super.onBackPressed()
        }
    }
}
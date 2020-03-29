package co.adrianblan.cheddar

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import co.adrianblan.ui.AppTheme
import co.adrianblan.ui.InsetsWrapper
import co.adrianblan.ui.extensions.isNightModeActive
import co.adrianblan.ui.node.Node

class MainActivity : AppCompatActivity() {

    private val rootViewModel : RootViewModel by viewModels()
    private val rootNode: Node<*> get() = rootViewModel.rootNode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentView: View = window.decorView

        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

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
                    rootNode.render()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!rootNode.onBackPressed()) {
            super.onBackPressed()
        }
    }
}
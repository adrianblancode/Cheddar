package co.adrianblan.cheddar

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import co.adrianblan.cheddar.di.DaggerRootComponent
import co.adrianblan.cheddar.utils.appComponent
import co.adrianblan.matryoshka.node.AnyNode
import co.adrianblan.matryoshka.node.nodeFactory
import co.adrianblan.matryoshka.root.createVmRootNode
import co.adrianblan.ui.AppTheme
import co.adrianblan.ui.InsetsWrapper
import co.adrianblan.ui.RootView
import co.adrianblan.ui.utils.isNightModeActive

class MainActivity : AppCompatActivity() {

    private val rootNode: AnyNode by createVmRootNode(
        nodeFactory { savedState, nodeStore ->
            DaggerRootComponent.factory()
                .build(application.appComponent)
                .rootComponentFactory()
                .build()
                .storyNavigationNodeFactory()
                .create(
                    savedState = savedState,
                    nodeStore = nodeStore
                )
        }
    )

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
                    RootView {
                        rootNode.render()
                    }
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
package co.adrianblan.cheddar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import co.adrianblan.cheddar.di.DaggerRootComponent
import co.adrianblan.cheddar.extensions.appComponent
import co.adrianblan.common.ui.AppTheme
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var rootComposer: RootComposer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rootComposer =
            DaggerRootComponent.builder()
                .appComponent(appComponent)
                .build()
                .rootComposer()

        setContent {
            AppTheme {
                rootComposer.composeView()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO
    }

    override fun onBackPressed() {
        if (!rootComposer.onBackPressed()) {
            super.onBackPressed()
        }
    }
}
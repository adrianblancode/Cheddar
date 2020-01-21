package co.adrianblan.cheddar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import co.adrianblan.cheddar.di.DaggerRootComponent
import co.adrianblan.cheddar.extensions.appComponent
import co.adrianblan.common.ParentScope
import co.adrianblan.ui.AppTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class MainActivity : AppCompatActivity() {

    private lateinit var rootComposer: RootComposer
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rootComposer =
            DaggerRootComponent.factory()
                .build(
                    parentScope = ParentScope.of(scope),
                    appComponent = appComponent
                )
                .rootComposer()

        setContent {
            AppTheme {
                rootComposer.composeView()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // if (!isChangingConfigurations)
        scope.cancel()
    }

    override fun onBackPressed() {
        if (!rootComposer.onBackPressed()) {
            super.onBackPressed()
        }
    }
}
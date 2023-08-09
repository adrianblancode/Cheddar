package co.adrianblan.cheddar

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import co.adrianblan.domain.CustomTabsLauncher
import co.adrianblan.ui.AppTheme
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
        WindowInsetsControllerCompat(window, window.decorView)
            .apply {
                isAppearanceLightStatusBars = !isNightModeActive()
                isAppearanceLightNavigationBars = !isNightModeActive()
            }

        

        setContent {
            AppTheme {
                Surface {
                    Box {
                        Navigation(customTabsLauncher)

                        Surface(
                            color = MaterialTheme.colorScheme.scrim,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .windowInsetsBottomHeight(WindowInsets.navigationBars)
                        ) {}
                    }
                }
            }
        }
    }
}
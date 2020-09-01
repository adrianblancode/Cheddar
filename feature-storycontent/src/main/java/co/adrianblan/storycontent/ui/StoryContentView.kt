package co.adrianblan.storycontent.ui

import android.net.Uri
import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp
import co.adrianblan.ui.InsetsAmbient

@Composable
fun StoryContentView(url: Uri, webContext: WebContext) {
    val insets = InsetsAmbient.current

    with(DensityAmbient.current) {
        Box(
            modifier = Modifier.padding(
                top = insets.top.toDp(),
                bottom = insets.bottom.toDp(),
                start = insets.left.toDp(),
                end = insets.right.toDp()
            )
        ) {
            Scaffold(
                topBar = {
                    Text(
                        text = url.toString(),
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.preferredHeight(56.dp)
                    )
                }
            ) {
                WebComponent(url = url.toString(), webContext = webContext)
            }
        }
    }
}
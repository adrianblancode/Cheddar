package co.adrianblan.common.ui

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Container
import androidx.ui.layout.Padding
import androidx.ui.material.MaterialTheme

@Composable
fun ErrorView() {
    Container(expanded = true) {
        Padding(padding = 16.dp) {
            Text(
                text = "Error",
                style = (+MaterialTheme.typography()).h6
            )
        }
    }
}
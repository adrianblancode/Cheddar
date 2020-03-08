package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp

@Composable
fun LoadingView() {
    Container(expanded = true, padding = EdgeInsets(16.dp)) {
        Text(
            text = "Loading…",
            style = (MaterialTheme.typography()).h6
        )
    }
}
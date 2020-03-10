package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.unit.dp

@Composable
fun ErrorView() {
    Container(expanded = true, padding = EdgeInsets(16.dp)) {
        Text(
            text = stringResource(id = R.string.error_title),
            style = MaterialTheme.typography().h6
        )
    }
}
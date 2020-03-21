package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.LayoutAlign
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp

@Preview
@Composable
fun ErrorView() {
    Container(expanded = true, padding = EdgeInsets(32.dp)) {
        Text(
            text = stringResource(id = R.string.error_title),
            style = MaterialTheme.typography().h6.copy(textAlign = TextAlign.Center),
            modifier = LayoutAlign.Center
        )
    }
}
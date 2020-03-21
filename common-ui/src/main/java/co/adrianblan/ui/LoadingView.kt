package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp

@Preview
@Composable
fun LoadingView(
    modifier: Modifier = Modifier.None
) {
    Container(
        expanded = true,
        padding = EdgeInsets(32.dp),
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.loading_title),
            style = MaterialTheme.typography().h6.copy(textAlign = TextAlign.Center),
            modifier = LayoutAlign.Center
        )
    }
}
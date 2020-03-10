package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp

@Composable
fun LoadingView() {
    Container(expanded = true, padding = EdgeInsets(16.dp)) {
        Text(
            text = stringResource(id = R.string.loading_title),
            style = MaterialTheme.typography().h6
        )
    }
}
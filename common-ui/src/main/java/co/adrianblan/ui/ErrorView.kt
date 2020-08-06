package co.adrianblan.ui

import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview

@Preview
@Composable
fun ErrorView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        padding = 32.dp,
        gravity = ContentGravity.Center
    ) {
        Text(
            text = stringResource(id = R.string.error_title),
            style = MaterialTheme.typography.h6.copy(textAlign = TextAlign.Center)
        )
    }
}
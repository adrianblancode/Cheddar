package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.wrapContentSize
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp

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
package co.adrianblan.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp

@Composable
private fun CollapsingToolbar(
    scrollState: ScrollState,
    minHeight: Dp,
    maxHeight: Dp,
    toolbarContent: @Composable (collapsedFraction: () -> Float) -> Unit
) {

    val density = LocalDensity.current
    val totalCollapseDistance: Dp = maxHeight - minHeight

    val collapsedFraction: Float by remember(density) {
        derivedStateOf {
                // 1f is fully collapsed
                with(density) {
                    minOf(scrollState.value.toDp() / totalCollapseDistance, 1f)
                }
        }
    }

    val height: Dp by remember {
        derivedStateOf {
            minHeight + (maxHeight - minHeight) * (1f - collapsedFraction)
        }
    }
    val elevation by remember {
        derivedStateOf {
            lerp(0.dp, 0.5.dp, collapsedFraction)
        }
    }

    val collapsedFractionBlock = remember {
        { collapsedFraction }
    }

    Surface(
        color = MaterialTheme.colorScheme.scrim,
        shadowElevation = elevation
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .requiredHeight(height)
        ) {
            toolbarContent(collapsedFractionBlock)
        }
    }
}

// Collapsing toolbar with body content drawn behind it
@Composable
fun CollapsingScaffold(
    scrollState: ScrollState,
    minHeight: Dp,
    maxHeight: Dp,
    toolbarContent: @Composable (collapsedFraction: () -> Float) -> Unit,
    bodyContent: @Composable () -> Unit
) {

    // Background color for body
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        bodyContent()
    }

    Box(
        // Prevent children being clickable from behind toolbar
        modifier = Modifier
            .clickable(enabled = false) {}
            .fillMaxWidth(),
        content = {
            CollapsingToolbar(
                scrollState = scrollState,
                minHeight = minHeight,
                maxHeight = maxHeight
            ) { collapsedFraction ->
                toolbarContent(collapsedFraction)
            }
        })
}
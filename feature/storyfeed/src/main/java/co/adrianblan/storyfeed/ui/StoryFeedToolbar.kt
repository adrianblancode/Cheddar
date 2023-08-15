package co.adrianblan.storyfeed.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import co.adrianblan.model.StoryType
import co.adrianblan.ui.AppTheme

@Composable
fun StoryFeedToolbar(
    collapsedFraction: Float,
    storyType: StoryType,
    onStoryTypeClick: (StoryType) -> Unit
) {

    var showStoryTypePopup by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            // Set min width to align popup in center
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(10.dp)
        ) {

            val typography = MaterialTheme.typography

            val headerTextSize by remember(collapsedFraction) {
                derivedStateOf {
                    lerp(
                        typography.headlineLarge.fontSize,
                        typography.headlineSmall.fontSize,
                        collapsedFraction
                    )
                }
            }

            StoryFeedHeader(
                headerTextSize = headerTextSize,
                storyType = storyType,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                showStoryTypePopup = true
            }

            if (showStoryTypePopup) {
                StoryTypePopup(
                    selectedStoryType = storyType,
                    onStoryTypeClick = { storyType ->
                        onStoryTypeClick(storyType)
                        showStoryTypePopup = false
                    },
                    onDismiss = {
                        showStoryTypePopup = false
                    }
                )
            }
        }

    }
}

@Composable
private fun StoryFeedHeader(
    storyType: StoryType,
    modifier: Modifier = Modifier,
    headerTextSize: TextUnit = MaterialTheme.typography.headlineLarge.fontSize,
    onClick: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(4.dp)
        ) {

            val title: String = stringResource(storyType.titleStringResource())

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.displaySmall
                        .copy(
                            fontSize = headerTextSize,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .padding(vertical = 1.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun StoryFeedToolbarPreview() {
    AppTheme {
        Box(modifier = Modifier.height(300.dp)) {
            StoryFeedToolbar(
                collapsedFraction = 1f,
                storyType = StoryType.TOP,
                onStoryTypeClick = {}
            )
        }
    }
}
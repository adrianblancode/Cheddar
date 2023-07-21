package co.adrianblan.storyfeed.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import co.adrianblan.model.StoryType

@Composable
fun StoryFeedToolbar(
    collapsedFraction: Float,
    height: Dp,
    storyType: StoryType,
    onStoryTypeClick: (StoryType) -> Unit
) {

    var showStoryTypePopup by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.height(height)
            .fillMaxWidth()
    ) {
        Box(
            // Set min width to align popup in center
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
        ) {

            val typography = MaterialTheme.typography

            val headerTextSize by remember(collapsedFraction) {
                derivedStateOf {
                    lerp(
                        typography.h4.fontSize,
                        typography.h6.fontSize,
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
fun StoryFeedHeader(
    storyType: StoryType,
    headerTextSize: TextUnit = MaterialTheme.typography.h6.fontSize,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.clickable(onClick = onClick)
                .padding(4.dp)
        ) {

            val title: String = stringResource(storyType.titleStringResource())

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.h4
                        .copy(
                            fontSize = headerTextSize,
                            color = MaterialTheme.colors.onPrimary
                        )
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    tint = MaterialTheme.colors.onBackground,
                    contentDescription = null
                )
            }
        }
    }
}
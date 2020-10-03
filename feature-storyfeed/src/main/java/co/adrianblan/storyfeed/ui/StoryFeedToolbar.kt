package co.adrianblan.storyfeed.ui

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import co.adrianblan.domain.StoryType

@Composable
fun StoryFeedToolbar(
    collapsedFraction: Float,
    height: Dp,
    storyType: StoryType,
    onStoryTypeClick: (StoryType) -> Unit
) {

    val showStoryTypePopup = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.preferredHeight(height)
            // Set min width to align popup in center
            .preferredWidthIn(min = storyTypePopupWidth)
            .padding(12.dp),
        alignment = Alignment.BottomCenter,
    ) {

        val headerTextSize =
            lerp(
                MaterialTheme.typography.h4.fontSize,
                MaterialTheme.typography.h6.fontSize,
                collapsedFraction
            )

        StoryFeedHeader(
            headerTextSize = headerTextSize,
            storyType = storyType
        ) {
            showStoryTypePopup.value = true
        }

        if (showStoryTypePopup.value) {
            StoryTypePopup(
                selectedStoryType = storyType,
                onStoryTypeClick = { storyType ->
                    onStoryTypeClick(storyType)
                    showStoryTypePopup.value = false
                },
                onDismiss = {
                    showStoryTypePopup.value = false
                }
            )
        }
    }
}

@Composable
fun StoryFeedHeader(
    storyType: StoryType,
    headerTextSize: TextUnit = MaterialTheme.typography.h6.fontSize,
    onClick: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent
    ) {
        Box(
            alignment = Alignment.Center,
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
                    asset = Icons.Default.ArrowDropDown,
                    tint = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}
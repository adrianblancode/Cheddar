package co.adrianblan.storyfeed.ui

import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowDropDown
import androidx.ui.material.ripple.ripple
import androidx.ui.res.stringResource
import androidx.ui.unit.Dp
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import androidx.ui.unit.lerp
import co.adrianblan.domain.StoryType

@Composable
fun StoryFeedToolbar(
    collapsedFraction: Float,
    height: Dp,
    storyType: StoryType,
    onStoryTypeClick: (StoryType) -> Unit
) {

    Box(
        padding = 12.dp,
        gravity = ContentGravity.BottomCenter,
        modifier = Modifier.preferredHeight(height)
            // Set min width to align popup in center
            .preferredWidthIn(minWidth = storyTypePopupWidth)
    ) {

        val headerTextSize =
            lerp(
                MaterialTheme.typography.h4.fontSize,
                MaterialTheme.typography.h6.fontSize,
                collapsedFraction
            )

        var showStoryTypePopup by mutableStateOf(false)

        // Seems like if statement does not automatically recompose on state change
        Recompose { recompose ->
            StoryFeedHeader(
                headerTextSize = headerTextSize,
                storyType = storyType
            ) {
                showStoryTypePopup = true
                recompose()
            }

            if (showStoryTypePopup) {
                StoryTypePopup(
                    selectedStoryType = storyType,
                    onStoryTypeClick = { storyType ->
                        onStoryTypeClick(storyType)
                        showStoryTypePopup = false
                        recompose()
                    },
                    onDismiss = {
                        showStoryTypePopup = false
                        recompose()
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
    onClick: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier.ripple(bounded = true)
                .clickable(onClick = onClick)
        ) {
            Box(
                gravity = ContentGravity.Center,
                padding = 4.dp
            ) {
                Row(verticalGravity = Alignment.CenterVertically) {

                    // TODO memo
                    val title = stringResource(storyType.titleStringResource())

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
}
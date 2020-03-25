package co.adrianblan.storyfeed

import androidx.compose.Composable
import androidx.compose.Recompose
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowDropDown
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Surface
import androidx.ui.res.stringResource
import androidx.ui.unit.Dp
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import androidx.ui.unit.lerp
import co.adrianblan.hackernews.StoryType
import co.adrianblan.ui.VectorImage

@Composable
fun StoryFeedToolbar(
    collapsedFraction: Float,
    height: Dp,
    storyType: StoryType,
    onStoryTypeClick: (StoryType) -> Unit
) {

    Container(
        padding = EdgeInsets(12.dp),
        alignment = Alignment.BottomLeft,
        modifier = LayoutHeight(height)
    ) {

        val headerTextSize =
            lerp(
                MaterialTheme.typography().h4.fontSize,
                MaterialTheme.typography().h6.fontSize,
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
    headerTextSize: TextUnit = MaterialTheme.typography().h6.fontSize,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent
    ) {
        Ripple(true) {
            Clickable(onClick = onClick) {
                Container(padding = EdgeInsets(4.dp)) {
                    Row {
                        val title = remember(storyType) {
                            stringResource(storyType.titleStringResource())
                        }

                        Text(
                            text = title,
                            style = MaterialTheme.typography().h4
                                .copy(
                                    fontSize = headerTextSize,
                                    color = MaterialTheme.colors().onPrimary
                                )
                        )
                        VectorImage(
                            vector = Icons.Default.ArrowDropDown,
                            tint = MaterialTheme.colors().onBackground,
                            modifier = LayoutGravity.Center
                        )
                    }
                }
            }
        }
    }
}
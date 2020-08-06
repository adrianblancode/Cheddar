package co.adrianblan.storyfeed.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import co.adrianblan.domain.StoryType
import co.adrianblan.ui.AppTheme

internal val storyTypePopupWidth: Dp = 190.dp

@Composable
fun StoryTypePopup(
    selectedStoryType: StoryType,
    onStoryTypeClick: (StoryType) -> Unit,
    onDismiss: () -> Unit
) {

    Popup(
        isFocusable = true,
        onDismissRequest = onDismiss
    ) {
        // Popups require reapplication of the app theme
        AppTheme {
            Box(
                padding = 8.dp,
                modifier = Modifier.preferredWidthIn(maxWidth = storyTypePopupWidth)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colors.background,
                    elevation = 4.dp
                ) {
                    Column {
                        val storyTypes = remember { StoryType.values() }

                        storyTypes
                            .map { storyType ->

                                val isSelected = selectedStoryType == storyType

                                StoryTypePopupItem(
                                    storyType = storyType,
                                    isSelected = isSelected
                                ) {
                                    onStoryTypeClick(storyType)
                                    onDismiss()
                                }
                            }
                    }
                }
            }
        }
    }
}

@Composable
fun StoryTypePopupItem(
    storyType: StoryType,
    isSelected: Boolean,
    onClick: (StoryType) -> Unit
) {
    Box(
        gravity = ContentGravity.Center,
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = { onClick(storyType) })
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {

            Text(
                text = stringResource(storyType.titleStringResource()),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.weight(1f)
                    .padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = 16.dp,
                        bottom = 12.dp
                    )
            )

            if (isSelected) {
                Box(
                    modifier = Modifier.gravity(Alignment.CenterVertically),
                    paddingEnd = 12.dp,
                    paddingTop = 2.dp
                ) {
                    Icon(
                        asset = Icons.Default.Check,
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.gravity(Alignment.CenterVertically)
                            .preferredSize(32.dp)
                    )
                }
            }
        }
    }
}
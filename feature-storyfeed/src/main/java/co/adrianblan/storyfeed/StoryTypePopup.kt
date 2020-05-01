package co.adrianblan.storyfeed

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.DropdownPopup
import androidx.ui.core.Modifier
import androidx.ui.core.PopupProperties
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Check
import androidx.ui.material.ripple.ripple
import androidx.ui.res.stringResource
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import co.adrianblan.hackernews.StoryType
import co.adrianblan.ui.AppTheme

internal val storyTypePopupWidth: Dp = 190.dp

@Composable
fun StoryTypePopup(
    selectedStoryType: StoryType,
    onStoryTypeClick: (StoryType) -> Unit,
    onDismiss: () -> Unit
) {

    DropdownPopup(
        popupProperties = PopupProperties(true, onDismissRequest = onDismiss)
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
    Clickable(
        onClick = { onClick(storyType) },
        modifier = Modifier.ripple(bounded = true)
    ) {
        Box(
            gravity = ContentGravity.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {

                Text(
                    text = stringResource(storyType.titleStringResource()),
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.weight(1f) +
                            Modifier.padding(
                                start = 12.dp,
                                end = 12.dp,
                                top = 16.dp,
                                bottom = 12.dp
                            )
                )

                if (isSelected) {
                    Box(
                        modifier  = Modifier.gravity(Alignment.CenterVertically),
                        paddingEnd = 12.dp,
                        paddingTop = 2.dp
                    ) {
                        Icon(
                            asset = Icons.Default.Check,
                            tint = MaterialTheme.colors.secondary,
                            modifier = Modifier.gravity(Alignment.CenterVertically) +
                                    Modifier.preferredSize(32.dp)
                        )
                    }
                }
            }
        }
    }
}
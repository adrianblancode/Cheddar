package co.adrianblan.storyfeed

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.DropdownPopup
import androidx.ui.core.PopupProperties
import androidx.ui.core.Text
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Check
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Surface
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import co.adrianblan.hackernews.StoryType
import co.adrianblan.ui.AppTheme
import co.adrianblan.ui.VectorImage

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
            Container(
                padding = EdgeInsets(8.dp),
                constraints = DpConstraints(maxWidth = 190.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colors().background,
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
    Ripple(bounded = true) {
        Clickable(onClick = { onClick(storyType) }) {
            Container(modifier = LayoutWidth.Fill + LayoutAlign.BottomLeft) {
                Row(arrangement = Arrangement.SpaceBetween) {

                    Text(
                        text = stringResource(storyType.titleStringResource()),
                        style = MaterialTheme.typography().subtitle1,
                        modifier = LayoutFlexible(1f) +
                                LayoutPadding(
                                    left = 12.dp,
                                    top = 16.dp,
                                    bottom = 12.dp,
                                    right = 12.dp
                                )
                    )

                    if (isSelected) {
                        Container(
                            modifier = LayoutGravity.Center,
                            padding = EdgeInsets(right = 12.dp, top = 2.dp)
                        ) {
                            VectorImage(
                                vector = Icons.Default.Check,
                                tint = MaterialTheme.colors().secondary,
                                modifier = LayoutGravity.Center +
                                        LayoutSize(32.dp, 32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
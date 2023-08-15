package co.adrianblan.storyfeed.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import co.adrianblan.model.StoryType
import co.adrianblan.ui.AppTheme

internal val storyTypePopupWidth: Dp = 190.dp

@Composable
fun StoryTypePopup(
    selectedStoryType: StoryType,
    onStoryTypeClick: (StoryType) -> Unit,
    onDismiss: () -> Unit
) {

    val popupTopOffsetPx = with(LocalDensity.current) { 40.dp.toPx().toInt() }

    Popup(
        properties = PopupProperties(focusable = true),
        onDismissRequest = onDismiss,
        offset = IntOffset(0, popupTopOffsetPx)
    ) {
        // Popups require reapplication of the app theme
        AppTheme {
            Box(
                modifier = Modifier
                    .widthIn(max = storyTypePopupWidth)
                    .padding(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.background,
                    shadowElevation = 4.dp
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
private fun StoryTypePopupItem(
    storyType: StoryType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {

            Text(
                text = stringResource(storyType.titleStringResource()),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = 16.dp,
                        bottom = 12.dp
                    )
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(
                            end = 12.dp,
                            top = 2.dp
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        tint = MaterialTheme.colorScheme.secondary,
                        // .align(Alignment.CenterVertically)
                        modifier = Modifier.size(32.dp),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun StoryTypePopupItemPreview() {
    AppTheme {
        StoryTypePopupItem(storyType = StoryType.TOP, isSelected = true, onClick = {})
    }
}
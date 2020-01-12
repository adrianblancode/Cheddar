package co.adrianblan.cheddar.feature.stories

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.*
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.Ripple
import androidx.ui.res.imageResource
import androidx.ui.res.stringResource
import co.adrianblan.cheddar.R

@Composable
fun StoriesView(storiesViewState: StoriesViewState) {
    FlexColumn {
        inflexible {
            TopAppBar(
                title = {
                    Text(
                        text = +stringResource(R.string.app_name),
                        style = (+MaterialTheme.typography()).h6
                    )
                }
            )
        }
        expanded(1f) {
            VerticalScroller {
                Column {
                    storiesViewState.stories.forEach { story ->
                        Padding(left = 16.dp, right = 16.dp, top = 8.dp, bottom = 8.dp) {
                            Text(
                                text = story.toString(),
                                style = (+MaterialTheme.typography()).h6
                            )
                        }
                    }
                }
            }
        }
    }
    Align(alignment = Alignment.BottomRight) {
        Container(modifier = Spacing(bottom = 16.dp, right = 8.dp)) {
            Ripple(bounded = true) {
                FloatingActionButton(
                    icon = +imageResource(android.R.drawable.ic_menu_search),
                    color = (+MaterialTheme.colors()).secondary,
                    onClick = {}
                )
            }
        }
    }
}
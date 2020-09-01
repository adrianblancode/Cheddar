package co.adrianblan.storycontent

import android.net.Uri
import androidx.compose.runtime.Composable
import co.adrianblan.matryoshka.node.Node
import co.adrianblan.storycontent.ui.StoryContentView
import co.adrianblan.storycontent.ui.WebContext
import javax.inject.Inject

class StoryContentNode
@Inject constructor(
    private val url: Uri
) : Node() {

    private val webContext = WebContext()

    @Composable
    override fun render() {
        StoryContentView(url = url, webContext = webContext)
    }

    override fun onBackPressed(): Boolean {
        if (webContext.canGoBack()) {
            webContext.goBack()
            return true
        }
        return false
    }
}
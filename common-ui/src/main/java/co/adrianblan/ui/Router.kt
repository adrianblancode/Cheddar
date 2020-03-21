package co.adrianblan.ui

import androidx.compose.Model
import androidx.compose.frames.ModelList
import androidx.compose.frames.modelListOf

interface Router {
    val composers: List<Composer>
    fun onBackPressed(): Boolean
}

@Model
class StackRouter private constructor(
    override val composers: ModelList<Composer>
): Router {

    fun push(composer: Composer) =
        composers.add(composer)

    private fun pop() {
        composers.removeAt(composers.size - 1)
            .apply {
                detach()
            }
    }

    private fun canPop() = composers.size > 1

    override fun onBackPressed(): Boolean {
        // Iterate through stack of children, see if any children handle it
        composers.reversed()
            .forEach { composer ->
                if (composer.onBackPressed()) {
                    return true
                }
            }

        return if (canPop()) {
            pop()
            true
        } else false
    }

    companion object {
        fun of(initialComposer: Composer): StackRouter =
            StackRouter(modelListOf(initialComposer))
    }
}
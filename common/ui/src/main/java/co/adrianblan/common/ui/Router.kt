package co.adrianblan.common.ui

import androidx.compose.Model
import androidx.compose.frames.ModelList
import androidx.compose.frames.modelListOf

interface Router {
    fun activeComposer(): Composer
    fun onBackPressed(): Boolean
}

@Model
class StackRouter private constructor(
    private var composers: ModelList<Composer>
): Router {

    override fun activeComposer(): Composer = composers.last()

    fun push(composer: Composer) =
        composers.add(composer)

    private fun pop() {
        composers.removeAt(composers.size - 1)
    }

    private fun canPop() = composers.size >= 1

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
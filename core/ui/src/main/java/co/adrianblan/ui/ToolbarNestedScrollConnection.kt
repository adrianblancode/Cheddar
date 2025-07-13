package co.adrianblan.ui

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

/** Coordinates scroll between toolbar and LazyColumn */
class ToolbarNestedScrollConnection(
    private val maxNestedScrollHeightPx: Float
) : NestedScrollConnection {

    var currentOffset by mutableFloatStateOf(0f)
        private set

    private var currentFling: AnimationState<*, *>? = null

    // Returns progress from 0 to 1
    fun progress(): Float {
        if (maxNestedScrollHeightPx == 0f) return 0f
        return (-currentOffset / maxNestedScrollHeightPx).coerceIn(0f, 1f)
    }

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        currentFling = null
        val delta = available.y

        // Consume initial scroll when scrolling content down
        return if (delta < 0 && currentOffset > -maxNestedScrollHeightPx) {
            val consumed = consumeScroll(delta)
            Offset(x = 0f, y = consumed)
        } else Offset.Companion.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        currentFling = null
        val delta = available.y

        // Consume last scroll when scrolling content up
        return if (delta > 0 && currentOffset < 0) {
            val consumed = consumeScroll(delta)
            Offset(x = 0f, y = consumed)
        } else Offset.Companion.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val velocity = available.y

        // When flinging content down, transfer fling to content
        if (velocity < 0 && currentOffset > -maxNestedScrollHeightPx) {
            fling(velocity)
        }
        // Don't slow down current fling
        return Velocity.Companion.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        val velocity = available.y

        // When flinging content up, transfer fling to toolbar
        return if (velocity > 0 && currentOffset < 0) {
            val consumed = fling(velocity)
            Velocity(x = 0f, y = consumed)
        } else Velocity.Companion.Zero
    }

    // Tries to consume scroll delta, returns amount consumed
    private fun consumeScroll(delta: Float): Float {
        val previousOffset = currentOffset
        currentOffset = (currentOffset + delta).coerceIn(-maxNestedScrollHeightPx, 0f)
        return currentOffset - previousOffset
    }

    // Tries to consume fling velocity, returns amount consumed
    private suspend fun fling(initialVelocity: Float): Float {
        val fling = AnimationState(
            initialValue = currentOffset,
            initialVelocity = initialVelocity
        )
        currentFling = fling

        var consumedVelocity = 0f

        fling.animateDecay(
            animationSpec = exponentialDecay(absVelocityThreshold = 10f)
        ) {
            if (
                currentOffset == 0f
                || currentOffset <= -maxNestedScrollHeightPx
                || fling != currentFling
            ) {
                cancelAnimation()
            }

            val delta = value - currentOffset
            val consumed = consumeScroll(delta)
            consumedVelocity = initialVelocity - velocity
        }

        return consumedVelocity
    }
}
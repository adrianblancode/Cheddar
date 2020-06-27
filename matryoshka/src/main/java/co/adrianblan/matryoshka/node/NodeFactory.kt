package co.adrianblan.matryoshka.node

import android.os.Parcelable

interface NodeFactory<out Node> {
    fun create(
        savedState: Parcelable?,
        nodeStore: NodeStore
    ): Node
}

fun <T : Node> nodeFactory(creator: (Parcelable?, NodeStore) -> T): NodeFactory<T> =
    object : NodeFactory<T> {
        override fun create(savedState: Parcelable?, nodeStore: NodeStore): T =
            creator(savedState, nodeStore)
    }
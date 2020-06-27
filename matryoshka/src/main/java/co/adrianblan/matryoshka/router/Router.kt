package co.adrianblan.matryoshka.router

import co.adrianblan.matryoshka.node.AnyNode
import co.adrianblan.matryoshka.node.NodeFactory
import co.adrianblan.matryoshka.node.NodeStore

/**
 * A Router is used by a parent Node to attach and detach child Nodes.
 * T is used as a key to create nodes.
 */
abstract class Router<T : Any> {

    protected abstract val nodeStore: NodeStore

    abstract fun T.nodeFactory(): NodeFactory<AnyNode>

}
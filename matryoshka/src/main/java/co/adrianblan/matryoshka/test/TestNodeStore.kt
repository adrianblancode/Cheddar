package co.adrianblan.matryoshka.test

import co.adrianblan.matryoshka.node.AnyNode
import co.adrianblan.matryoshka.node.NodeFactory
import co.adrianblan.matryoshka.node.NodeStore

/** Test node store which doesn't detach any children */
object TestNodeStore : NodeStore {

    override fun <T : AnyNode> child(key: String, childFactory: NodeFactory<T>): T =
        childFactory.create(null, TestNodeStore)

    override fun detach() {}

}
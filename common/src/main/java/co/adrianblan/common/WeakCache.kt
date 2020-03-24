package co.adrianblan.common

import java.util.*

class WeakCache<K : Any, V : Any> {
    private val cache = WeakHashMap<K, V>()

    fun put(key: K, value: V) {
        cache[key] = value
    }

    fun get(key: K): V? = cache[key]
}
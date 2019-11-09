package co.adrianblan.common


/**
 * A container object which may or may not contain a non-null value.
 *
 * You can access the value using the `value` property.
 *
 * You can also treat [Optional] as a stream by using [map], [flatMap] and [filter].
 */
sealed class Optional<out T : Any> {
    abstract val value: T?

    /**
     * Return `true` if there is a value present, otherwise `false`.
     *
     * This is equivalent of doing `this is Present` or `value != null`.
     *
     * Prefer using `optional is Present` over this property as it will smart cast the value.
     *
     * @return `true` if there is a value present, otherwise `false`
     */
    inline val isPresent: Boolean
        get() = this is Present

    /**
     * Returns the value of this optional or `null`.
     *
     * @return the Optional's [value].
     */
    abstract operator fun component1(): T?

    /**
     * If a value is present, and the value matches the given predicate,
     * return an [Optional] describing the value, otherwise return an
     * empty [Optional].
     *
     * @param predicate a predicate to apply to the value, if present
     * @return an [Optional] describing the value of this [Optional]
     *         if a value is present and the value matches the given predicate,
     *         otherwise an empty [Optional]
     */
    inline fun filter(predicate: (T) -> Boolean): Optional<T> =
        if (this is Present && predicate(value)) this else Empty

    /**
     * If a value is present, apply the provided mapping function to it,
     * and if the result is non-null, return an [Optional] describing the
     * result.  Otherwise return an empty [Optional].

     * @param U The type of the result of the mapping function
     * @param mapper a mapping function to apply to the value, if present
     * @return an [Optional] describing the result of applying a mapping
     *         function to the value of this [Optional], if a value is present,
     *         otherwise an empty [Optional]
     */
    inline fun <U : Any> map(mapper: (T) -> U?): Optional<U> = value?.let(mapper).toOptional()

    /**
     * If a value is present, apply the provided [Optional]-bearing
     * mapping function to it, return that result, otherwise return an empty
     * [Optional].  This method is similar to [map],
     * but the provided mapper is one whose result is already an [Optional],
     * and if invoked, `flatMap` does not wrap it with an additional
     * [Optional].
     *
     * @param U The type parameter to the [Optional] returned by the mapper
     * @param mapper a mapping function to apply to the value, if present
     *               the mapping function
     * @return the result of applying an [Optional]-bearing mapping
     *         function to the value of this [Optional], if a value is present,
     *         otherwise an empty [Optional]
     */
    inline fun <U : Any> flatMap(mapper: (T) -> Optional<U>): Optional<U> = value?.let(mapper) ?: empty()

    /**
     * Indicates whether some other object is "equal to" this Optional. The
     * other object is considered equal if:
     *  * it is also an [Optional] and;
     *  * both instances have no value present or;
     *  * the present values are "equal to" each other via `equals()`.
     *
     * @param other an object to be tested for equality
     * @return {code true} if the other object is "equal to" this object otherwise `false`
     */
    final override fun equals(other: Any?): Boolean =
        this === other || (other is Optional<*> && javaClass == other.javaClass && value == other.value)

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if
     * no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    final override fun hashCode(): Int = value.hashCode()

    /**
     * Returns a non-empty string representation of this Optional suitable for
     * debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     *
     * @return the string representation of this instance
     */
    abstract override fun toString(): String

    companion object {
        /**
         * Returns an empty [Optional] instance.  No value is present for this
         * [Optional].
         *
         * This function is useful when you want to receive an empty [Optional] of a certain type.
         * [Optional.Empty] can be used when the type does matter.
         *
         * @param T Type of the non-existent value
         * @return an empty [Optional]
         */
        fun <T : Any> empty(): Optional<T> = Empty

        /**
         * Returns an [Optional] with the specified value.
         *
         * @param T the class of the value
         * @param value the value of the optional, may be null
         * @return an `Opt  ional` with the value
         */
        operator fun <T : Any> invoke(value: T?): Optional<T> = if (value == null) Empty else Present(
            value
        )
    }

    /**
     * Represents an empty (null value) [Optional].
     *
     * There is only ever one instance of this object and its [value] always returns `null`.
     */
    object Empty : Optional<Nothing>() {
        override val value: Nothing? get() = null
        override operator fun component1(): Nothing? = null
        override fun toString() = "Optional.Empty"
    }

    /**
     * Represents a present (a non null value) [Optional].
     *
     * This objects [value] will always return non null.
     */
    class Present<out T : Any>(override val value: T) : Optional<T>() {
        override operator fun component1(): T = value
        override fun toString() = "Optional[$value]"
    }
}

fun <T : Any> T?.toOptional(): Optional<T> = Optional(this)
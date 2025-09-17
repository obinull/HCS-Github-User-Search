package dev.byto.hcsgus.util

fun Int?.orEmpty(): Int = this ?: 0
fun Int?.orZero(): Int = this ?: 0
fun Int?.orDefault(defaultValue: Int): Int = this ?: defaultValue
fun Int?.orThrow(exception: () -> Throwable): Int = this ?: throw exception()
fun Int?.orCalculate(calculation: () -> Int): Int = this ?: calculation()
fun Int.isPositive(): Boolean = this > 0
fun Int.isNegative(): Boolean = this < 0
fun Int.isZero(): Boolean = this == 0
fun Int.toFormattedString(format: String): String = String.format(format, this)
fun Int?.isValidId(): Boolean {
    return this != null && this != 0
}

fun Double?.orZero(): Double = this ?: 0.0
fun Float?.orZero(): Float = this ?: 0f
fun Long?.orZero(): Long = this ?: 0L
fun Boolean?.orZero(): Boolean = this ?: false
fun Boolean?.orFalse(): Boolean = this ?: false

fun String?.orEmptyDefaultPlaceholder(): String = this ?: "-"

// This function is for non-nullable Strings
fun String.orNull(): String? {
    return this.ifEmpty { null }
}

fun Int?.orNull(): Int? {
    return if (this.isNullOrZero()) null else this
}

fun Int?.isNullOrZero(): Boolean {
    return try {
        this == null || this == 0
    } catch (_: NullPointerException) {
        true
    }
}

// This function is for nullable Strings
@JvmName("orNullForNullableString") // Provide a distinct JVM name
fun String?.orNull(): String? {
    return if (this.isNullOrEmpty()) null else this
}

fun String?.equalsIn(vararg contains: String?): Boolean {
    if (this == null) return false
    for (s in contains) {
        if (s != null && s.equals(this, ignoreCase = true)) {
            return true
        }
    }
    return false
}

fun String.equalsIn(vararg strings: String, ignoreCase: Boolean = false): Boolean {
    return strings.any { this.equals(it, ignoreCase = ignoreCase) }
}

fun String.equalsIn(strings: Collection<String>, ignoreCase: Boolean = false): Boolean {
    return strings.any { this.equals(it, ignoreCase = ignoreCase) }
}

fun String?.toIntOrZero(): Int = this?.toIntOrNull() ?: 0

fun String?.toLongOrZero(): Long = this?.toLongOrNull() ?: 0L

@JvmName("toFloatOrZeroNullable")
fun String?.toFloatOrZero(): Float = this?.toFloatOrNull() ?: 0.0f

fun String.toFloatOrZero(): Float {
    return this.toFloatOrNull() ?: 0.0f
}

fun String.toDoubleOrZero(): Double {
    return this.toDoubleOrNull() ?: 0.0
}

fun String?.toFloatOrZeroExplicitBlank(): Float {
    if (this.isNullOrBlank()) {
        return 0.0f
    }
    return this.toFloatOrNull() ?: 0.0f // Then attempt conversion
}

fun <T : Any> T?.toStringOrNull(): String? {
    return this?.toString()
}

fun <T : Any> T?.toStringOrDefault(): String {
    return this?.toString() ?: "-"
}

fun StringBuilder.appendProperty(prefix: String, name: String, value: Any?) {
    if (this.length > prefix.length) {
        this.append(", ")
    }
    this.append(name).append("=")
    when (value) {
        is String -> this.append("'").append(value).append("'")
        is Int, is Long, is Float, is Double, is Boolean -> this.append(value) // Handle primitive types directly
        is List<*> -> {
            this.append("[")
            value.forEachIndexed { index, item ->
                if (index > 0) this.append(", ")
                // Recursively call toString() or a custom string function for list items
                this.append(item.toString())
            }
            this.append("]")
        }
        is Map<*, *> -> {
            this.append("{")
            value.entries.forEachIndexed { index, (key, item) ->
                if (index > 0) this.append(", ")
                this.append(key.toString()).append("=").append(item.toString())
            }
            this.append("}")
        }
        is Array<*> -> {
            this.append("[")
            value.forEachIndexed { index, item ->
                if (index > 0) this.append(", ")
                this.append(item.toString())
            }
            this.append("]")
        }
        null -> this.append("")
        else -> this.append(value.toString())
    }
}

/**
 * Checks if a nullable CharSequence is not null and not empty.
 * This is a more concise way of writing `charSequence.orEmpty().isNotEmpty()`.
 */
fun CharSequence?.isNotNullOrNotEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

/**
 * Checks if a nullable String is not null and not empty.
 * This is a more concise way of writing `string.orEmpty().isNotEmpty()`.
 */
fun String?.isNotNullOrNotEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

/**
 * Returns `true` if this nullable CharSequence is either `null` or empty.
 * Equivalent to `this.orEmpty().isEmpty()` or `this.isNullOrEmpty()`.
 */
fun CharSequence?.isNullOrActuallyEmpty(): Boolean {
    return this.isNullOrEmpty()
}

/**
 * Returns `true` if this nullable Collection is either `null` or empty.
 * Equivalent to `this.orEmpty().isEmpty()` or `this.isNullOrEmpty()`.
 */
fun <T> Collection<T>?.isNullOrActuallyEmpty(): Boolean {
    return this.isNullOrEmpty()
}

/**
 * Returns `true` if this nullable List is either `null` or empty.
 * Equivalent to `this.orEmpty().isEmpty()` or `this.isNullOrEmpty()`.
 */
fun <T> List<T>?.isNullOrActuallyEmpty(): Boolean {
    return this.isNullOrEmpty()
}

// You can add similar extensions for Map?, Array?, etc. if needed
/**
 * Returns `true` if this nullable Map is either `null` or empty.
 * Equivalent to `this.orEmpty().isEmpty()` or `this.isNullOrEmpty()`.
 */
fun <K, V> Map<K, V>?.isNullOrActuallyEmpty(): Boolean {
    return this.isNullOrEmpty()
}

/**
 * Returns `true` if this nullable CharSequence is not null and not empty.
 * Equivalent to `this.orEmpty().isNotEmpty()` or `!this.isNullOrEmpty()`.
 */
fun CharSequence?.isNotNullAndNotEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

/**
 * Returns `true` if this nullable Collection is not null and not empty.
 * Equivalent to `this.orEmpty().isNotEmpty()` or `!this.isNullOrEmpty()`.
 */
fun <T> Collection<T>?.isNotNullAndNotEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

// You can add similar extensions for Map?, Array?, etc. if needed
/**
 * Returns `true` if this nullable Map is not null and not empty.
 * Equivalent to `this.orEmpty().isNotEmpty()` or `!this.isNullOrEmpty()`.
 */
fun <K, V> Map<K, V>?.isNotNullAndNotEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

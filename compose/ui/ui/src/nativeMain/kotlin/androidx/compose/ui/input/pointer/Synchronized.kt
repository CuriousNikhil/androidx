@file:Suppress("UNUSED_PARAMETER")
package androidx.compose.ui.input.pointer


// TODO: Instead of multiple syncronized for different packages
// maybe have a common in the common compose.
// TODO: need to figure out this for native.
inline fun <R> synchronized(lock: Any, block: () -> R): R =
    error("implement native synchronized") // block()


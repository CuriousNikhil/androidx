@file:Suppress("UNUSED_PARAMETER")
package androidx.compose.animation.core

// TODO: need to figure out this for native.
inline fun <R> synchronized(lock: Any, block: () -> R): R = error("implement native synchronized") // block()


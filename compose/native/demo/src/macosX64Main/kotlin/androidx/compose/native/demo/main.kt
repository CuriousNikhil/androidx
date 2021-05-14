package androidx.compose.native.demo

import platform.AppKit.*

import androidx.compose.native.Window
/*
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
*/

fun main() {
    NSApplication.sharedApplication()
    createWindow("Compose/Native sample")
    NSApp?.run()
}

fun createWindow(title: String) {
    val window = Window(title) {
        /*
        var text by remember { mutableStateOf("Hello, World!") }

        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }

        */
    }
}
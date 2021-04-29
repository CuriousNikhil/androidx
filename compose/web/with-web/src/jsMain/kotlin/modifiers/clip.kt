/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.compose.common.ui.draw

import org.jetbrains.ui.ww.Modifier
import jetbrains.compose.common.shapes.Shape
import jetbrains.compose.common.shapes.CircleShape
import org.jetbrains.compose.web.ww.internal.castOrCreate
import androidx.compose.web.css.borderRadius
import androidx.compose.web.css.percent

actual fun Modifier.clip(shape: Shape): Modifier = castOrCreate().apply {
    when (shape) {
        CircleShape -> add {
            borderRadius(50.percent)
        }
    }
}
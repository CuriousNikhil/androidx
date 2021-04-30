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
package androidx.compose.foundation.layout.ww

import androidx.compose.ui.unit.ww.Dp
import org.jetbrains.ui.ww.Modifier
import org.jetbrains.compose.web.ww.internal.castOrCreate
import androidx.compose.web.css.marginTop
import androidx.compose.web.css.marginLeft
import androidx.compose.web.css.px

actual fun Modifier.offset(x: Dp, y: Dp): Modifier = castOrCreate().apply {
    add {
        marginLeft(x.value.px)
        marginTop(y.value.px)
    }
}
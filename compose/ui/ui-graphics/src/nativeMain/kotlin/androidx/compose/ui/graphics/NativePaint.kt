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

package androidx.compose.ui.graphics
/*
import org.jetbrains.skiko.skia.native.FilterQuality as SkijaFilterQuality
import org.jetbrains.skiko.skia.native.PaintMode as SkijaPaintMode
import org.jetbrains.skiko.skia.native.PaintStrokeCap as SkijaPaintStrokeCap
import org.jetbrains.skiko.skia.native.PaintStrokeJoin as SkijaPaintStrokeJoin
*/
// TODO: remove me.
class NativePaintSub

actual typealias NativePaint = NativePaintSub// org.jetbrains.skiko.skia.native.Paint

actual fun Paint(): Paint = error("Implement native Paint")// SkiaPaint()
/*
class SkiaPaint : Paint {
    internal val skia = org.jetbrains.skiko.skia.native.Paint()

    constructor() {
        filterQuality = FilterQuality.Medium
    }

    override fun asFrameworkPaint(): NativePaint = skia

    override var alpha: Float
        get() = Color(skia.color).alpha
        set(value) {
            skia.color = Color(skia.color).copy(alpha = value).toArgb()
        }

    override var isAntiAlias: Boolean
        get() = skia.isAntiAlias
        set(value) {
            skia.isAntiAlias = value
        }

    override var color: Color
        get() = Color(skia.color)
        set(color) {
            skia.color = color.toArgb()
        }

    override var blendMode: BlendMode = BlendMode.SrcOver
        set(value) {
            skia.blendMode = value.toSkija()
            field = value
        }

    override var style: PaintingStyle = PaintingStyle.Fill
        set(value) {
            skia.mode = value.toSkija()
            field = value
        }

    override var strokeWidth: Float
        get() = skia.strokeWidth
        set(value) {
            skia.strokeWidth = value
        }

    override var strokeCap: StrokeCap = StrokeCap.Butt
        set(value) {
            skia.strokeCap = value.toSkija()
            field = value
        }

    override var strokeJoin: StrokeJoin = StrokeJoin.Round
        set(value) {
            skia.strokeJoin = value.toSkija()
            field = value
        }

    override var strokeMiterLimit: Float = 0f
        set(value) {
            skia.strokeMiter = value
            field = value
        }

    override var filterQuality: FilterQuality = FilterQuality.None
        set(value) {
            skia.filterQuality = value.toSkija()
            field = value
        }

    override var shader: Shader? = null
        set(value) {
            skia.shader = value
            field = value
        }

    override var colorFilter: ColorFilter? = null
        set(value) {
            skia.colorFilter = value?.asDesktopColorFilter()
            field = value
        }

    override var pathEffect: PathEffect? = null
        set(value) {
            skia.pathEffect = (value as DesktopPathEffect?)?.asDesktopPathEffect()
            field = value
        }

    private fun PaintingStyle.toSkija() = when (this) {
        PaintingStyle.Fill -> SkijaPaintMode.FILL
        PaintingStyle.Stroke -> SkijaPaintMode.STROKE
    }

    private fun StrokeCap.toSkija() = when (this) {
        StrokeCap.Butt -> SkijaPaintStrokeCap.BUTT
        StrokeCap.Round -> SkijaPaintStrokeCap.ROUND
        StrokeCap.Square -> SkijaPaintStrokeCap.SQUARE
    }

    private fun StrokeJoin.toSkija() = when (this) {
        StrokeJoin.Miter -> SkijaPaintStrokeJoin.MITER
        StrokeJoin.Round -> SkijaPaintStrokeJoin.ROUND
        StrokeJoin.Bevel -> SkijaPaintStrokeJoin.BEVEL
    }

    private fun FilterQuality.toSkija() = when (this) {
        FilterQuality.None -> SkijaFilterQuality.NONE
        FilterQuality.Low -> SkijaFilterQuality.LOW
        FilterQuality.Medium -> SkijaFilterQuality.MEDIUM
        FilterQuality.High -> SkijaFilterQuality.HIGH
    }
}
*/
actual fun BlendMode.isSupported(): Boolean = true
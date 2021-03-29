import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

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



class Attr<T> {}
class TypeFoo

interface WithComposableInterface {
    @Composable
    fun Foo()
}

class WithComposableInterfaceImpl : WithComposableInterface {
    @Composable
    override fun Foo() {
        remember { 1000 }
    }
}

class WithComposableInConstructor(val l: @Composable () -> Unit)

class WithDefaultComposableInConstructor(
    val l: @Composable () -> Unit = @Composable { NoParameters() }
)

@Composable
fun NoParameters() {}

@Composable
fun WithIntDefaultValue(i: Int = 10) {}

@Composable
fun WithFooDefaultValue(i: TypeFoo = TypeFoo()) {}

@Composable
inline fun <T> FooExntensionLambdaTyped(a: Attr<T>.() -> Unit = {}) {
    val b = Attr<T>().a()
}

@Composable
inline fun <reified T> FooExntensionLambdaTypedReified(a: Attr<T>.() -> Unit = {}) {
    val b = Attr<T>().a()
}

@Composable
fun <T> FooExtensionLambdaTypedNoInl(a: Attr<T>.() -> Unit = {}) {
    val b = Attr<T>().a()
}
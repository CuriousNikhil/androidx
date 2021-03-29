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


import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember

class CallComposables {

    fun compose(content: @Composable () -> Unit) {}

    @Composable
    fun call() {
        NoParameters()

        FooExntensionLambdaTyped<String>()
        FooExntensionLambdaTyped<String> { }
        FooExtensionLambdaTypedNoInl<String>()
        FooExntensionLambdaTypedReified<TypeFoo>()
        FooExntensionLambdaTypedReified<TypeFoo> {}

        WithIntDefaultValue()
        WithIntDefaultValue(111)
        WithFooDefaultValue()
        WithFooDefaultValue(TypeFoo())

        WithComposableInterfaceImpl().Foo()

        WithComposableInConstructor {
            NoParameters()
        }.l()

        WithDefaultComposableInConstructor().l()

        remember<RememberObserver> {
            object : RememberObserver {
                override fun onRemembered() {}
                override fun onForgotten() {}
                override fun onAbandoned() {}
            }
        }

        anonymousImpl.Foo()

        val createdAnonymousImpl = createWithComposableInterface()
        createdAnonymousImpl.Foo()

        val localAnonymousImpl = object : WithComposableInterface {
            @Composable
            override fun Foo() {
                NoParameters()
            }
        }

        takesInWithComposableInterface(localAnonymousImpl)

        // TODO: this doesn't compile. Could not find local implementation for Foo$composable
        // localAnonymousImpl.Foo() -
    }

    private val anonymousImpl = object : WithComposableInterface {
        @Composable
        override fun Foo() {
            NoParameters()
        }
    }

    private fun createWithComposableInterface(): WithComposableInterface {
        return object : WithComposableInterface {
            @Composable
            override fun Foo() {
                NoParameters()
            }
        }
    }

    @Composable
    fun takesInWithComposableInterface(i: WithComposableInterface) {
        i.Foo()
    }

    fun callCompose() {
        compose {
            call()
        }
    }
}
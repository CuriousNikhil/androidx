/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.compose.compiler.plugins.kotlin.lower

import androidx.compose.compiler.plugins.kotlin.hasComposableAnnotation
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ParameterDescriptor
import org.jetbrains.kotlin.descriptors.PropertyGetterDescriptor
import org.jetbrains.kotlin.descriptors.PropertySetterDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.descriptors.WrappedClassConstructorDescriptor
import org.jetbrains.kotlin.ir.descriptors.WrappedDeclarationDescriptor
import org.jetbrains.kotlin.ir.descriptors.WrappedFunctionDescriptorWithContainerSource
import org.jetbrains.kotlin.ir.descriptors.WrappedPropertyGetterDescriptor
import org.jetbrains.kotlin.ir.descriptors.WrappedPropertySetterDescriptor
import org.jetbrains.kotlin.ir.descriptors.WrappedReceiverParameterDescriptor
import org.jetbrains.kotlin.ir.descriptors.WrappedSimpleFunctionDescriptor
import org.jetbrains.kotlin.ir.descriptors.WrappedTypeParameterDescriptor
import org.jetbrains.kotlin.ir.descriptors.WrappedValueParameterDescriptor
import org.jetbrains.kotlin.ir.util.DeepCopySymbolRemapper
import org.jetbrains.kotlin.ir.util.DescriptorsRemapper
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.types.KotlinType

/**
 * Symbol remapper which is aware of possible type signature change after changing parameters on
 * composable function types.
 *
 * For example, when function has a signature of:
 * ```
 * fun A(@Composable f: () -> Unit)
 * ```
 * it is going to be converted to incompatible signature of:
 * ```
 * fun A(f: (Composer<*>, Int) -> Unit)
 * ```
 * Same applies for receiver and return types.
 *
 * The conversion is applied to the two types of descriptors:
 * 1) Function and constructor descriptors that reference @Composable type, so that IR and
 *    descriptor signature matches;
 * 2) All wrapped descriptors. Underlying IR function will be copied anyway, so we need to ensure
 *    correct connection between such descriptor and IR function it represents.
 *
 * After remapping them, all new descriptors are bound to corresponding IR elements using
 * [WrappedComposableDescriptorPatcher] right after they are created
 * (see usages in [ComposerTypeRemapper])
 *
 * This conversion is only required for KLIB based linking, but can be used on JVM as well
 * for consistency.
 */
class ComposableSymbolRemapper : DeepCopySymbolRemapper(
    object : DescriptorsRemapper {
        override fun remapDeclaredConstructor(
            descriptor: ClassConstructorDescriptor
        ): ClassConstructorDescriptor =
            if (descriptor.isTransformed()) {
                WrappedClassConstructorDescriptor()
            } else {
                super.remapDeclaredConstructor(descriptor)
            }

        override fun remapDeclaredSimpleFunction(
            descriptor: FunctionDescriptor
        ): FunctionDescriptor =
            if (descriptor.isTransformed()) {
                when (descriptor) {
                    is PropertyGetterDescriptor -> WrappedPropertyGetterDescriptor()
                    is PropertySetterDescriptor -> WrappedPropertySetterDescriptor()
                    is WrappedFunctionDescriptorWithContainerSource -> {
                        WrappedFunctionDescriptorWithContainerSource()
                    }
                    else -> WrappedSimpleFunctionDescriptorWithSource(descriptor.source)
                }
            } else {
                super.remapDeclaredSimpleFunction(descriptor)
            }

        override fun remapDeclaredValueParameter(
            descriptor: ParameterDescriptor
        ): ParameterDescriptor =
            if (descriptor.isTransformed()) {
                when (descriptor) {
                    is ValueParameterDescriptor -> WrappedValueParameterDescriptor()
                    is ReceiverParameterDescriptor -> WrappedReceiverParameterDescriptor()
                    else -> super.remapDeclaredValueParameter(descriptor)
                }
            } else {
                super.remapDeclaredValueParameter(descriptor)
            }

        override fun remapDeclaredTypeParameter(
            descriptor: TypeParameterDescriptor
        ): TypeParameterDescriptor =
            if (descriptor.isTransformed()) {
                WrappedTypeParameterDescriptor()
            } else {
                super.remapDeclaredTypeParameter(descriptor)
            }

        private fun ClassConstructorDescriptor.isTransformed(): Boolean =
            this is WrappedClassConstructorDescriptor
                || valueParameters.any { it.type.containsComposable() }

        private fun FunctionDescriptor.isTransformed(): Boolean =
            this is WrappedSimpleFunctionDescriptor
                || valueParameters.any { it.type.containsComposable() }
                || returnType?.containsComposable() == true

        private fun ParameterDescriptor.isTransformed(): Boolean =
            this is WrappedDeclarationDescriptor<*>
                || type.containsComposable()
                || containingDeclaration.let { it is FunctionDescriptor && it.isTransformed() }

        private fun TypeParameterDescriptor.isTransformed(): Boolean =
            this is WrappedTypeParameterDescriptor
                || containingDeclaration.let { it is FunctionDescriptor && it.isTransformed() }

        private fun KotlinType.containsComposable() =
            hasComposableAnnotation()
                || arguments.any { it.type.hasComposableAnnotation() }
    }
)

/**
 * Special case to keep the original source element from the functions remapped in the
 * [ComposerParamTransformer.wrapDescriptor]
 */
private class WrappedSimpleFunctionDescriptorWithSource(
    private val source: SourceElement
) : WrappedSimpleFunctionDescriptor() {
    override fun getSource(): SourceElement = source
}

@OptIn(ObsoleteDescriptorBasedAPI::class)
object WrappedComposableDescriptorPatcher : IrElementVisitorVoid {
    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitConstructor(declaration: IrConstructor) {
        (declaration.descriptor as? WrappedClassConstructorDescriptor)?.bindIfNeeded(declaration)
        super.visitConstructor(declaration)
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction) {
        (declaration.descriptor as? WrappedSimpleFunctionDescriptor)?.bindIfNeeded(declaration)
        super.visitSimpleFunction(declaration)
    }

    @Suppress("DEPRECATION")
    override fun visitValueParameter(declaration: IrValueParameter) {
        (declaration.descriptor as? WrappedValueParameterDescriptor)?.bindIfNeeded(declaration)
        (declaration.descriptor as? WrappedReceiverParameterDescriptor)?.bindIfNeeded(declaration)
        super.visitValueParameter(declaration)
    }

    @Suppress("DEPRECATION")
    override fun visitTypeParameter(declaration: IrTypeParameter) {
        (declaration.descriptor as? WrappedTypeParameterDescriptor)?.bindIfNeeded(declaration)
        super.visitTypeParameter(declaration)
    }

    private fun <T : IrDeclaration> WrappedDeclarationDescriptor<T>.bindIfNeeded(declaration: T) {
        if (!isBound()) {
            bind(declaration)
        }
    }
}

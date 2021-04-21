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

package androidx.compose.web.elements

import androidx.compose.runtime.Composable
import androidx.compose.web.attributes.AttrsBuilder
import androidx.compose.web.attributes.Tag
import androidx.compose.web.css.CSSGroupingRuleDeclaration
import androidx.compose.web.css.CSSRuleDeclaration
import androidx.compose.web.css.CSSRuleDeclarationList
import androidx.compose.web.css.CSSStyleRuleDeclaration
import androidx.compose.web.css.StyleSheetBuilder
import androidx.compose.web.css.StyleSheetBuilderImpl
import androidx.compose.web.css.cssRules
import androidx.compose.web.css.deleteRule
import androidx.compose.web.css.get
import androidx.compose.web.css.insertRule
import androidx.compose.web.css.styleMap
import org.w3c.dom.HTMLStyleElement
import org.w3c.dom.css.CSSGroupingRule
import org.w3c.dom.css.CSSRule
import org.w3c.dom.css.StyleSheet

@Composable
inline fun Style(
    crossinline applyAttrs: AttrsBuilder<Tag.Style>.() -> Unit = {},
    rulesBuild: StyleSheetBuilder.() -> Unit
) {
    val builder = StyleSheetBuilderImpl()
    builder.rulesBuild()
    Style(applyAttrs, builder.cssRules)
}

@Composable
inline fun Style(
    crossinline applyAttrs: AttrsBuilder<Tag.Style>.() -> Unit = {},
    cssRules: CSSRuleDeclarationList
) {
    TagElement<Tag.Style, HTMLStyleElement>(
        tagName = "style",
        applyAttrs = {
            applyAttrs()
        },
        applyStyle = {}
    ) {
        DomSideEffect(cssRules) { style ->
            style.sheet?.let { sheet ->
                setCSSRules(sheet, cssRules)
                onDispose {
                    clearCSSRules(sheet)
                }
            }
        }
    }
}

fun clearCSSRules(sheet: StyleSheet) {
    repeat(sheet.cssRules.length) {
        sheet.deleteRule(0)
    }
}

fun setCSSRules(sheet: StyleSheet, cssRules: CSSRuleDeclarationList) {
    cssRules.forEach { cssRule ->
        sheet.addRule(cssRule)
    }
}

private fun StyleSheet.addRule(cssRule: String): CSSRule {
    val cssRuleIndex = this.insertRule(cssRule, this.cssRules.length)
    return this.cssRules[cssRuleIndex]
}

private fun CSSGroupingRule.addRule(cssRule: String): CSSRule {
    val cssRuleIndex = this.insertRule(cssRule, this.cssRules.length)
    return this.cssRules[cssRuleIndex]
}

private fun StyleSheet.addRule(cssRuleDeclaration: CSSRuleDeclaration) {
    val cssRule = addRule("${cssRuleDeclaration.header} {}")
    fillRule(cssRuleDeclaration, cssRule)
}

private fun CSSGroupingRule.addRule(cssRuleDeclaration: CSSRuleDeclaration) {
    val cssRule = addRule("${cssRuleDeclaration.header} {}")
    fillRule(cssRuleDeclaration, cssRule)
}

private fun fillRule(
    cssRuleDeclaration: CSSRuleDeclaration,
    cssRule: CSSRule
) {
    when (cssRuleDeclaration) {
        is CSSStyleRuleDeclaration ->
            cssRuleDeclaration.properties.forEach { (name, value) ->
                cssRule.styleMap.set(name, value)
            }
        is CSSGroupingRuleDeclaration -> {
            val cssGroupingRule = cssRule.unsafeCast<CSSGroupingRule>()
            cssRuleDeclaration.rules.forEach { childRuleDeclaration ->
                cssGroupingRule.addRule(childRuleDeclaration)
            }
        }
    }
}

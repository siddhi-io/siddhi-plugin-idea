/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.siddhi.plugins.idea.formatter;

import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.Nullable;
import org.wso2.siddhi.plugins.idea.SiddhiLanguage;

import javax.annotation.Nonnull;

import static org.wso2.siddhi.plugins.idea.SiddhiTypes.ALL;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.AS;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.AT;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.CLOSE_PAR;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.CLOSE_SQUARE_BRACKETS;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.COL;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.COMMA;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.DOT;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.FLOAT_LITERAL;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.FUNCTION;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.IDENTIFIER;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.INT_LITERAL;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.JOIN;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.LT;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.MINUS;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.OPEN_PAR;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.OPEN_SQUARE_BRACKETS;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.OPERATORS;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.PLUS;
import static org.wso2.siddhi.plugins.idea.SiddhiTypes.WITH;

/**
 * Allows siddhi language plugin to build a formatting model for a file in the language, or
 * for a portion of a file.
 * A formatting model defines how a file is broken into non-whitespace blocks and different
 * types of whitespace (alignment, indents and wraps) between them.
 */
public class SiddhiFormattingModelBuilder implements FormattingModelBuilder {

    @Nonnull
    @Override
    public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
        SiddhiBlock rootBlock = new SiddhiBlock(
                element.getNode(), null, Indent.getNoneIndent(), null, settings, createSpaceBuilder(settings)
        );
        return FormattingModelProvider.createFormattingModelForPsiFile(
                element.getContainingFile(), rootBlock, settings
        );
    }

    private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
        return new SpacingBuilder(settings, SiddhiLanguage.INSTANCE)
                .around(OPERATORS).spaceIf(true)
                .between(IDENTIFIER, LT).spaceIf(false)
                .between(PLUS, INT_LITERAL).spaceIf(false)
                .between(MINUS, INT_LITERAL).spaceIf(false)
                .between(PLUS, FLOAT_LITERAL).spaceIf(false)
                .between(MINUS, FLOAT_LITERAL).spaceIf(false)
                .around(PLUS).spaceIf(true)
                .around(MINUS).spaceIf(true)
                .before(ALL).spaceIf(false)
                .after(ALL).spaceIf(true)
                .around(AS).spaceIf(true)
                .after(FUNCTION).spaceIf(true)
                .around(JOIN).spaceIf(true)
                .around(WITH).spaceIf(true)
                .around(DOT).spaceIf(false)
                .between(OPEN_SQUARE_BRACKETS, CLOSE_SQUARE_BRACKETS).spaceIf(false)
                .between(OPEN_PAR, CLOSE_PAR).spaceIf(true)
                .around(COL).spaceIf(false)
                .before(COMMA).spaceIf(false)
                .after(COMMA).spaceIf(true)
                .after(AT).spaceIf(false)
                .between(IDENTIFIER, OPEN_PAR).spaceIf(true);
    }

    @Nullable
    @Override
    public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
        return null;
    }
}

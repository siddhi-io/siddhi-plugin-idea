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

package org.wso2.siddhi.plugins.idea.spellchecker;

import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.psi.IdentifierPSINode;

import javax.annotation.Nonnull;

/**
 * Defines spell checking strategy.
 */
public class SiddhiSpellcheckingStrategy extends SpellcheckingStrategy {

    private final SiddhiIdentifierTokenizer identifierTokenizer = new SiddhiIdentifierTokenizer();

    @Nonnull
    @Override
    public Tokenizer getTokenizer(PsiElement element) {
        if (element instanceof IdentifierPSINode) {
            if (((IdentifierPSINode) element).getElementType() == SiddhiTypes.IDENTIFIER) {
                return TEXT_TOKENIZER;
            }
        }
        return super.getTokenizer(element);
    }
}

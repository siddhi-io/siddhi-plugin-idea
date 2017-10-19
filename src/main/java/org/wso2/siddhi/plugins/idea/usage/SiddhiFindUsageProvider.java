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

package org.wso2.siddhi.plugins.idea.usage;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.antlr.jetbrains.adaptor.lexer.RuleIElementType;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.wso2.siddhi.plugins.idea.psi.IdentifierPSINode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.*;

public class SiddhiFindUsageProvider implements FindUsagesProvider {

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return null;
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof IdentifierPSINode;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (!(element.getParent() instanceof ANTLRPsiNode)) {
            return "";
        }
        ANTLRPsiNode parent = (ANTLRPsiNode) element.getParent();
        RuleIElementType elType = (RuleIElementType) parent.getNode().getElementType();
        // Todo - Add more types
        switch (elType.getRuleIndex()) {
            case RULE_definition_aggregation:
                return "Aggregation";
            case RULE_definition_function:
                return "Function";
            case RULE_definition_stream:
                return "Stream";
            case RULE_definition_table:
                return "Table";
            case RULE_definition_trigger:
                return "Trigger";
            case RULE_definition_window:
                return "Window";
            case RULE_y:
                return "Execution Element";
        }
        return "";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        return element.getText();
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        String text = element.getText();
        return text;
    }
}
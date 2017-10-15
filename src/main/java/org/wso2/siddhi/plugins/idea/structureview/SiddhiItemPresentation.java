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

package org.wso2.siddhi.plugins.idea.structureview;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.antlr.jetbrains.adaptor.psi.IdentifierDefSubtree;
import org.wso2.siddhi.plugins.idea.SiddhiIcons;
import org.wso2.siddhi.plugins.idea.psi.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SiddhiItemPresentation implements ItemPresentation {

    protected final PsiElement element;

    protected SiddhiItemPresentation(PsiElement element) {
        this.element = element;
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        if (element instanceof FunctionDefinitionNode) {
            return SiddhiIcons.FIELD;
        } else if (element instanceof TriggerDefinitionNode) {
            return SiddhiIcons.FIELD;
        } else if (element instanceof StreamDefinitionNode) {
            return SiddhiIcons.FIELD;
        } else if (element instanceof AggregationDefinitionNode) {
            return SiddhiIcons.FIELD;
        } else if (element instanceof WindowDefinitionNode) {
            return SiddhiIcons.FIELD;
        } else if (element instanceof TableDefinitionNode) {
            return SiddhiIcons.FIELD;
        } else if (element instanceof ExecutionElementNode) {
            return SiddhiIcons.METHOD;
        }
        return SiddhiIcons.ICON;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        if (element instanceof IdentifierDefSubtree) {
            PsiElement nameIdentifier = ((IdentifierDefSubtree) element).getNameIdentifier();
            if (nameIdentifier != null) {
                return nameIdentifier.getText();
            }
        }
        return element.getText();
    }

    @Nullable
    @Override
    public String getLocationString() {
        return null;
    }
}

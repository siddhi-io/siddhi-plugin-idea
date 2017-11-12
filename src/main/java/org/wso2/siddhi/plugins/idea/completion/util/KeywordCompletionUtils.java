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
package org.wso2.siddhi.plugins.idea.completion.util;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.psi.AttributeReferenceNode;
import org.wso2.siddhi.plugins.idea.psi.ConstantValueNode;
import org.wso2.siddhi.plugins.idea.psi.ExpressionNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionOperationNode;
import org.wso2.siddhi.plugins.idea.psi.MathOperationNode;
import org.wso2.siddhi.plugins.idea.psi.NameNode;
import org.wso2.siddhi.plugins.idea.psi.NullCheckNode;

public class KeywordCompletionUtils {

        @Nullable
    public static PsiElement getPreviousVisibleSiblingSkippingComments(@NotNull PsiElement currentElement) {
        PsiElement prevVisibleSibling = PsiTreeUtil.prevVisibleLeaf(currentElement);
        if (prevVisibleSibling instanceof PsiComment) {
            prevVisibleSibling = getPreviousVisibleSiblingSkippingComments(prevVisibleSibling);
        }
        if (prevVisibleSibling == null) {
            return null;
        }
        return prevVisibleSibling;
    }

    public static Boolean isExpression(@NotNull PsiElement element) {
        if (PsiTreeUtil.getParentOfType(element, ExpressionNode.class) != null) {
            if (PsiTreeUtil.getParentOfType(element, MathOperationNode.class) != null ||
                    PsiTreeUtil.getParentOfType(element, NullCheckNode.class) != null ||
                    PsiTreeUtil.getParentOfType(element, NameNode.class) != null ||
                    PsiTreeUtil.getParentOfType(element, AttributeReferenceNode.class) != null ||
                    PsiTreeUtil.getParentOfType(element, ConstantValueNode.class) != null ||
                    PsiTreeUtil.getParentOfType(element, FunctionOperationNode.class) != null) {
                return true;
            } else {
                PsiElement prevVisibleSibling = getPreviousVisibleSiblingSkippingComments(element);
                IElementType elementType = ((LeafPsiElement) element).getElementType();
                if (elementType == SiddhiTypes.CLOSE_PAR
                        && PsiTreeUtil.getParentOfType(prevVisibleSibling, MathOperationNode.class) != null) {
                    return true;
                }
            }
        }
        return false;
    }
}

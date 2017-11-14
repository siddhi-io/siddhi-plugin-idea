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
package org.wso2.siddhi.plugins.idea.completion.executionelements.query;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.psi.DeleteFromTableNode;
import org.wso2.siddhi.plugins.idea.psi.OutputEventTypeNode;
import org.wso2.siddhi.plugins.idea.psi.OutputRateNode;
import org.wso2.siddhi.plugins.idea.psi.QueryInputNode;
import org.wso2.siddhi.plugins.idea.psi.QueryOutputNode;
import org.wso2.siddhi.plugins.idea.psi.QuerySectionNode;
import org.wso2.siddhi.plugins.idea.psi.TargetNode;
import org.wso2.siddhi.plugins.idea.psi.UpdateOrInsertIntoNode;
import org.wso2.siddhi.plugins.idea.psi.UpdateTableNode;

import javax.annotation.Nonnull;

import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addForKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addIntoKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOnKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOutputEventTypeKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addSetKeyword;
import static org.wso2.siddhi.plugins.idea.completion.util.KeywordCompletionUtils.getPreviousVisibleSiblingSkippingComments;

/**
 * Provides code completions for query outputs.
 */
public class QueryOutputCompletionContributor {

    public static void queryOutputCompletion(@Nonnull CompletionResultSet result, PsiElement element,
                                             PsiElement prevVisibleSibling, IElementType
                                                     prevVisibleSiblingElementType, PsiElement
                                                     prevPreVisibleSibling) {
        //suggestions after INSERT keyword
        if (prevVisibleSiblingElementType == SiddhiTypes.INSERT && (PsiTreeUtil.getParentOfType
                (prevPreVisibleSibling, OutputRateNode.class) != null || PsiTreeUtil.getParentOfType
                (prevPreVisibleSibling, QuerySectionNode.class) != null || PsiTreeUtil.getParentOfType
                (prevPreVisibleSibling, QueryInputNode.class) != null)) {
            addOutputEventTypeKeywords(result);
            addIntoKeyword(result);
            return;
        }
        //suggesting INTO keyword after a output event type in a query
        PsiElement parentOfPrevVisSibling = prevVisibleSibling.getParent();
        if (parentOfPrevVisSibling instanceof OutputEventTypeNode) {
            PsiElement prevVisibleSiblingOfParent = getPreviousVisibleSiblingSkippingComments(parentOfPrevVisSibling);
            IElementType elementTypeOfPrevVisibleSiblingOfParent = null;
            if (prevVisibleSiblingOfParent != null) {
                elementTypeOfPrevVisibleSiblingOfParent = ((LeafPsiElement) prevVisibleSiblingOfParent)
                        .getElementType();
            }
            if (elementTypeOfPrevVisibleSiblingOfParent == SiddhiTypes.INSERT) {
                addIntoKeyword(result);
                return;
            }
        }
        //Suggestions inside a QueryOutputNode
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, QueryOutputNode.class) != null) {
            //Suggesting keywords related to "delete" in query
            if (PsiTreeUtil.getParentOfType(element, DeleteFromTableNode.class) != null) {
                if (PsiTreeUtil.getParentOfType(prevVisibleSibling, TargetNode.class) != null) {
                    addForKeyword(result);
                    addOnKeyword(result);
                    return;
                }
                if (prevVisibleSiblingElementType == SiddhiTypes.FOR) {
                    addOutputEventTypeKeywords(result);
                    return;
                }
                if (PsiTreeUtil.getParentOfType(prevVisibleSibling, OutputEventTypeNode.class) != null) {
                    addOnKeyword(result);
                    return;
                }
            }
            //suggesting keywords related to "update or insert into" in query
            if (PsiTreeUtil.getParentOfType(element, UpdateOrInsertIntoNode.class) != null) {
                if (PsiTreeUtil.getParentOfType(prevVisibleSibling, TargetNode.class) != null) {
                    addForKeyword(result);
                    addOnKeyword(result);
                    addSetKeyword(result);
                    return;
                }
                if (prevVisibleSiblingElementType == SiddhiTypes.FOR) {
                    addOutputEventTypeKeywords(result);
                    return;
                }
                if (PsiTreeUtil.getParentOfType(prevVisibleSibling, OutputEventTypeNode.class) != null) {
                    addOnKeyword(result);
                    addSetKeyword(result);
                    return;
                }
            }
            //suggesting keywords related to "update" in query
            if (PsiTreeUtil.getParentOfType(element, UpdateTableNode.class) != null) {
                if (PsiTreeUtil.getParentOfType(prevVisibleSibling, TargetNode.class) != null) {
                    addForKeyword(result);
                    addOnKeyword(result);
                    addSetKeyword(result);
                    return;
                }
                if (prevVisibleSiblingElementType == SiddhiTypes.FOR) {
                    addOutputEventTypeKeywords(result);
                    return;
                }
                if (PsiTreeUtil.getParentOfType(prevVisibleSibling, OutputEventTypeNode.class) != null) {
                    addOnKeyword(result);
                    addSetKeyword(result);
                    return;
                }
            }
            //Suggesting  output event types after RETURN keyword in the QueryOutputNode
            if (prevVisibleSiblingElementType == SiddhiTypes.RETURN) {
                addOutputEventTypeKeywords(result);
                return;
            }
        }
    }
}

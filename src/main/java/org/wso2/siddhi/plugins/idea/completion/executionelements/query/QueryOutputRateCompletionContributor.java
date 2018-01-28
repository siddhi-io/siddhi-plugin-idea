/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.jetbrains.annotations.NotNull;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.psi.OutputRateNode;
import org.wso2.siddhi.plugins.idea.psi.TimeValueNode;

import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addBeginingOfQueryOutputKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addEventsKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addEveryKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOutputRateTypesKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addSnapshotKeyword;
import static org.wso2.siddhi.plugins.idea.completion.util.KeywordCompletionUtils.getPreviousVisibleSiblingSkippingComments;

/**
 * Provides code completions for query outputRate section.
 */
public class QueryOutputRateCompletionContributor {

    public static void queryOutputRateCompletion(@NotNull CompletionResultSet result, PsiElement element,
                                              PsiElement prevVisibleSibling,
                                              IElementType prevVisibleSiblingElementType,
                                              PsiElement prevPrevVisibleSibling,
                                              IElementType prevPreVisibleSiblingElementType) {
        // Suggestions related to QueryOutputRateNode
        if (PsiTreeUtil.getParentOfType(element, OutputRateNode.class) != null) {
            // suggesting keywords after 'output' keyword
            if (prevVisibleSiblingElementType == SiddhiTypes.OUTPUT) {
                addSnapshotKeyword(result);
                addOutputRateTypesKeyword(result);
                addEveryKeyword(result);
                return;
            }
            // suggesting keywords after 'snapshot' keyword
            if (prevPreVisibleSiblingElementType == SiddhiTypes.OUTPUT && prevVisibleSiblingElementType ==
                    SiddhiTypes.SNAPSHOT) {
                addEveryKeyword(result);
                return;
            }
            // suggesting keywords after outputRateTypes keyword
            if ((prevVisibleSiblingElementType == SiddhiTypes.ALL
                    || prevVisibleSiblingElementType == SiddhiTypes.LAST
                    || prevVisibleSiblingElementType == SiddhiTypes.FIRST)
                && prevPreVisibleSiblingElementType == SiddhiTypes.OUTPUT) {
                addEveryKeyword(result);
                return;
            }
            // suggesting keywords after  OUTPUT output_rate_type? EVERY INT_LITERAL rule
            // avoiding after OUTPUT SNAPSHOT EVERY clause
            if (prevVisibleSiblingElementType == SiddhiTypes.INT_LITERAL
                    && prevPreVisibleSiblingElementType == SiddhiTypes.EVERY) {
                PsiElement prevPrevPreVisibleSibling = null;
                if (prevPrevVisibleSibling != null) {
                    prevPrevPreVisibleSibling = getPreviousVisibleSiblingSkippingComments(prevPrevVisibleSibling);
                }
                IElementType prevPrevPreVisibleSiblingElementType = null;
                if (prevPrevPreVisibleSibling != null) {
                    prevPrevPreVisibleSiblingElementType = ((LeafPsiElement) prevPrevPreVisibleSibling)
                            .getElementType();
                }
                if (prevPrevPreVisibleSiblingElementType != SiddhiTypes.SNAPSHOT) {
                    addEventsKeyword(result);
                    return;
                }
            }
            // This provides suggestions after ->OUTPUT output_rate_type? EVERY ( time_value )
            // | OUTPUT SNAPSHOT EVERY time_value in output_rate rule
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, OutputRateNode.class) != null
                    && PsiTreeUtil.getParentOfType(prevVisibleSibling, TimeValueNode.class) != null) {
                PsiElement timeValueNodeElement = PsiTreeUtil.getParentOfType(prevVisibleSibling, TimeValueNode.class);
                PsiElement prevSiblingOfTimeValueNode = null;
                if (timeValueNodeElement != null) {
                    prevSiblingOfTimeValueNode = getPreviousVisibleSiblingSkippingComments(timeValueNodeElement);
                }
                IElementType prevSiblingOfTimeValueNodeElementType = null;
                if (prevSiblingOfTimeValueNode != null) {
                    prevSiblingOfTimeValueNodeElementType = ((LeafPsiElement) prevSiblingOfTimeValueNode)
                            .getElementType();
                }
                if (prevSiblingOfTimeValueNodeElementType == SiddhiTypes.EVERY &&
                        prevVisibleSiblingElementType != SiddhiTypes.INT_LITERAL) {
                    addBeginingOfQueryOutputKeywords(result);
                    return;
                }
            }
        }
    }
}

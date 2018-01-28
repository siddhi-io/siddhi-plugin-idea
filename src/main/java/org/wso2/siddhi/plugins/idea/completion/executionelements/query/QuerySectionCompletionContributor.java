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
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.psi.AttributeReferenceNode;
import org.wso2.siddhi.plugins.idea.psi.ExpressionNode;
import org.wso2.siddhi.plugins.idea.psi.GroupByNode;
import org.wso2.siddhi.plugins.idea.psi.HavingNode;
import org.wso2.siddhi.plugins.idea.psi.LimitNode;
import org.wso2.siddhi.plugins.idea.psi.OrderByNode;
import org.wso2.siddhi.plugins.idea.psi.OutputAttributeNode;
import org.wso2.siddhi.plugins.idea.psi.QuerySectionNode;

import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addBeginingOfQueryOutputKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addByKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addGroupKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addHavingKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addLimitKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOrderKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOrderRelatedKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOutputKeyword;

/**
 * Provides code completions for query sections.
 */
public class QuerySectionCompletionContributor {

    public static void querySectionCompletion(@NotNull CompletionResultSet result, PsiElement element,
                                              PsiElement prevVisibleSibling,
                                              IElementType prevVisibleSiblingElementType,
                                              PsiElement prevPrevVisibleSibling) {
        // Suggestions related to QuerySectionNode
        if (PsiTreeUtil.getParentOfType(element, QuerySectionNode.class) != null) {
            // This provides suggestions after ->(SELECT ('*'| (output_attribute (',' output_attribute)* ))) in
            // query_section1 rule
            if ((prevVisibleSiblingElementType != SiddhiTypes.AS && PsiTreeUtil
                    .getParentOfType(prevVisibleSibling, OutputAttributeNode.class) != null) ||
                    prevVisibleSiblingElementType == SiddhiTypes.STAR) {
                addGroupKeyword(result);
                addHavingKeyword(result);
                addOrderKeyword(result);
                addLimitKeyword(result);
                addOutputKeyword(result);
                addBeginingOfQueryOutputKeywords(result);
                return;
            }
            // suggesting 'by' keyword after 'group' keyword
            if (prevVisibleSiblingElementType == SiddhiTypes.GROUP || prevVisibleSiblingElementType == SiddhiTypes
                    .ORDER) {
                addByKeyword(result);
                return;
            }
            // This provides suggestions after ->(SELECT ('*'| (output_attribute (',' output_attribute)* ))) group_by in
            // query_section1 rule
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, AttributeReferenceNode.class) != null
                    && PsiTreeUtil.getParentOfType(element, GroupByNode.class) != null) {
                addHavingKeyword(result);
                addOrderKeyword(result);
                addLimitKeyword(result);
                addOutputKeyword(result);
                addBeginingOfQueryOutputKeywords(result);
                return;
            }
            // This provides suggestions after ->(SELECT ('*'| (output_attribute (',' output_attribute)* ))) having in
            // query_section1 rule
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, ExpressionNode.class) != null
                    && PsiTreeUtil.getParentOfType(element, HavingNode.class) != null) {
                addOrderKeyword(result);
                addLimitKeyword(result);
                addOutputKeyword(result);
                addBeginingOfQueryOutputKeywords(result);
                return;
            }
            // This provides suggestions after ->(SELECT ('*'| (output_attribute (',' output_attribute)* ))) order_by in
            // query_section1 rule
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, AttributeReferenceNode.class) != null
                    && PsiTreeUtil.getParentOfType(element, OrderByNode.class) != null) {
                addOrderRelatedKeywords(result);
                addLimitKeyword(result);
                addOutputKeyword(result);
                addBeginingOfQueryOutputKeywords(result);
                return;
            }
            // This provides suggestions after ->(SELECT ('*'| (output_attribute (',' output_attribute)* ))) ORDER BY
            // order_by_reference in query_section1 rule
            if ((prevVisibleSiblingElementType == SiddhiTypes.ASC || prevVisibleSiblingElementType == SiddhiTypes.DESC)
                    && PsiTreeUtil.getParentOfType(prevPrevVisibleSibling, AttributeReferenceNode.class) != null
                    && PsiTreeUtil.getParentOfType(prevPrevVisibleSibling, OrderByNode.class) != null) {
                addLimitKeyword(result);
                addOutputKeyword(result);
                addBeginingOfQueryOutputKeywords(result);
                return;
            }
            // This provides suggestions after ->(SELECT ('*'| (output_attribute (',' output_attribute)* ))) limit in
            // query_section1 rule
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, ExpressionNode.class) != null
                    && PsiTreeUtil.getParentOfType(element, LimitNode.class) != null) {
                addOutputKeyword(result);
                addBeginingOfQueryOutputKeywords(result);
                return;
            }
        }
    }
}

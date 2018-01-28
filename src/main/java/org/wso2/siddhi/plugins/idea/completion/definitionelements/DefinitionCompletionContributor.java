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
package org.wso2.siddhi.plugins.idea.completion.definitionelements;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.psi.AggregationDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.AggregationNameNode;
import org.wso2.siddhi.plugins.idea.psi.AggregationTimeDurationNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeNameNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeReferenceNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeTypeNode;
import org.wso2.siddhi.plugins.idea.psi.DefinitionElementNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionNameNode;
import org.wso2.siddhi.plugins.idea.psi.GroupByNode;
import org.wso2.siddhi.plugins.idea.psi.LanguageNameNode;
import org.wso2.siddhi.plugins.idea.psi.OutputAttributeNode;
import org.wso2.siddhi.plugins.idea.psi.OutputEventTypeNode;
import org.wso2.siddhi.plugins.idea.psi.StreamIdNode;
import org.wso2.siddhi.plugins.idea.psi.TriggerNameNode;
import org.wso2.siddhi.plugins.idea.psi.WindowDefinitionNode;

import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addAggregateKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addAggregationTimeTypesKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addAtKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addByKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addDefineTypesAsLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addEveryKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addFromKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addGroupKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addLanguageTypesKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOutputEventTypeKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addReturnKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addSelectKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addValueTypesAsLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addWindowProcessorTypesAsLookups;
import static org.wso2.siddhi.plugins.idea.completion.util.KeywordCompletionUtils.getPreviousVisibleSiblingSkippingComments;

/**
 * Provides code completions for definitions.
 */
public class DefinitionCompletionContributor {

    public static void definitionCompletion(@NotNull CompletionResultSet result, PsiElement element,
                                           PsiElement prevVisibleSibling,
                                           IElementType prevVisibleSiblingElementType,
                                           PsiElement prevPrevVisibleSibling) {
        // suggestions after define keyword
        if (prevVisibleSiblingElementType == SiddhiTypes.DEFINE) {
            addDefineTypesAsLookups(result);
            return;
        }
        // suggestions after an attribute type if it is in a definition element other than an aggregation definition
        // if the aggregation is also included it will provide attribute types after the attributes in 'select'
        // clause inside the aggregation definition
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, AttributeNameNode.class) != null
                && PsiTreeUtil.getParentOfType(prevVisibleSibling, DefinitionElementNode.class) != null
                && PsiTreeUtil.getParentOfType(prevVisibleSibling, AggregationDefinitionNode.class) == null) {
            addValueTypesAsLookups(result);
            return;
        }
        //TODO: on that class handle attribute type suggestions
        // suggestions after a trigger name node AT keyword
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, TriggerNameNode.class) != null) {
            addAtKeyword(result);
            return;
        }
        if (prevPrevVisibleSibling != null) {
            // suggestions related to aggregation definition
            if (PsiTreeUtil.getParentOfType(element, AggregationDefinitionNode.class) != null) {
                aggregationDefinitionRelatedKeywordCompletion(result, element,
                        prevVisibleSibling, prevVisibleSiblingElementType, prevPrevVisibleSibling);
                return;
            }
            // after AT in a Trigger definition, suggest EVERY keyword
            if (prevVisibleSiblingElementType == SiddhiTypes.AT && PsiTreeUtil.getParentOfType
                    (prevPrevVisibleSibling, TriggerNameNode.class) != null) {
                addEveryKeyword(result);
                return;
            }
            // suggestions related to window definitions
            if (PsiTreeUtil.getParentOfType(element, WindowDefinitionNode.class) != null) {
                windowDefinitionRelatedKeywordCompletion(result, element, prevVisibleSibling,
                        prevVisibleSiblingElementType);
                return;
            }
            // suggestions related to function definitions
            if (PsiTreeUtil.getParentOfType(element, FunctionDefinitionNode.class) != null) {
                functionDefinitionRelatedKeywordCompletion(result, element, prevPrevVisibleSibling,
                        prevVisibleSiblingElementType);
                return;
            }
        }
    }



    private static void windowDefinitionRelatedKeywordCompletion(@NotNull CompletionResultSet result,
                                                                PsiElement element,
                                                                PsiElement prevVisibleSibling,
                                                                IElementType prevVisibleSiblingElementType) {
        if (getPreviousVisibleSiblingSkippingComments(prevVisibleSibling) != null) {
            PsiElement prevPrevVisibleSibling = getPreviousVisibleSiblingSkippingComments(prevVisibleSibling);
            if (element.getParent().getParent().getPrevSibling() != null) {
                if (prevVisibleSiblingElementType == SiddhiTypes.CLOSE_PAR && PsiTreeUtil.getParentOfType
                        (prevPrevVisibleSibling, AttributeTypeNode.class) != null && element.getParent()
                        .getParent().getPrevSibling() instanceof PsiWhiteSpace) {
                    addWindowProcessorTypesAsLookups(result);
                    return;
                }
            }
        }
        if (PsiTreeUtil.getParentOfType(element, OutputEventTypeNode.class) != null) {
            addOutputEventTypeKeywords(result);
        }
    }

    private static void functionDefinitionRelatedKeywordCompletion(@NotNull CompletionResultSet result,
                                                                  PsiElement element,
                                                                  PsiElement prevPreVisibleSibling,
                                                                  IElementType prevVisibleSiblingElementType) {
        if (PsiTreeUtil.getParentOfType(element, LanguageNameNode.class) != null
                && PsiTreeUtil.getParentOfType(prevPreVisibleSibling, FunctionNameNode.class) != null
                && prevVisibleSiblingElementType == SiddhiTypes.OPEN_SQUARE_BRACKETS) {
            addLanguageTypesKeywords(result);
            return;
        }
        if (element.getParent().getPrevSibling() != null) {
            if (prevVisibleSiblingElementType == SiddhiTypes.CLOSE_SQUARE_BRACKETS
                    && PsiTreeUtil.getParentOfType(prevPreVisibleSibling, LanguageNameNode.class) != null
                    && element.getParent().getPrevSibling() instanceof PsiWhiteSpace) {
                addReturnKeyword(result);
                return;
            }
        }
        IElementType prevPrevVisibleSiblingElementType = null;
        if (prevPreVisibleSibling != null) {
            prevPrevVisibleSiblingElementType = ((LeafPsiElement) prevPreVisibleSibling)
                    .getElementType();
        }
        if (PsiTreeUtil.getParentOfType(element, FunctionDefinitionNode.class) != null
                && prevVisibleSiblingElementType == SiddhiTypes.RETURN
                && prevPrevVisibleSiblingElementType == SiddhiTypes.CLOSE_SQUARE_BRACKETS) {
            addValueTypesAsLookups(result);
        }
    }

    private static void aggregationDefinitionRelatedKeywordCompletion(@NotNull CompletionResultSet result,
                                                                     PsiElement element,
                                                                     PsiElement prevVisibleSibling,
                                                                     IElementType prevVisibleSiblingElementType,
                                                                     PsiElement prevPreVisibleSibling) {
        // add from keyword after aggregation name
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, AggregationNameNode.class) != null) {
            addFromKeyword(result);
            return;
        }
        // add select keyword after stream source
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, StreamIdNode.class) != null) {
            addSelectKeyword(result);
            return;
        }
        // This provides suggestions after ->(SELECT ('*'| (output_attribute (',' output_attribute)* ))) in
        // group_by_query_selection rule in aggregation definition
        if ((prevVisibleSiblingElementType != SiddhiTypes.AS && PsiTreeUtil
                .getParentOfType(prevVisibleSibling, OutputAttributeNode.class) != null) ||
                prevVisibleSiblingElementType == SiddhiTypes.STAR) {
            addGroupKeyword(result);
            addAggregateKeyword(result);
            return;
        }
        // suggesting 'by' keyword after 'group' keyword
        if (prevVisibleSiblingElementType == SiddhiTypes.GROUP) {
            addByKeyword(result);
            return;
        }
        // This provides suggestions after ->(SELECT ('*'| (output_attribute (',' output_attribute)* ))) group_by in
        // group_by_query_selection rule in aggregation definition
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, AttributeReferenceNode.class) != null
                && PsiTreeUtil.getParentOfType(element, GroupByNode.class) != null) {
            addAggregateKeyword(result);
            return;
        }
        // suggesting 'by' keyword after 'aggregate' keyword
        if (prevVisibleSiblingElementType == SiddhiTypes.AGGREGATE) {
            addByKeyword(result);
            addEveryKeyword(result);
            return;
        }
        IElementType prevPrevVisibleSiblingElementType = null;
        PsiElement prevPrevPrevVisibleSibling;
        IElementType prevPrevPrevVisibleSiblingElementType = null;
        if (prevPreVisibleSibling != null) {
            prevPrevVisibleSiblingElementType = ((LeafPsiElement) prevPreVisibleSibling).getElementType();
            prevPrevPrevVisibleSibling = getPreviousVisibleSiblingSkippingComments(prevPreVisibleSibling);
            if (prevPrevPrevVisibleSibling != null) {
                prevPrevPrevVisibleSiblingElementType = ((LeafPsiElement) prevPrevPrevVisibleSibling).getElementType();
            }
        }
        // suggesting 'every' keyword after ->DEFINE AGGREGATION aggregation_name
        // FROM standard_stream group_by_query_selection AGGREGATE (BY attribute_reference) rule in aggregation
        // definition
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, AttributeReferenceNode.class) != null
                && prevPrevVisibleSiblingElementType == SiddhiTypes.BY
                && prevPrevPrevVisibleSiblingElementType == SiddhiTypes.AGGREGATE) {
            addEveryKeyword(result);
            return;
        }
        // suggesting aggregation time types in aggregation definition
        if (PsiTreeUtil.getParentOfType(element, AggregationTimeDurationNode.class) != null
                && (prevVisibleSiblingElementType == SiddhiTypes.EVERY
                || prevVisibleSiblingElementType == SiddhiTypes.COMMA
                || prevVisibleSiblingElementType == SiddhiTypes.TRIPLE_DOT)) {
            addAggregationTimeTypesKeyword(result);
            return;
        }
    }
}

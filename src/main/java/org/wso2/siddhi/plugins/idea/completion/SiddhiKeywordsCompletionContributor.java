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

package org.wso2.siddhi.plugins.idea.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.completion.executionelements.partition.PartitionCompletionContributor;
import org.wso2.siddhi.plugins.idea.completion.executionelements.query.QueryCompletionContributor;
import org.wso2.siddhi.plugins.idea.psi.AnnotationNode;
import org.wso2.siddhi.plugins.idea.psi.AppAnnotationNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeNameNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeTypeNode;
import org.wso2.siddhi.plugins.idea.psi.DefinitionElementNode;
import org.wso2.siddhi.plugins.idea.psi.ExecutionElementNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionNameNode;
import org.wso2.siddhi.plugins.idea.psi.LanguageNameNode;
import org.wso2.siddhi.plugins.idea.psi.OutputEventTypeNode;
import org.wso2.siddhi.plugins.idea.psi.ParseNode;
import org.wso2.siddhi.plugins.idea.psi.PartitionNode;
import org.wso2.siddhi.plugins.idea.psi.QueryNode;
import org.wso2.siddhi.plugins.idea.psi.SiddhiAppNode;
import org.wso2.siddhi.plugins.idea.psi.SiddhiFile;
import org.wso2.siddhi.plugins.idea.psi.TriggerNameNode;
import org.wso2.siddhi.plugins.idea.psi.WindowDefinitionNode;

import javax.annotation.Nonnull;

import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addAfterATSymbolLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addAtKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addDefineTypesAsLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addEveryKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addFromKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addInitialTypesAsLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addLanguageTypesKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOutputEventTypeKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addReturnKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addValueTypesAsLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addWindowProcessorTypesAsLookups;
import static org.wso2.siddhi.plugins.idea.completion.executionelements.partition.PartitionCompletionContributor.isEndOfAQueryOutput;
import static org.wso2.siddhi.plugins.idea.completion.util.KeywordCompletionUtils.getPreviousVisibleSiblingSkippingComments;

/**
 * Defines utility methods used for code completions.
 */
public class SiddhiKeywordsCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@Nonnull CompletionParameters parameters, @Nonnull CompletionResultSet result) {
        PsiElement element = parameters.getPosition();
        if (element instanceof LeafPsiElement) {
            IElementType elementType = ((LeafPsiElement) element).getElementType();
            if (elementType == SiddhiTypes.IDENTIFIER && getPreviousVisibleSiblingSkippingComments(element) == null) {
                // gives null in first line first character
                //initial suggestions-suggestion for first character of the file
                addInitialTypesAsLookups(result);
                return;
            }
            if (elementType == SiddhiTypes.IDENTIFIER && getPreviousVisibleSiblingSkippingComments(element) != null) {
                PsiElement prevVisibleSibling = getPreviousVisibleSiblingSkippingComments(element);
                if (prevVisibleSibling != null) {
                    IElementType prevVisibleSiblingElementType = null;
                    if (prevVisibleSibling instanceof LeafPsiElement) {
                        prevVisibleSiblingElementType = ((LeafPsiElement) prevVisibleSibling).getElementType();
                    }
                    PsiElement prevPreVisibleSibling = null;
                    if (getPreviousVisibleSiblingSkippingComments(prevVisibleSibling) != null) {

                        prevPreVisibleSibling = getPreviousVisibleSiblingSkippingComments(prevVisibleSibling);
                    }
                    PsiElement prevPrePreVisibleSibling = null;
                    if (getPreviousVisibleSiblingSkippingComments(prevVisibleSibling) != null) {

                        prevPrePreVisibleSibling = getPreviousVisibleSiblingSkippingComments(prevVisibleSibling);
                    }
                    //Suggestions after ';' in a partition. This means another query is going to write
                    if (prevVisibleSiblingElementType == SiddhiTypes.SCOL
                            && PsiTreeUtil.getParentOfType(element, ParseNode.class) != null
                            && prevPreVisibleSibling != null
                            && PsiTreeUtil.getParentOfType(prevPreVisibleSibling, PartitionNode.class) != null
                            && prevPrePreVisibleSibling != null
                            && isEndOfAQueryOutput(prevPreVisibleSibling, prevPrePreVisibleSibling)) {
                        addFromKeyword(result);
                        return;
                    }
                    //Suggestions after a semicolon
                    if (prevVisibleSiblingElementType == SiddhiTypes.SCOL) {
                        addInitialTypesAsLookups(result);
                        return;
                    }
                    //Suggestions after @ symbol
                    if (prevVisibleSiblingElementType == SiddhiTypes.AT_SYMBOL) {
                        addAfterATSymbolLookups(result);
                        return;
                    }
                    //suggestions after define keyword
                    if (prevVisibleSiblingElementType == SiddhiTypes.DEFINE) {
                        addDefineTypesAsLookups(result);
                        return;
                    }
                    //suggestions after an annotation
                    if (prevVisibleSiblingElementType == SiddhiTypes.CLOSE_PAR && (PsiTreeUtil.getParentOfType
                            (prevVisibleSibling, AppAnnotationNode.class) != null ||
                            PsiTreeUtil.getParentOfType(prevVisibleSibling, AnnotationNode.class) != null)) {
                        addInitialTypesAsLookups(result);
                        return;
                    }
                    //suggestions after an attribute type if it is in a definition element
                    if (PsiTreeUtil.getParentOfType(prevVisibleSibling, AttributeNameNode.class) != null && PsiTreeUtil
                            .getParentOfType(prevVisibleSibling, DefinitionElementNode.class) != null) {
                        addValueTypesAsLookups(result);
                        return;
                    }
                    //suggestions after a trigger name node AT keyword
                    if (PsiTreeUtil.getParentOfType(prevVisibleSibling, TriggerNameNode.class) != null) {
                        addAtKeyword(result);
                        return;
                    }
                    if (prevPreVisibleSibling != null) {
                        //Handling suggestions in a query
                        if (PsiTreeUtil.getParentOfType(element, ExecutionElementNode.class) != null) {
                            executionElementRelatedKeywordCompletion(result, element, prevVisibleSibling,
                                    prevVisibleSiblingElementType, prevPreVisibleSibling);
                            return;
                        }
                        //after AT in a Trigger definition, suggest EVERY keyword
                        if (prevVisibleSiblingElementType == SiddhiTypes.AT && PsiTreeUtil.getParentOfType
                                (prevPreVisibleSibling, TriggerNameNode.class) != null) {
                            addEveryKeyword(result);
                            return;
                        }
                        //Window definitions suggestions
                        if (PsiTreeUtil.getParentOfType(element, WindowDefinitionNode.class) != null) {
                            windowDefinitionRelatedKeywordCompletion(result, element, prevVisibleSibling,
                                    prevVisibleSiblingElementType);
                        }
                        //suggestions related to function definition
                        functionDefinitionRelatedKeywordCompletion(result, element, prevPreVisibleSibling,
                                prevVisibleSiblingElementType);
                        //TODO: add aggregation definition
                    }
                    //Adding suggestions after a comment
                    if (prevVisibleSibling instanceof PsiComment && (prevVisibleSibling.getParent() instanceof
                            SiddhiFile
                            || prevVisibleSibling.getParent() instanceof ParseNode
                            || prevVisibleSibling.getParent().getParent() instanceof ParseNode
                            || prevVisibleSibling.getParent() instanceof SiddhiAppNode)) {
                        addInitialTypesAsLookups(result);
                    }
                }
            }
        }
    }

    //TODO: Restructure definition related completions
    private void windowDefinitionRelatedKeywordCompletion(@Nonnull CompletionResultSet result, PsiElement element,
                                                          PsiElement prevVisibleSibling, IElementType
                                                                  prevVisibleSiblingElementType) {
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

    private void functionDefinitionRelatedKeywordCompletion(@Nonnull CompletionResultSet result, PsiElement element,
                                                            PsiElement prevPreVisibleSibling,
                                                            IElementType prevVisibleSiblingElementType) {
        if (PsiTreeUtil.getParentOfType(element, LanguageNameNode.class) != null && PsiTreeUtil
                .getParentOfType(prevPreVisibleSibling, FunctionNameNode.class) != null
                && prevVisibleSiblingElementType == SiddhiTypes.OPEN_SQUARE_BRACKETS) {
            addLanguageTypesKeywords(result);
            return;
        }
        if (element.getParent().getPrevSibling() != null) {
            if (prevVisibleSiblingElementType == SiddhiTypes.CLOSE_SQUARE_BRACKETS && PsiTreeUtil
                    .getParentOfType(prevPreVisibleSibling, LanguageNameNode.class) != null &&
                    element.getParent().getPrevSibling() instanceof PsiWhiteSpace) {
                addReturnKeyword(result);
                return;
            }
        }
        IElementType prevPrevVisibleSiblingElementType = null;
        if (prevPreVisibleSibling != null) {
            prevPrevVisibleSiblingElementType = ((LeafPsiElement) prevPreVisibleSibling)
                    .getElementType();
        }
        if (PsiTreeUtil.getParentOfType(element, FunctionDefinitionNode.class) != null &&
                prevVisibleSiblingElementType == SiddhiTypes.RETURN &&
                prevPrevVisibleSiblingElementType == SiddhiTypes.CLOSE_SQUARE_BRACKETS) {
            addValueTypesAsLookups(result);
        }
    }

    private void executionElementRelatedKeywordCompletion(@Nonnull CompletionResultSet result, PsiElement element,
                                                          PsiElement prevVisibleSibling, IElementType
                                                                  prevVisibleSiblingElementType, PsiElement
                                                                  prevPreVisibleSibling) {
        //keyword completion related to partitions
        if (PsiTreeUtil.getParentOfType(element, PartitionNode.class) != null) {
            PartitionCompletionContributor.partitionCompletion(result, element, prevVisibleSibling,
                    prevVisibleSiblingElementType,
                    prevPreVisibleSibling);
            //Don't use 'return' here. Because inside a partition there can be queries as well. So if we return from
            // here then those code suggestions for queries will not work.
        }
        //keyword completion related to queries
        if (PsiTreeUtil.getParentOfType(element, QueryNode.class) != null) {
            QueryCompletionContributor.queryCompletion(result, element, prevVisibleSibling,
                    prevVisibleSiblingElementType, prevPreVisibleSibling);
            return;
        }
    }
}

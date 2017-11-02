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
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.psi.AnnotationNode;
import org.wso2.siddhi.plugins.idea.psi.AppAnnotationNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeNameNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeReferenceNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeTypeNode;
import org.wso2.siddhi.plugins.idea.psi.DefinitionElementNode;
import org.wso2.siddhi.plugins.idea.psi.DefinitionElementWithExecutionElementNode;
import org.wso2.siddhi.plugins.idea.psi.DeleteFromTableNode;
import org.wso2.siddhi.plugins.idea.psi.ExecutionElementNode;
import org.wso2.siddhi.plugins.idea.psi.ExpressionNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionNameNode;
import org.wso2.siddhi.plugins.idea.psi.GroupByNode;
import org.wso2.siddhi.plugins.idea.psi.HavingNode;
import org.wso2.siddhi.plugins.idea.psi.JoinNode;
import org.wso2.siddhi.plugins.idea.psi.LanguageNameNode;
import org.wso2.siddhi.plugins.idea.psi.OutputAttributeNode;
import org.wso2.siddhi.plugins.idea.psi.OutputEventTypeNode;
import org.wso2.siddhi.plugins.idea.psi.OutputRateNode;
import org.wso2.siddhi.plugins.idea.psi.ParseNode;
import org.wso2.siddhi.plugins.idea.psi.QueryInputNode;
import org.wso2.siddhi.plugins.idea.psi.QueryOutputNode;
import org.wso2.siddhi.plugins.idea.psi.QuerySectionNode;
import org.wso2.siddhi.plugins.idea.psi.SiddhiFile;
import org.wso2.siddhi.plugins.idea.psi.SourceNode;
import org.wso2.siddhi.plugins.idea.psi.StreamIdNode;
import org.wso2.siddhi.plugins.idea.psi.TargetNode;
import org.wso2.siddhi.plugins.idea.psi.TimeValueNode;
import org.wso2.siddhi.plugins.idea.psi.TriggerNameNode;
import org.wso2.siddhi.plugins.idea.psi.UpdateOrInsertIntoNode;
import org.wso2.siddhi.plugins.idea.psi.UpdateTableNode;
import org.wso2.siddhi.plugins.idea.psi.WindowDefinitionNode;

import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addAfterATSymbolLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addBeginingOfQueryOutputKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addAtKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addByKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addDefineTypesAsLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addEveryKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addForKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addHavingKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addInitialTypesAsLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addIntoKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addLanguageTypesKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOnKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOutputEventTypeKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addReturnKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addSetKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addSuggestionsAfterSource;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addSuggestionsAfterUnidirectional;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addValueTypesAsLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addWindowProcessorTypesAsLookups;

public class SiddhiKeywordsCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition();
        if (element instanceof LeafPsiElement) {
            IElementType elementType = ((LeafPsiElement) element).getElementType();
            if (elementType == SiddhiTypes.IDENTIFIER && PsiTreeUtil.prevVisibleLeaf(element) == null) { //gives
                // gives null in first line first character
                //initial suggestions-suggestion for first character of the file
                addInitialTypesAsLookups(result);
                return;
            }
            if (elementType == SiddhiTypes.IDENTIFIER && PsiTreeUtil.prevVisibleLeaf(element) != null) {
                PsiElement prevVisibleSibling = PsiTreeUtil.prevVisibleLeaf(element);
                IElementType prevVisibleSiblingElementType = ((LeafPsiElement) prevVisibleSibling).getElementType();

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
                if (PsiTreeUtil.prevVisibleLeaf(prevVisibleSibling) != null) {

                    PsiElement prevPreVisibleSibling = PsiTreeUtil.prevVisibleLeaf(prevVisibleSibling);
                    //Handling suggestions in a query
                    if (PsiTreeUtil.getParentOfType(element, ExecutionElementNode.class) != null) {
                        executionElementRelatedKeywordCompletion(result, element, prevVisibleSibling,
                                prevVisibleSiblingElementType, prevPreVisibleSibling);
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
                }
                //Adding suggestions after a comment
                if (prevVisibleSibling instanceof PsiComment && (prevVisibleSibling.getParent() instanceof SiddhiFile
                        || prevVisibleSibling.getParent() instanceof ParseNode || ((prevVisibleSibling.getParent()
                        instanceof PsiErrorElement) && prevVisibleSibling.getParent().getParent() instanceof
                        SiddhiFile))) {
                    addInitialTypesAsLookups(result);
                }
            }
        }
    }

    private void windowDefinitionRelatedKeywordCompletion(@NotNull CompletionResultSet result, PsiElement element,
                                                          PsiElement prevVisibleSibling, IElementType
                                                                  prevVisibleSiblingElementType) {
        if (PsiTreeUtil.prevVisibleLeaf(prevVisibleSibling) != null) {
            PsiElement prevPrevVisibleSibling = PsiTreeUtil.prevVisibleLeaf(prevVisibleSibling);
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

    private void functionDefinitionRelatedKeywordCompletion(@NotNull CompletionResultSet result, PsiElement element,
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

    private void executionElementRelatedKeywordCompletion(@NotNull CompletionResultSet result, PsiElement element,
                                                          PsiElement prevVisibleSibling, IElementType
                                                                  prevVisibleSiblingElementType, PsiElement
                                                                  prevPreVisibleSibling) {
        //Suggestions related to QueryInputNode
        if (PsiTreeUtil.getParentOfType(element, QueryInputNode.class) != null) {
            if (PsiTreeUtil.getParentOfType(element, StreamIdNode.class) != null && prevVisibleSiblingElementType
                    == SiddhiTypes.FROM) {
                addEveryKeyword(result);
                return;
            }
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, SourceNode.class) != null) {
                addSuggestionsAfterSource(result);
                return;
            }
            if (prevVisibleSiblingElementType == SiddhiTypes.UNIDIRECTIONAL) {
                addSuggestionsAfterUnidirectional(result);
                return;
            }
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, JoinNode.class) != null) {
                addEveryKeyword(result);
                return;
            }
        }
        //Suggestions related to QuerySectionNode
        if (PsiTreeUtil.getParentOfType(element, QuerySectionNode.class) != null) {
            //This provides suggestions after ->(SELECT ('*'| (output_attribute (',' output_attribute)* ))) in
            // query_section1 rule
            if ((prevVisibleSiblingElementType != SiddhiTypes.AS && PsiTreeUtil
                    .getParentOfType(prevVisibleSibling, OutputAttributeNode.class) != null) ||
                    prevVisibleSiblingElementType == SiddhiTypes.STAR) {
                addBeginingOfQueryOutputKeywords(result);
                return;
            }
            //suggesting by keyword after group keyword
            if (prevVisibleSiblingElementType == SiddhiTypes.GROUP) {
                addByKeyword(result);
            }
        }
        //suggesting keywords in the beginning of a query_output rule
        //This provides suggestions after ->OUTPUT output_rate_type? EVERY ( time_value) | OUTPUT SNAPSHOT EVERY
        // time_value  in output_rate rule
        IElementType prevPreVisibleSiblingElementType = ((LeafPsiElement) prevPreVisibleSibling).getElementType();
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, OutputRateNode.class) != null
                && PsiTreeUtil.getParentOfType(prevVisibleSibling, TimeValueNode.class) != null) {
            PsiElement timeValueNodeElement = PsiTreeUtil.getParentOfType(prevVisibleSibling, TimeValueNode.class);
            PsiElement prevSiblingOfTimeValueNode = null;
            if (timeValueNodeElement != null) {
                prevSiblingOfTimeValueNode = PsiTreeUtil.prevVisibleLeaf(timeValueNodeElement);
            }
            IElementType prevSiblingOfTimeValueNodeElementType = null;
            if (prevSiblingOfTimeValueNode != null) {
                prevSiblingOfTimeValueNodeElementType = ((LeafPsiElement) prevSiblingOfTimeValueNode).getElementType();
            }
            if (prevSiblingOfTimeValueNodeElementType == SiddhiTypes.EVERY &&
                    prevVisibleSiblingElementType != SiddhiTypes.INT_LITERAL) {
                addBeginingOfQueryOutputKeywords(result);
                return;
            }
        }
        //This provides suggestions after ->OUTPUT output_rate_type? EVERY INT_LITERAL EVENTS in output_rate rule
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, OutputRateNode.class) != null
                && prevVisibleSiblingElementType == SiddhiTypes.EVENTS
                && prevPreVisibleSiblingElementType == SiddhiTypes.INT_LITERAL) {
            addBeginingOfQueryOutputKeywords(result);
        }
        //This provides suggestions after ->(SELECT ('*'| (output_attribute (',' output_attribute)* ))) having in
        // query_section1 rule
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, ExpressionNode.class) != null
                && PsiTreeUtil.getParentOfType(element, HavingNode.class) != null) {
            addBeginingOfQueryOutputKeywords(result);
        }
        //This provides suggestions after ->(SELECT ('*'| (output_attribute (',' output_attribute)* ))) group_by in
        // query_section1 rule
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, AttributeReferenceNode.class) != null
                && PsiTreeUtil.getParentOfType(element, GroupByNode.class) != null) {
            addBeginingOfQueryOutputKeywords(result);
            addHavingKeyword(result);
        }
        //Suggestions related to QueryOutputNode
        //suggestions after INSERT keyword
        if (prevVisibleSiblingElementType == SiddhiTypes.INSERT && (PsiTreeUtil.getParentOfType
                (prevPreVisibleSibling, OutputRateNode.class) != null || PsiTreeUtil.getParentOfType
                (prevPreVisibleSibling, QuerySectionNode.class) != null || PsiTreeUtil.getParentOfType
                (prevPreVisibleSibling, QueryInputNode.class) != null)) {
            addOutputEventTypeKeywords(result);
            addIntoKeyword(result);
        }
        //suggesting INTO keyword after a output event type in a query
        PsiElement parentOfPrevVisSibling = prevVisibleSibling.getParent();
        if (parentOfPrevVisSibling instanceof OutputEventTypeNode) {
            PsiElement prevVisibleSiblingOfParent = PsiTreeUtil.prevVisibleLeaf(parentOfPrevVisSibling);
            IElementType elementTypeOfPrevVisibleSiblingOfParent = null;
            if (prevVisibleSiblingOfParent != null) {
                elementTypeOfPrevVisibleSiblingOfParent = ((LeafPsiElement) prevVisibleSiblingOfParent)
                        .getElementType();
            }
            if (elementTypeOfPrevVisibleSiblingOfParent == SiddhiTypes.INSERT) {
                addIntoKeyword(result);
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

    @Nullable
    private PsiElement getPreviousVisibleSiblings(int previousPastPositions, @NotNull PsiElement element) {
        PsiElement prevVisibleSibling = element;
        try {
            for (int i = 0; i < previousPastPositions; i++) {
                prevVisibleSibling = PsiTreeUtil.prevVisibleLeaf(prevVisibleSibling);
            }
            return prevVisibleSibling;
        } catch (NullPointerException exception) {
            return null;
        }
    }
}

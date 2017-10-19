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
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.psi.AnnotationElementNode;
import org.wso2.siddhi.plugins.idea.psi.AnnotationNode;
import org.wso2.siddhi.plugins.idea.psi.AppAnnotationNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeNameNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeTypeNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionNameNode;
import org.wso2.siddhi.plugins.idea.psi.LanguageNameNode;
import org.wso2.siddhi.plugins.idea.psi.OutputEventTypeNode;
import org.wso2.siddhi.plugins.idea.psi.ParseNode;
import org.wso2.siddhi.plugins.idea.psi.SiddhiAppNode;
import org.wso2.siddhi.plugins.idea.psi.SiddhiFile;
import org.wso2.siddhi.plugins.idea.psi.TriggerNameNode;
import org.wso2.siddhi.plugins.idea.psi.WindowDefinitionNode;

import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addAfterATSymbolLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addAtKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addDefineTypesAsLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addEveryKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addInitialTypesAsLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addLanguageTypesKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOutputEventTypeKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addReturnKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addValueTypesAsLookups;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addWindowProcessorTypesAsLookups;

public class SiddhiKeywordsCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition();
        PsiElement parent = element.getParent();

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
                //Annotation highlighter exception
                //suggestions after an annotation //TODO: After annotation suggestions needed to be fixed. CHECK
                if (PsiTreeUtil.getParentOfType(prevVisibleSibling, AnnotationElementNode.class) != null ||
                        PsiTreeUtil.getParentOfType(prevVisibleSibling, AppAnnotationNode.class) != null ||
                        PsiTreeUtil.getParentOfType(prevVisibleSibling, AnnotationNode.class) != null) {
                    addInitialTypesAsLookups(result);
                    return;
                }
                //suggestions after an attribute type
                if (PsiTreeUtil.getParentOfType(prevVisibleSibling, AttributeNameNode.class) != null) {
                    addValueTypesAsLookups(result);
                    return;
                }
                //suggestions after a trigger name node AT keyword
                if (PsiTreeUtil.getParentOfType(prevVisibleSibling, TriggerNameNode.class) != null) {
                    addAtKeyword(result);
                    return;
                }
                if (PsiTreeUtil.prevVisibleLeaf(prevVisibleSibling) != null) {
                    //after AT in a Trigger definition, suggest EVERY keyword
                    PsiElement prevPreVisibleSibling = PsiTreeUtil.prevVisibleLeaf(prevVisibleSibling);
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
}

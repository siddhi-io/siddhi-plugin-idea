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

package org.wso2.plugins.idea.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.wso2.plugins.idea.SiddhiTypes;
import org.wso2.plugins.idea.psi.*;

import static org.wso2.plugins.idea.completion.SiddhiCompletionUtils.*;

public class SiddhiKeywordsCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition();

        if (element instanceof LeafPsiElement) {
            if (((LeafPsiElement) element).getElementType() != null) {
                IElementType elementType = ((LeafPsiElement) element).getElementType();
                if (elementType == SiddhiTypes.IDENTIFIER) {
                    if (PsiTreeUtil.prevVisibleLeaf(element) != null) { //gives null in first line first character
                        PsiElement prevVisibleSibling = PsiTreeUtil.prevVisibleLeaf(element);
                        IElementType prevVisibleSiblingElementType = ((LeafPsiElement) prevVisibleSibling)
                                .getElementType();

                        if(prevVisibleSiblingElementType==SiddhiTypes.SEMI_COLON){
                            addInitialTypesAsLookups(result);
                            return;
                        }
                        //After @ symbol suggestions
                        if (prevVisibleSiblingElementType == SiddhiTypes.AT_SYMBOL) {
                            addAfterATSymbolLookups(result);
                            return;
                        }
                        //suggestions after define
                        if (prevVisibleSiblingElementType == SiddhiTypes.DEFINE) {
                            addDefineTypesAsLookups(result);
                            return;
                        }
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
                            //after AT in a Trigger definition EVERY keyword suggestion
                            PsiElement prevPreVisibleSibling = PsiTreeUtil.prevVisibleLeaf(prevVisibleSibling);
                            if (prevVisibleSiblingElementType == SiddhiTypes.AT && PsiTreeUtil.getParentOfType
                                    (prevPreVisibleSibling, TriggerNameNode.class) != null) {
                                addEveryKeyword(result);
                                return;
                            }
                            //Window definitions suggestions
                            if (PsiTreeUtil.getParentOfType(element, WindowDefinitionNode.class) != null) {
                                windowDefinitionKeywordCompletion(result, element, prevVisibleSibling,
                                        prevVisibleSiblingElementType);
                                return;
                            }
                            //suggestions related to function definition
                            if (PsiTreeUtil.getParentOfType(element, LanguageNameNode.class) != null && PsiTreeUtil
                                    .getParentOfType(prevPreVisibleSibling, FunctionNameNode.class) != null
                                && prevVisibleSiblingElementType==SiddhiTypes.OPEN_SQUARE_BRACKETS) {
                                addLanguageTypesKeywords(result);
                                return;
                            }
                            if(element.getParent().getPrevSibling()!=null){
                                if (prevVisibleSiblingElementType==SiddhiTypes.CLOSE_SQUARE_BRACKETS && PsiTreeUtil
                                        .getParentOfType(prevPreVisibleSibling, LanguageNameNode.class) != null &&
                                        element.getParent().getPrevSibling() instanceof PsiWhiteSpace) {
                                    addReturnKeyword(result);
                                    return;
                                }
                            }
                            if (((LeafPsiElement) prevVisibleSibling).getElementType() != null) {
                                IElementType prevPrevVisibleSiblingElementType = ((LeafPsiElement) prevPreVisibleSibling)
                                        .getElementType();
                                if (PsiTreeUtil.getParentOfType(element, FunctionDefinitionNode.class) != null &&
                                        prevVisibleSiblingElementType==SiddhiTypes.RETURN &&
                                        prevPrevVisibleSiblingElementType==SiddhiTypes.CLOSE_SQUARE_BRACKETS) {
                                    addValueTypesAsLookups(result);
                                    return;
                                }
                                return;
                            }
                            //TODO: add aggregation definition
                        }
                    }
                    //Initial suggestions
                    if(element.getParent().getParent()!=null){
                        PsiElement parentOfParent=element.getParent().getParent();
                        if(parentOfParent instanceof SiddhiAppNode) {
                            addInitialTypesAsLookups(result);
                            return;
                        }
                    }
                    //TODO:Logic needed to be updated. Still after a comment initial types doesn't suggest
                    if(PsiTreeUtil.prevVisibleLeaf(element)!=null){
                        PsiElement prevVisibleSibling = PsiTreeUtil.prevVisibleLeaf(element);
                        if(prevVisibleSibling instanceof PsiComment){
                            addInitialTypesAsLookups(result);
                            return;
                        }
                    }
                }
            }
        }
    }

    public void windowDefinitionKeywordCompletion(@NotNull CompletionResultSet result, PsiElement element, PsiElement
            prevVisibleSibling, IElementType prevVisibleSiblingElementType) {

        if(PsiTreeUtil.prevVisibleLeaf(prevVisibleSibling)!=null) {
            PsiElement prevPrevVisibleSibling = PsiTreeUtil.prevVisibleLeaf(prevVisibleSibling);
            if(element.getParent().getParent().getPrevSibling()!=null){
                if (prevVisibleSiblingElementType == SiddhiTypes.CLOSE_PAR && PsiTreeUtil.getParentOfType
                        (prevPrevVisibleSibling, AttributeTypeNode.class) != null && element.getParent()
                        .getParent().getPrevSibling() instanceof PsiWhiteSpace) {
                    addWindowProcessorTypesAsLookups(result);
                    return;
                }
            }
        }

        if(PsiTreeUtil.getParentOfType(element, OutputEventTypeNode.class) != null){
            addOutputEventTypeKeywords(result);
            return;
        }
    }
}

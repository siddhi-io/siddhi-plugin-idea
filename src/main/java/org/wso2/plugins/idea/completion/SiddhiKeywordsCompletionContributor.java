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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;
import org.wso2.plugins.idea.SiddhiTypes;
import org.wso2.plugins.idea.psi.*;

import static org.wso2.plugins.idea.completion.SiddhiCompletionUtils.*;

public class SiddhiKeywordsCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition();
        PsiElement parent = element.getParent();

        if (element instanceof LeafPsiElement) {
            IElementType elementType = ((LeafPsiElement) element).getElementType();//TODO: Handle null exception.
            if (elementType == SiddhiTypes.IDENTIFIER) {
                if (PsiTreeUtil.prevVisibleLeaf(element) != null) { //gives null in first line first character
                    PsiElement prevVisibleSibling = PsiTreeUtil.prevVisibleLeaf(element);
                    IElementType prevVisibleSiblingElementType = ((LeafPsiElement) prevVisibleSibling).getElementType();
                    if (prevVisibleSiblingElementType == SiddhiTypes.DEFINE) {
                        addDefineTypesAsLookups(result);
                        return;
                    }
                    TriggerDefinitionNode triggerDefinitionNode = PsiTreeUtil.getParentOfType(element,
                            TriggerDefinitionNode.class);
                    if (triggerDefinitionNode != null) {
                        TriggerNameNode triggerNameNode = PsiTreeUtil.getParentOfType(prevVisibleSibling,
                                TriggerNameNode.class);
                        if (triggerNameNode != null) {
                            addAtKeyword(result);
                            return;
                        }
                        if (prevVisibleSiblingElementType == SiddhiTypes.AT) {
                            addEveryKeyword(result);
                            return;
                        }
                    }
                    //TODO: Include these statements in different methods
                    WindowDefinitionNode windowDefinitionNode = PsiTreeUtil.getParentOfType(element,
                            WindowDefinitionNode.class);
                    if (windowDefinitionNode != null) {
//                        FunctionOperationNode functionOperationNode = PsiTreeUtil.getParentOfType(element,
//                                FunctionOperationNode.class);
                        if (prevVisibleSiblingElementType == SiddhiTypes.CLOSE_PAR ){//TODO: if not a error element
                            // don't sufggest //Update the logic //event after sufggesion para it suggests
                            // &&
                            // functionOperationNode !=
                            // null) {
                            addWindowProcessorTypesAsLookups(result);
                            return;
                        }//TODO: handle the whitespace
//                        PsiElement prevSibling = element.getPrevSibling();
//                        boolean withWhitespace=false;
//                        if(prevSibling==null || !(prevSibling instanceof PsiWhiteSpace)) {
//                            addWindowProcessorTypesAsLookups(result);
//                            return;
//                        }
                    }
                }
            }
        }

        if (parent instanceof PsiErrorElement) {
            //PsiElement parentOfParent = parent.getParent();
            //PsiElement prevVisibleSibling = PsiTreeUtil.prevVisibleLeaf(parent);
            PsiElement prevVisibleSibling = PsiTreeUtil.prevVisibleLeaf(element);
            AttributeNameNode attributeNameNode = PsiTreeUtil.getParentOfType(prevVisibleSibling,
                    AttributeNameNode.class);//TODO: check null
            if(attributeNameNode!=null){
            //if (parentOfParent instanceof AttributeTypeNode) {
                addValueTypesAsLookups(result);//TODO: Value Types doesn't show up

            } else {
                addInitialTypesAsLookups(result);//TODO: Adjust the suggestion place
            }
        }


    }
}

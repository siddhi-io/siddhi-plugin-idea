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

package org.wso2.siddhi.plugins.idea.psi.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils;
import org.wso2.siddhi.plugins.idea.psi.AttributeNameNode;
import org.wso2.siddhi.plugins.idea.psi.IdentifierPSINode;
import org.wso2.siddhi.plugins.idea.psi.QueryInputNode;
import org.wso2.siddhi.plugins.idea.psi.QueryNode;
import org.wso2.siddhi.plugins.idea.psi.StandardStreamNode;
import org.wso2.siddhi.plugins.idea.psi.StreamDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.StreamIdNode;

import java.util.Arrays;
import java.util.List;

public class AttributeNameReference extends SiddhiElementReference {

    public AttributeNameReference(@Nonnull IdentifierPSINode element) {
        super(element);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return super.resolve();
    }

    @Nonnull
    @Override
    public Object[] getVariants() {
        //TODO: resolve attribute types for streams created in the output stream in query output
        //TODO:restrict attribute name suggestions to only used in the select phrase for group by clause
        //TODO:restrict attribute name suggestions to only used in the select phrase
        //TODO:restrict attribute suggestions in delete,update or insert,update in query output
        //TODO: add attribute suggestions after on in queries ex: on Table.roomNo == Rstream.roomNo
        IdentifierPSINode identifier = getElement();
        PsiFile psiFile = identifier.getContainingFile();
        //Avoiding suggesting attributes after group keyword
        PsiElement prevVisibleSibling = PsiTreeUtil.prevVisibleLeaf(identifier);
        if (prevVisibleSibling != null) {
            IElementType prevVisibleSiblingElementType = ((LeafPsiElement) prevVisibleSibling).getElementType();
            if (prevVisibleSiblingElementType == SiddhiTypes.GROUP) {
                return (new LookupElement[0]);
            }
        }

        List attributeNameNodes = null;
        if (PsiTreeUtil.getParentOfType(identifier, QueryNode.class) != null) {
            PsiElement queryNodeElement = PsiTreeUtil.getParentOfType(identifier, QueryNode.class);
            PsiElement queryInputNodeElement = PsiTreeUtil.getChildOfType(queryNodeElement, QueryInputNode.class);
            //suggestions for Standard stream query input
            PsiElement standardStreamNode = PsiTreeUtil.getChildOfType(queryInputNodeElement, StandardStreamNode.class);
            PsiElement deepestVisibleLastElement;
            if (standardStreamNode != null) {
                deepestVisibleLastElement = PsiTreeUtil.getDeepestVisibleLast(standardStreamNode);
            } else {
                return (new LookupElement[0]);
            }
            IElementType deepestVisibleLastElementType;
            if (deepestVisibleLastElement != null) {
                deepestVisibleLastElementType = ((LeafPsiElement) deepestVisibleLastElement).getElementType();
            } else {
                return (new LookupElement[0]);
            }
            String streamName = "";
            if (deepestVisibleLastElementType == SiddhiTypes.IDENTIFIER) {
                streamName = deepestVisibleLastElement.getText();
            }
            List streamNodes = Arrays.asList((PsiTreeUtil.findChildrenOfType(psiFile, StreamIdNode.class).toArray()));
            for (Object streamNode : streamNodes) {
                PsiElement element = (StreamIdNode) streamNode;
                if (!streamName.equalsIgnoreCase("") && element.getText().equalsIgnoreCase(streamName) && PsiTreeUtil
                        .getParentOfType(element, StreamDefinitionNode.class) != null) {
                    StreamDefinitionNode streamDefinitionNode = PsiTreeUtil.getParentOfType(element, StreamDefinitionNode.class);
                    attributeNameNodes = Arrays.asList((PsiTreeUtil.findChildrenOfType(streamDefinitionNode,
                            AttributeNameNode.class).toArray()));
                    break;
                }
            }
        }
        List<LookupElement> results = null;
        if (attributeNameNodes != null) {
            results = SiddhiCompletionUtils.createAttributeNameLookupElements(attributeNameNodes
                    .toArray());
        }
        if (results != null) {
            return results.toArray(new LookupElement[results.size()]);
        }
        return (new LookupElement[0]);
    }
}

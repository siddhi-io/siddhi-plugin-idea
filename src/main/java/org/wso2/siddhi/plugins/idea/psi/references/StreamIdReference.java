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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils;
import org.wso2.siddhi.plugins.idea.psi.AggregationDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.BasicSourceNode;
import org.wso2.siddhi.plugins.idea.psi.IdentifierPSINode;
import org.wso2.siddhi.plugins.idea.psi.JoinSourceNode;
import org.wso2.siddhi.plugins.idea.psi.QueryOutputNode;
import org.wso2.siddhi.plugins.idea.psi.RightSourceNode;
import org.wso2.siddhi.plugins.idea.psi.StandardStreamNode;
import org.wso2.siddhi.plugins.idea.psi.StreamDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.StreamIdNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.wso2.siddhi.plugins.idea.completion.util.KeywordCompletionUtils.
        getPreviousVisibleSiblingSkippingComments;

/**
 * Provides element reference for stream ids.
 */
public class StreamIdReference extends SiddhiElementReference {

    public StreamIdReference(@NotNull IdentifierPSINode element) {
        super(element);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return super.resolve();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        IdentifierPSINode identifier = getElement();
        int caretOffSet = identifier.getTextOffset();
        /*
          We suggest stream ids in the following places
          1. after "from" in a Standard Stream node
          2. if the parent is a Basic Source node
          3. after "insert into"(not "update or insert into") clause in the query output node
          4. if the parent is a Join Source node
          5. Right Source Node ex: from stream1 join stream2--In here if the user request suggestions in the before
                s(still he entered stream2) in stream2 we need to provide suggestions.
                ***In here we need to avoid suggesting stream ids after a unidirectional keyword.***
        */
        if (PsiTreeUtil.getParentOfType(identifier, StandardStreamNode.class) != null
                || PsiTreeUtil.getParentOfType(identifier, BasicSourceNode.class) != null
                || PsiTreeUtil.getParentOfType(identifier, JoinSourceNode.class) != null
                || PsiTreeUtil.getParentOfType(identifier, RightSourceNode.class) != null
                || PsiTreeUtil.getParentOfType(identifier, QueryOutputNode.class) != null) {
            try {
                if ((PsiTreeUtil.getParentOfType(identifier, RightSourceNode.class) != null
                        && ((LeafPsiElement) PsiTreeUtil.prevVisibleLeaf(identifier)).getElementType() ==
                        SiddhiTypes.UNIDIRECTIONAL)) {
                    return new LookupElement[0];
                }
            } catch (NullPointerException e) {
                return new LookupElement[0];
            }
            PsiFile psiFile = identifier.getContainingFile();
            // for aggregation definitions we need to suggest only stream definitions. All the table, window
            // definitions should be removed
            if (PsiTreeUtil.getParentOfType(identifier, AggregationDefinitionNode.class) != null) {
                PsiElement prevVisibleSibling = getPreviousVisibleSiblingSkippingComments(identifier);
                if (prevVisibleSibling == null) {
                    return new LookupElement[0];
                }
                IElementType prevVisibleSiblingElementType = null;
                if (prevVisibleSibling instanceof LeafPsiElement) {
                    prevVisibleSiblingElementType = ((LeafPsiElement) prevVisibleSibling).getElementType();
                }
                //StreamIds should be suggested only after the 'from' clause in aggregation definition
                if (prevVisibleSiblingElementType != SiddhiTypes.FROM) {
                    return new LookupElement[0];
                }
                // streamDefinitionNodes has all stream definitions
                List streamDefinitionNodes = Arrays.asList((PsiTreeUtil.findChildrenOfType(psiFile,
                        StreamDefinitionNode.class).toArray()));
                List<StreamIdNode> streamIdNodesInStreamDefinitionsUpToCursorPoint = new ArrayList<>();
                for (Object streamDefinition: streamDefinitionNodes) {
                    PsiElement streamDefinitionNode = ((StreamDefinitionNode) streamDefinition);
                    StreamIdNode streamDefinitionNodeIdentifier = PsiTreeUtil.findChildOfType(streamDefinitionNode,
                            StreamIdNode.class);
                    if (streamDefinitionNodeIdentifier != null
                            && streamDefinitionNodeIdentifier.getTextOffset() < caretOffSet) {
                        streamIdNodesInStreamDefinitionsUpToCursorPoint.add(streamDefinitionNodeIdentifier);
                    }
                }
                List<LookupElement> results = SiddhiCompletionUtils.createSourceLookupElements
                        (streamIdNodesInStreamDefinitionsUpToCursorPoint.toArray());
                return results.toArray(new LookupElement[results.size()]);
            }
            List streamDefinitionNodesInFile = Arrays.asList((PsiTreeUtil.findChildrenOfType(psiFile,
                    StreamIdNode.class).toArray()));
            List<StreamIdNode> streamDefinitionNodesUpToCursorPoint = new ArrayList<>();
            for (Object streamDefinitionNode : streamDefinitionNodesInFile) {
                StreamIdNode streamDefinitionNodeIdentifier = ((StreamIdNode) streamDefinitionNode);
                if (streamDefinitionNodeIdentifier != null
                        && streamDefinitionNodeIdentifier.getTextOffset() < caretOffSet) {
                    streamDefinitionNodesUpToCursorPoint.add(streamDefinitionNodeIdentifier);
                }
            }
            List<LookupElement> results = SiddhiCompletionUtils.createSourceLookupElements
                    (streamDefinitionNodesUpToCursorPoint.toArray());
            return results.toArray(new LookupElement[results.size()]);
        }
        return new LookupElement[0];
    }
}

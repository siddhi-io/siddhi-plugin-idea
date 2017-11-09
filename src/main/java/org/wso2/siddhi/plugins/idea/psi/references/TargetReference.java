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
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils;
import org.wso2.siddhi.plugins.idea.psi.IdentifierPSINode;
import org.wso2.siddhi.plugins.idea.psi.OutputEventTypeNode;
import org.wso2.siddhi.plugins.idea.psi.StreamIdNode;
import org.wso2.siddhi.plugins.idea.psi.TableDefinitionNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TargetReference extends SiddhiElementReference {

    public TargetReference(@NotNull IdentifierPSINode element) {
        super(element);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return super.resolve();
    }

    /**
     * In this method only the table names are filtered and make lookups elements
     **/
    @NotNull
    @Override
    public Object[] getVariants() {
        IdentifierPSINode identifier = getElement();
        int caretOffSet = identifier.getTextOffset();
        //Stopping suggestions after output event type in a query. Ex: insert currents events _a --in place a editor
        // suggests target node names since the psi tree doesn't recognise that the error should be in the into element
        if (PsiTreeUtil.prevVisibleLeaf(identifier) != null) {
            PsiElement prevVisibleSibling = PsiTreeUtil.prevVisibleLeaf(identifier);
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, OutputEventTypeNode.class) != null) {
                return new LookupElement[0];
            }
        }
        PsiFile psiFile = identifier.getContainingFile();
        List streamIdNodesWithDuplicates = Arrays.asList((PsiTreeUtil.findChildrenOfType(psiFile, StreamIdNode
                .class).toArray()));
        List<StreamIdNode> tableDefinitionNodesWithoutDuplicates = new ArrayList<>();
        for (Object streamIdNode : streamIdNodesWithDuplicates) {

            PsiElement streamIdNodeIdentifier = (StreamIdNode) streamIdNode;

            if (streamIdNodeIdentifier != null
                    && PsiTreeUtil.getParentOfType(streamIdNodeIdentifier, TableDefinitionNode.class) != null
                    && streamIdNodeIdentifier.getTextOffset() < caretOffSet) {
                tableDefinitionNodesWithoutDuplicates.add((StreamIdNode) streamIdNodeIdentifier);
            }
        }
        List<LookupElement> results = SiddhiCompletionUtils.createEventTableLookupElements
                (tableDefinitionNodesWithoutDuplicates.toArray());
        return results.toArray(new LookupElement[results.size()]);
    }
}

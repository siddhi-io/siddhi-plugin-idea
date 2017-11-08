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
import org.wso2.siddhi.plugins.idea.psi.BasicSourceNode;
import org.wso2.siddhi.plugins.idea.psi.IdentifierPSINode;
import org.wso2.siddhi.plugins.idea.psi.JoinSourceNode;
import org.wso2.siddhi.plugins.idea.psi.QueryOutputNode;
import org.wso2.siddhi.plugins.idea.psi.StandardStreamNode;
import org.wso2.siddhi.plugins.idea.psi.StreamIdNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        int caretOffSet=identifier.getTextOffset();
        //TODO:check(with the grammar file) the actual place that can be applied these stream name
        /*
          We suggest stream ids in the following places
          1. after "from" in a Standard Stream node
          2. if the parent is a Basic Source node
          3. after "insert into"(not "update or insert into") clause in the query output node
          4. if the parent is a Join Source node
        */
        if(PsiTreeUtil.getParentOfType(identifier,StandardStreamNode.class)!=null
                || PsiTreeUtil.getParentOfType(identifier, BasicSourceNode.class)!=null
                || PsiTreeUtil.getParentOfType(identifier, JoinSourceNode.class)!=null
                || PsiTreeUtil.getParentOfType(identifier, QueryOutputNode.class)!=null) {
            PsiFile psiFile = identifier.getContainingFile();
            List streamDefinitionNodesWithDuplicates = Arrays.asList((PsiTreeUtil.findChildrenOfType(psiFile, StreamIdNode
                    .class).toArray()));
            List<StreamIdNode> streamDefinitionNodesWithoutDuplicates = new ArrayList<>();
            for (Object streamDefinitionNode : streamDefinitionNodesWithDuplicates) {
                PsiElement streamDefinitionNodeIdentifier = ((StreamIdNode) streamDefinitionNode);
                if (streamDefinitionNodeIdentifier != null && streamDefinitionNodeIdentifier.getTextOffset() < caretOffSet) {
                    streamDefinitionNodesWithoutDuplicates.add((StreamIdNode) streamDefinitionNodeIdentifier);
                }
            }
            List<LookupElement> results = SiddhiCompletionUtils.createSourceLookupElements
                    (streamDefinitionNodesWithoutDuplicates.toArray());
            return results.toArray(new LookupElement[results.size()]);
        }
        return new LookupElement[0];
    }
}

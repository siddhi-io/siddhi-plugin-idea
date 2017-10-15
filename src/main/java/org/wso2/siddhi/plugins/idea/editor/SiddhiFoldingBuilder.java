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

package org.wso2.siddhi.plugins.idea.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.folding.NamedFoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.wso2.siddhi.plugins.idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class SiddhiFoldingBuilder extends CustomFoldingBuilder implements DumbAware {

    @Override
    protected void buildLanguageFoldRegions(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiElement root,
                                            @NotNull Document document, boolean quick) {
        if (!(root instanceof SiddhiFile)) {
            return;
        }
        buildQueryFoldRegions(descriptors, root);
    }

    private void buildQueryFoldRegions(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiElement root) {
        // Get all the query input nodes.
        Collection<QueryNode> queryNodes = PsiTreeUtil.findChildrenOfType(root, QueryNode
                .class);
        for (QueryNode queryNode : queryNodes) {
            // Get the function body. This is used to calculate the start offset.
            QueryInputNode callableUnitBodyNode = PsiTreeUtil.getChildOfType(queryNode,
                    QueryInputNode.class);
            if (callableUnitBodyNode == null) {
                continue;
            }
            // Add folding descriptor.
            addFoldingDescriptor(descriptors, queryNode, callableUnitBodyNode);
        }
    }

    private void addFoldingDescriptor(@NotNull List<FoldingDescriptor> descriptors, PsiElement node,
                                      PsiElement bodyNode) {
        // Calculate the start and end offsets.
        int startOffset = bodyNode.getTextRange().getStartOffset();
        int endOffset = node.getTextRange().getEndOffset();
        // Add the new folding descriptor.
        descriptors.add(new NamedFoldingDescriptor(node, startOffset, endOffset, null, "{...}"));
    }

    @Override
    protected String getLanguagePlaceholderText(@NotNull ASTNode node, @NotNull TextRange range) {
        return "...";
    }

    @Override
    protected boolean isRegionCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}

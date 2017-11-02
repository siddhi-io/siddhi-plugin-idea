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

package org.wso2.siddhi.plugins.idea.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.wso2.siddhi.plugins.idea.SiddhiTypes.*;

public class SiddhiBlock extends AbstractBlock {

    private SpacingBuilder spacingBuilder;

    @NotNull
    private final ASTNode node;
    @Nullable
    private final Alignment alignment;
    @Nullable
    private final Indent indent;
    @Nullable
    private final Wrap wrap;
    @NotNull
    private final CodeStyleSettings mySettings;
    @NotNull
    private final SpacingBuilder mySpacingBuilder;
    @Nullable
    private List<Block> mySubBlocks;


    protected SiddhiBlock(@NotNull ASTNode node, @Nullable Alignment alignment, @Nullable Indent indent, @Nullable
            Wrap wrap, @NotNull CodeStyleSettings settings, SpacingBuilder spacingBuilder) {
        super(node, wrap, alignment);

        this.node = node;
        this.alignment = alignment;
        this.indent = indent;
        this.wrap = wrap;
        this.mySettings = settings;
        this.mySpacingBuilder = spacingBuilder;
        this.spacingBuilder = spacingBuilder;
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> blocks = new ArrayList<>();
        ASTNode child = node.getFirstChildNode();
        IElementType parentElementType = node.getElementType();

        while (child != null) {
            IElementType childElementType = child.getElementType();
            if (childElementType != TokenType.WHITE_SPACE) {

                Indent indent = Indent.getNoneIndent();

                if (isInsideADefinitionElement(childElementType)) {
                    if (child.getFirstChildNode() != null && child.getLastChildNode() != null) {
                        indent = Indent.getSpaceIndent(4);
                    }
                } else if (childElementType == MULTILINE_COMMENT) {
                    if (isADefinitionElement(parentElementType) || isACodeBlock(parentElementType)) {
                        indent = Indent.getSpaceIndent(4);
                    }
                }
                // If the child node text is empty, the IDEA core will throw an exception.
                if (!child.getText().isEmpty()) {
                    Block block = new SiddhiBlock(
                            child,
                            Alignment.createAlignment(),
                            indent,
                            null,
                            mySettings,
                            spacingBuilder
                    );
                    blocks.add(block);
                }
            }
            child = child.getTreeNext();
        }
        return blocks;
    }

    private static boolean isADefinitionElement(@NotNull final IElementType parentElementType) {
        return false;
    }

    private static boolean isInsideADefinitionElement(@NotNull final IElementType childElementType) {
        return false;
    }

    private static boolean isACodeBlock(@NotNull final IElementType parentElementType) {
        return false;
    }

    @Override
    public Indent getIndent() {
        return indent;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return spacingBuilder.getSpacing(this, child1, child2);
    }

    @Override
    public boolean isLeaf() {
        return node.getFirstChildNode() == null;
    }
}

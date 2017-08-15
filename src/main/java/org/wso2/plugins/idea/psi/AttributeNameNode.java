package org.wso2.plugins.idea.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import org.antlr.jetbrains.adaptor.psi.IdentifierDefSubtree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.plugins.idea.SiddhiIcons;
import org.wso2.plugins.idea.SiddhiTypes;
import org.wso2.plugins.idea.psi.impl.SiddhiItemPresentation;

import javax.swing.*;

public class AttributeNameNode extends IdentifierDefSubtree {

    public AttributeNameNode(@NotNull ASTNode node) {
        super(node, SiddhiTypes.IDENTIFIER);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new SiddhiItemPresentation(getNameIdentifier()) {

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return SiddhiIcons.PARAMETER;
            }
        };
    }
}

//TODO: Add item presentation method for all the suitable classes.
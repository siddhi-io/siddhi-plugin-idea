package org.wso2.plugins.idea.psi;

import com.intellij.lang.ASTNode;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class AttributeTypeNode extends ANTLRPsiNode {
    public AttributeTypeNode(@NotNull ASTNode node) {
        super(node);

    }
}

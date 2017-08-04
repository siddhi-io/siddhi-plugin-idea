package org.wso2.plugins.idea.psi;

import com.intellij.lang.ASTNode;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class FunctionBodyNode extends ANTLRPsiNode {
    public FunctionBodyNode(@NotNull ASTNode node) {
        super(node);
    }
}

package org.wso2.plugins.idea.psi;

import com.intellij.lang.ASTNode;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class FunctionNameNode extends ANTLRPsiNode {
    public FunctionNameNode(@NotNull ASTNode node) {
        super(node);
    }
}

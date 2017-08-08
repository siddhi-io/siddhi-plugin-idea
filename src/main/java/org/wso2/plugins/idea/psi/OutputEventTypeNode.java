package org.wso2.plugins.idea.psi;

import com.intellij.lang.ASTNode;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class OutputEventTypeNode extends ANTLRPsiNode {
    public OutputEventTypeNode(@NotNull ASTNode node) {
        super(node);
    }
}
package org.wso2.siddhi.plugins.idea.psi;

import com.intellij.lang.ASTNode;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class LeftSourceNode extends ANTLRPsiNode {
    public LeftSourceNode(@NotNull ASTNode node) {
        super(node);
    }
}

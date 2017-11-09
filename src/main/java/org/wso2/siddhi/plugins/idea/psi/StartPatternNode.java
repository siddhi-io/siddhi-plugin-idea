package org.wso2.siddhi.plugins.idea.psi;

import com.intellij.lang.ASTNode;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class StartPatternNode extends ANTLRPsiNode {
    public StartPatternNode(@NotNull ASTNode node) {
        super(node);
    }
}

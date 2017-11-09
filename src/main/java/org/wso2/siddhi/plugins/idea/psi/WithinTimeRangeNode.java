package org.wso2.siddhi.plugins.idea.psi;

import com.intellij.lang.ASTNode;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class WithinTimeRangeNode extends ANTLRPsiNode {
    public WithinTimeRangeNode(@NotNull ASTNode node) {
        super(node);
    }
}

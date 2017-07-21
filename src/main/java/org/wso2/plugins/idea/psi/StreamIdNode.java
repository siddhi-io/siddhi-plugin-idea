package org.wso2.plugins.idea.psi;

import com.intellij.lang.ASTNode;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class StreamIdNode extends ANTLRPsiNode {

    public StreamIdNode(@NotNull ASTNode node) {
        super(node);
    }
}

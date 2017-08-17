package org.wso2.plugins.idea.psi;

import com.intellij.lang.ASTNode;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class AppAnnotationNode extends ANTLRPsiNode {
    public AppAnnotationNode(@NotNull ASTNode node) {
        super(node);
    }
}
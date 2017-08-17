package org.wso2.plugins.idea.psi;

import com.intellij.lang.ASTNode;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class AnnotationElementNode extends ANTLRPsiNode {
    public AnnotationElementNode(@NotNull ASTNode node) {
        super(node);
    }
}
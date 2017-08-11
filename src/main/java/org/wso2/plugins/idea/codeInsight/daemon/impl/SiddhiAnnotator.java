package org.wso2.plugins.idea.codeInsight.daemon.impl;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.wso2.plugins.idea.highlighter.SiddhiSyntaxHighlightingColors;
import org.wso2.plugins.idea.psi.AttributeNameNode;
import org.wso2.plugins.idea.psi.StreamIdNode;


public class SiddhiAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if ( element instanceof StreamIdNode) { //PsiTreeUtil.getParentOfType(element.getParent(), AttributeNameNode
            // .class).
            annotateStreamIdNodes(element, holder);
        }
    }

    private void annotateStreamIdNodes(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        Annotation annotation = holder.createInfoAnnotation(element.getTextRange(), null);
        annotation.setTextAttributes(SiddhiSyntaxHighlightingColors.STREAM_ID);
    }

}

package org.wso2.plugins.idea.codeInsight;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.wso2.plugins.idea.highlighter.SiddhiSyntaxHighlightingColors;
import org.wso2.plugins.idea.psi.StreamIdNode;


public class SiddhiAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof StreamIdNode) {
            annotateStreamIdNodes(element, holder);
        }
    }

    private void annotateStreamIdNodes(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        Annotation annotation = holder.createInfoAnnotation(element, null);
        annotation.setTextAttributes(SiddhiSyntaxHighlightingColors.STREAM_ID);
    }

}

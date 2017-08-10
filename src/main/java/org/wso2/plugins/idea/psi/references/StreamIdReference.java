package org.wso2.plugins.idea.psi.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.plugins.idea.psi.IdentifierPSINode;

import java.util.LinkedList;
import java.util.List;

public class StreamIdReference extends SiddhiElementReference {

    public StreamIdReference(@NotNull IdentifierPSINode element) {
        super(element);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return super.resolve();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<String> results = new LinkedList<>();
//        results.addAll(BallerinaCompletionUtils.createLambdaFunctionLookupElements(ParenthesisInsertHandler.INSTANCE));
        return results.toArray(new LookupElement[results.size()]);
    }
}

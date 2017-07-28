package org.wso2.plugins.idea.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.antlr.jetbrains.adaptor.psi.ScopeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.plugins.idea.psi.IdentifierPSINode;

public abstract class SiddhiElementReference extends PsiReferenceBase<IdentifierPSINode> {

    public SiddhiElementReference(@NotNull IdentifierPSINode element) {
        /** WARNING: You must send up the text range or you get this error:
         * "Cannot find manipulator for PsiElement(ID) in org.antlr.jetbrains.sample.SampleElementRef"...
         *  when you click on an identifier.  During rename you get this
         *  error too if you don't impl handleElementRename().
         *
         *  The range is relative to start of the token; I guess for
         *  qualified references we might want to use just a part of the name.
         *  Or we might look inside string literals for stuff.
         */
        super(element, new TextRange(0, element.getText().length()));
    }

    /**
     * Change the REFERENCE's ID node (not the targeted def's ID node)
     * to reflect a rename.
     * <p>
     * Without this method, we get an error ("Cannot find manipulator...").
     * <p>
     * getElement() refers to the identifier node that references the definition.
     */
    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return myElement.setName(newElementName);
    }

    /**
     * Resolve a reference to the definition subtree (subclass of
     * IdentifierDefSubtree), do not resolve to the ID child of that
     * definition subtree root.
     */
    @Nullable
    @Override
    public PsiElement resolve() {
        ScopeNode scope = (ScopeNode) myElement.getContext();
        if (scope == null) {
            return null;
        }
        return scope.resolve(myElement);
    }
}
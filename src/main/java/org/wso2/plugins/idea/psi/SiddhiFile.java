package org.wso2.plugins.idea.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.antlr.jetbrains.adaptor.psi.ScopeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.plugins.idea.SiddhiFileType;
import org.wso2.plugins.idea.SiddhiLanguage;

public class SiddhiFile extends PsiFileBase implements ScopeNode {

    public SiddhiFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, SiddhiLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return SiddhiFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Siddhi File";
    }

    @Override
    public ScopeNode getContext() {
        return null;
    }

    @Nullable
    @Override
    public PsiElement resolve(PsiNamedElement element) {
        return null;
    }
}

/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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

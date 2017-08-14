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
package org.wso2.plugins.idea.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.plugins.idea.SiddhiIcons;
import org.wso2.plugins.idea.psi.SiddhiFile;

import java.util.Map;

public class SiddhiCreateFileAction extends CreateFileFromTemplateAction implements DumbAware {
    private static final String FILE_TEMPLATE_NAME = "Siddhi File";
    private static final String MAIN_TEMPLATE_NAME = "Siddhi Main";

    private static final String NEW_SIDDHI_FILE = "New Siddhi File";
    private static final String DEFAULT_SIDDHI_TEMPLATE_PROPERTY = "Siddhi Main";

    public SiddhiCreateFileAction() {
        super(NEW_SIDDHI_FILE, "", SiddhiIcons.ICON);
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory directory,
                               @NotNull CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle(NEW_SIDDHI_FILE).addKind("Siddhi Main", SiddhiIcons.ICON, MAIN_TEMPLATE_NAME);
    }

    @Nullable
    @Override
    protected String getDefaultTemplateProperty() {
        return DEFAULT_SIDDHI_TEMPLATE_PROPERTY;
    }

    @NotNull
    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return NEW_SIDDHI_FILE;
    }


    @Override
    protected void postProcess(PsiFile createdElement, String templateName, Map<String, String> customProperties) {
        if (createdElement instanceof SiddhiFile) {
            // Moving the caret to the end of the file.
            Project project = createdElement.getProject();
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor == null) {
                return;
            }
            VirtualFile virtualFile = createdElement.getContainingFile().getVirtualFile();
            if (virtualFile == null) {
                return;
            }
            PsiElement lastChild = createdElement.getLastChild();
            if (FileDocumentManager.getInstance().getDocument(virtualFile) == editor.getDocument()) {
                editor.getCaretModel().moveToOffset(lastChild.getTextRange().getEndOffset());
            }
        }
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SiddhiCreateFileAction;
    }
}

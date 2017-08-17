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

package org.wso2.plugins.idea.runconfig;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.ide.scratch.ScratchFileType;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.wso2.plugins.idea.SiddhiConstants;
import org.wso2.plugins.idea.SiddhiFileType;
import org.wso2.plugins.idea.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class SiddhiRunUtil {

    private SiddhiRunUtil() {

    }

    @Nullable
    static PsiElement getContextElement(@Nullable ConfigurationContext context) {
        if (context == null) {
            return null;
        }
        PsiElement psiElement = context.getPsiLocation();
        if (psiElement == null || !psiElement.isValid()) {
            return null;
        }

        FileIndexFacade indexFacade = FileIndexFacade.getInstance(psiElement.getProject());
        PsiFileSystemItem psiFile = psiElement instanceof PsiFileSystemItem ? (PsiFileSystemItem) psiElement :
                psiElement.getContainingFile();
        VirtualFile file = psiFile != null ? psiFile.getVirtualFile() : null;
        if (file != null && file.getFileType() != ScratchFileType.INSTANCE &&
                (!indexFacade.isInContent(file) || indexFacade.isExcludedFile(file))) {
            return null;
        }
        return psiElement;
    }

    public static void installSiddhiWithMainFileChooser(Project project,
                                                           @NotNull TextFieldWithBrowseButton fileField) {
        installFileChooser(project, fileField, file ->
                isRunnableSiddhiFile(PsiManager.getInstance(project).findFile(file)));
    }

    @Contract("null -> false")
    private static boolean isRunnableSiddhiFile(@Nullable PsiFile psiFile) {
        return hasExecutionElements(psiFile);
    }

    @Contract("null -> false")
    static boolean hasExecutionElements(PsiFile file) {
        Collection<ExecutionElementNode> executionElementNodes = PsiTreeUtil.findChildrenOfType(file,
                ExecutionElementNode.class);
//        for (ExecutionElementNode executionElementNode : executionElementNodes) {
//            if (isExecutableElement(executionElementNode)) {
//                return true;
//            }
//        }
        if(executionElementNodes!=null){
            return true;
        }
        return false;
    }

    /**
     * Checks whether the given executionElementNode is a main function node.
     *
     * @param executionElementNode FunctionDefinitionNode which needs to be checked
     * @return {@code true} if the provided node is a has execution Elements, {@code false} otherwise.
    */
    @Contract("null -> false")
    static boolean isExecutableElement(ExecutionElementNode executionElementNode) {
        // Get the function name.
//        PsiElement elementName = functionDefinitionNode.getNameIdentifier();
//        if (functionName == null) {
//            return false;
//        }else{
//            return true;
//        }
        //TODO:UPDATE LOGIC
        return true;
    }

    private static void installFileChooser(@NotNull Project project, @NotNull ComponentWithBrowseButton field,
                                           @Nullable Condition<VirtualFile> fileFilter) {
        FileChooserDescriptor chooseDirectoryDescriptor =
                FileChooserDescriptorFactory.createSingleFileDescriptor(SiddhiFileType.INSTANCE);
        chooseDirectoryDescriptor.setRoots(project.getBaseDir());
        chooseDirectoryDescriptor.setShowFileSystemRoots(false);
        chooseDirectoryDescriptor.withShowHiddenFiles(false);
        chooseDirectoryDescriptor.withFileFilter(fileFilter);
        if (field instanceof TextFieldWithBrowseButton) {
            ((TextFieldWithBrowseButton) field).addBrowseFolderListener(
                    new TextBrowseFolderListener(chooseDirectoryDescriptor, project));
        } else {
            //noinspection unchecked
            field.addBrowseFolderListener(project, new ComponentWithBrowseButton.BrowseFolderActionListener(null,
                    null, field, project, chooseDirectoryDescriptor,
                    TextComponentAccessor.TEXT_FIELD_WITH_HISTORY_WHOLE_TEXT));
        }
    }

    public static void printSiddhiEnvVariables(@NotNull GeneralCommandLine commandLine,
                                                  @NotNull ProcessHandler handler) {
        Map<String, String> environment = commandLine.getEnvironment();
        // Todo - Add SIDDHI_REPOSITORY
        //        handler.notifyTextAvailable("SIDDHI_REPOSITORY=" + StringUtil.nullize(environment.get
        //                (SiddhiConstants.SIDDHI_REPOSITORY)) + '\n', ProcessOutputTypes.SYSTEM);
    }

    @Nullable
    public static VirtualFile findByPath(@NotNull String path, @NotNull Project project) {
        String systemIndependentPath = FileUtil.toSystemIndependentName(path);
        VirtualFile projectBaseDir = project.getBaseDir();
        if (systemIndependentPath.isEmpty()) {
            return projectBaseDir;
        }
        return projectBaseDir.findFileByRelativePath(systemIndependentPath);
    }
}

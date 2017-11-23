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

package org.wso2.siddhi.plugins.idea.debugger.breakpoint;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.siddhi.plugins.idea.SiddhiFileType;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.psi.AnonymousStreamNode;
import org.wso2.siddhi.plugins.idea.psi.ExecutionElementNode;
import org.wso2.siddhi.plugins.idea.psi.PartitionNode;
import org.wso2.siddhi.plugins.idea.psi.QueryInputNode;
import org.wso2.siddhi.plugins.idea.psi.QueryNode;

import static org.wso2.siddhi.plugins.idea.completion.util.KeywordCompletionUtils.getNextVisibleSiblingSkippingComments;

/**
 * Implements a new breakpoint type named IN.
 */
public class SiddhiBreakPointTypeIN extends XLineBreakpointType<SiddhiBreakpointProperties> {

    public static final String ID = "SiddhiLineBreakpoint";
    private static final String NAME = "Siddhi breakpoint";

    protected SiddhiBreakPointTypeIN() {
        super(ID, NAME);
    }

    @Nullable
    @Override
    public SiddhiBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        return new SiddhiBreakpointProperties();
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        return line >= 0 && file.getFileType() == SiddhiFileType.INSTANCE && isLineBreakpointAvailable(file, line,
                project);
    }

    private static boolean isLineBreakpointAvailable(@NotNull VirtualFile file, int line, @NotNull Project project) {
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document == null || document.getLineEndOffset(line) == document.getLineStartOffset(line)) {
            return false;
        }
        Checker canPutAtChecker = new Checker();
        XDebuggerUtil.getInstance().iterateLine(project, document, line, canPutAtChecker);
        return canPutAtChecker.isLineBreakpointAvailable();
    }

    private static final class Checker implements Processor<PsiElement> {

        private boolean myIsLineBreakpointAvailable;
        private int counter = 0;

        @Override
        public boolean process(@NotNull PsiElement element) {
            if (PsiTreeUtil.nextVisibleLeaf(element) != null) {
                PsiElement nextVisibleSibling = getNextVisibleSiblingSkippingComments(element);
                IElementType elementType = element.getNode().getElementType();
                if (elementType == SiddhiTypes.FROM
                        //TODO:once the antlr tree collapsing issue fixed remove one getParent() from below
                        && element.getParent().getParent().getParent() instanceof ExecutionElementNode
                        && PsiTreeUtil.getParentOfType(element, QueryNode.class) != null
                        && PsiTreeUtil.getParentOfType(element, PartitionNode.class) == null
                        && PsiTreeUtil.getParentOfType(element, AnonymousStreamNode.class) == null
                        && PsiTreeUtil.getParentOfType(nextVisibleSibling, QueryInputNode.class) != null) {
                    counter = 1;
                    myIsLineBreakpointAvailable = true;
                } else {
                    myIsLineBreakpointAvailable = counter == 1;
                }
            }
            return true;
        }

        public boolean isLineBreakpointAvailable() {
            return myIsLineBreakpointAvailable;
        }
    }
}

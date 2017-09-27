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

package org.wso2.plugins.idea.debugger;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.ThreeState;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XInlineDebuggerDataCallback;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XNavigatable;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XNumericValuePresentation;
import com.intellij.xdebugger.frame.presentation.XStringValuePresentation;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.plugins.idea.highlighter.SiddhiSyntaxHighlightingColors;

import javax.swing.Icon;

public class SiddhiXValue extends XNamedValue {

    @NotNull
    private final SiddhiDebugProcess myProcess;
    @NotNull
    private final Object myValue;
    @NotNull
    private final String myFrameName;
    @Nullable
    private final Icon myIcon;

    SiddhiXValue(@NotNull SiddhiDebugProcess process, @NotNull String frameName, @NotNull String key, @NotNull Object
            value, @Nullable Icon icon) {
        super(key);
        myProcess = process;
        myFrameName = frameName;
        myValue = value;
        myIcon = icon;
    }

    @Override
    public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
        if(myValue instanceof JSONObject || myValue instanceof JSONArray) {
            node.setPresentation(myIcon, getName(),"", true);
        }else if(myValue instanceof String){
            node.setPresentation(AllIcons.Nodes.Property, new XStringValuePresentation(myValue.toString()),
                    false);
        }else if(myValue instanceof Integer){
            node.setPresentation(AllIcons.Nodes.Property, new XNumericValuePresentation(myValue.toString()), false);
        }else if(myValue instanceof Double){
            node.setPresentation(AllIcons.Nodes.Property, new XNumericValuePresentation(myValue.toString()), false);
        }else if(myValue instanceof Boolean){
            node.setPresentation(AllIcons.Nodes.Property, new XValuePresentation() {
                @Override
                public void renderValue(@NotNull XValueTextRenderer renderer) {
                    renderer.renderValue(myValue.toString(), SiddhiSyntaxHighlightingColors.KEYWORD);
                }
            },false);
        }else {
            node.setPresentation(AllIcons.Nodes.Property, "Type",myValue.toString(), false);
        }
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {

        if (myValue instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) this.myValue;
            XValueChildrenList list = new XValueChildrenList();
            jsonObject.keys().forEachRemaining(key ->
                    {
                        Object value = jsonObject.get(key);
                        list.add(key, new SiddhiXValue(myProcess, myFrameName, key, value, AllIcons.Nodes.Field));
                    }
            );
            node.addChildren(list, true);
        }
        if (myValue instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) this.myValue;
            XValueChildrenList list = new XValueChildrenList();
            jsonArray.forEach(o ->
                    {
                        Object value = o.toString();
                        list.add(((XValueNodeImpl) node).getName(), new SiddhiXValue(myProcess, myFrameName, (
                                (XValueNodeImpl) node).getName(), value, AllIcons.Nodes.Parameter));
                    }
            );
            node.addChildren(list, true);
        }
    }

    @Nullable
    private static PsiElement findTargetElement(@NotNull Project project, @NotNull XSourcePosition position,
                                                @NotNull Editor editor, @NotNull String name) {
        // Todo
        return null;
    }

    @Override
    public void computeSourcePosition(@NotNull XNavigatable navigatable) {
        readActionInPooledThread(new Runnable() {

            @Override
            public void run() {
                navigatable.setSourcePosition(findPosition());
            }

            @Nullable
            private XSourcePosition findPosition() {
                XDebugSession debugSession = myProcess.getSession();
                if (debugSession == null) {
                    return null;
                }
                XStackFrame stackFrame = debugSession.getCurrentStackFrame();
                if (stackFrame == null) {
                    return null;
                }
                Project project = debugSession.getProject();
                XSourcePosition position = debugSession.getCurrentPosition();
                Editor editor = ((FileEditorManagerImpl) FileEditorManager.getInstance(project))
                        .getSelectedTextEditor(true);
                if (editor == null || position == null) {
                    return null;
                }
                String name = myName.startsWith("&") ? myName.replaceFirst("\\&", "") : myName;
                PsiElement resolved = findTargetElement(project, position, editor, name);
                if (resolved == null) {
                    return null;
                }
                VirtualFile virtualFile = resolved.getContainingFile().getVirtualFile();
                return XDebuggerUtil.getInstance().createPositionByOffset(virtualFile, resolved.getTextOffset());
            }
        });
    }

    private static void readActionInPooledThread(@NotNull Runnable runnable) {
        ApplicationManager.getApplication().executeOnPooledThread(() ->
                ApplicationManager.getApplication().runReadAction(runnable));
    }

    @NotNull
    @Override
    public ThreeState computeInlineDebuggerData(@NotNull XInlineDebuggerDataCallback callback) {
        computeSourcePosition(callback::computed);
        return ThreeState.YES;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    @Override
    public boolean canNavigateToTypeSource() {
        // Todo
        return false;
    }

    @Override
    public void computeTypeSourcePosition(@NotNull XNavigatable navigatable) {
        // Todo
    }
}

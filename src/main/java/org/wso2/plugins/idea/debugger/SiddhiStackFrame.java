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

import org.json.JSONObject;
import com.google.gson.Gson;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import org.wso2.plugins.idea.debugger.dto.Frame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;

public class SiddhiStackFrame extends XStackFrame {

    private final SiddhiDebugProcess myProcess;
    private final Frame myFrame;

    SiddhiStackFrame(@NotNull SiddhiDebugProcess process, @NotNull Frame frame) {
        myProcess = process;
        myFrame = frame;
    }

    @Nullable
    @Override
    public XDebuggerEvaluator getEvaluator() {
        // Todo - Add evaluator support
        return null;
    }

    /**
     * Returns the source position. This is used to show the debug hit in the file.
     */
    @Nullable
    @Override
    public XSourcePosition getSourcePosition() {
        VirtualFile file = findFile();
        return file == null ? null : XDebuggerUtil.getInstance().createPosition(file, myFrame.getLocation()
                .getLineNumber() - 1);
    }

    @Nullable
    private VirtualFile findFile() {
        String relativePath = myFrame.getFileName();
        Project project = myProcess.getSession().getProject();
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();
        VirtualFile file = null;
        for (VirtualFile contentRoot : contentRoots) {
            String absolutePath = contentRoot.getPath() + Matcher.quoteReplacement(File.separator) + relativePath;
            file = LocalFileSystem.getInstance().findFileByPath(absolutePath);
            if (file != null) {
                break;
            }
        }
        return file;
    }

    /**
     * Customizes the stack name in the Frames sub window in Debug window.
     */
    @Override
    public void customizePresentation(@NotNull ColoredTextContainer component) {
        super.customizePresentation(component);
        component.append(" at ", SimpleTextAttributes.REGULAR_ATTRIBUTES);
        component.append(myFrame.getFrameName() + " : ", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
        component.append(String.valueOf(myFrame.getLocation().getQueryIndex()) + " : ", SimpleTextAttributes
                .REGULAR_BOLD_ATTRIBUTES);
        component.append(myFrame.getLocation().getQueryTerminal(), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
        component.setIcon(AllIcons.Debugger.StackFrame);
    }

    /**
     * Adds variables in the current stack to the node.
     */
    @Override
    public void computeChildren(@NotNull XCompositeNode node) {

        Map<String, Object> queryStateMap = myFrame.getQueryState();
        Object eventInfo = myFrame.getEventInfo();
        queryStateMap.put("Event State", eventInfo);

        Gson gson = new Gson();
        String queryStateJsonObject = gson.toJson(queryStateMap);
        JSONObject jsonObject = new JSONObject(queryStateJsonObject);

        // Create a new XValueChildrenList to hold the XValues.
        XValueChildrenList xValueChildrenList = new XValueChildrenList(1);

        jsonObject.keys().forEachRemaining(key -> {
            // Create a new XValue.
            SiddhiXValue siddhiXValue = new SiddhiXValue(myProcess, myFrame.getFrameName(), key, jsonObject.get(key),
                    AllIcons.Debugger.Value);
            // Add the XValue to the list.
            xValueChildrenList.add(key, siddhiXValue);
        });
        // Add the list to the node as children.
        node.addChildren(xValueChildrenList, true);
    }
}

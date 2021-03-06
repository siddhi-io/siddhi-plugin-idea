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

package org.wso2.siddhi.plugins.idea.inspections;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.wso2.siddhi.plugins.idea.SiddhiFileType;
import org.wso2.siddhi.plugins.idea.sdk.SiddhiSdkService;

import java.util.Set;

/**
 * Defines the notifications for the wrong module type.
 */
public class WrongModuleTypeNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel>
        implements DumbAware {

    private static final Key<EditorNotificationPanel> KEY = Key.create("Wrong module type");
    private static final String DONT_ASK_TO_CHANGE_MODULE_TYPE_KEY = "do.not.ask.to.change.module.type";

    private final Project myProject;

    public WrongModuleTypeNotificationProvider(@NotNull Project project) {
        myProject = project;
    }

    @NotNull
    @Override
    public Key<EditorNotificationPanel> getKey() {
        return KEY;
    }

    @Override
    public EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull FileEditor fileEditor) {
        if (file.getFileType() != SiddhiFileType.INSTANCE) {
            return null;
        }
        Module module = ModuleUtilCore.findModuleForFile(file, myProject);
        SiddhiSdkService.getInstance(myProject);
        return module == null || SiddhiSdkService.isSiddhiModule(module)
                || getIgnoredModules(myProject).contains(module.getName()) ? null : createPanel(myProject, module);
    }

    @NotNull
    private static EditorNotificationPanel createPanel(@NotNull Project project, @NotNull Module module) {
        EditorNotificationPanel panel = new EditorNotificationPanel();
        panel.setText("'" + module.getName() + "' is not a Siddhi Module, some code insight might not work here");
        return panel;
    }

    @NotNull
    private static Set<String> getIgnoredModules(@NotNull Project project) {
        String value = PropertiesComponent.getInstance(project).getValue(DONT_ASK_TO_CHANGE_MODULE_TYPE_KEY, "");
        return ContainerUtil.newLinkedHashSet(StringUtil.split(value, ","));
    }
}

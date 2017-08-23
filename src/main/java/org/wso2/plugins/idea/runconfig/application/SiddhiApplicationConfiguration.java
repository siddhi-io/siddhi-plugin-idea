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

package org.wso2.plugins.idea.runconfig.application;

import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import org.wso2.plugins.idea.runconfig.SiddhiModuleBasedConfiguration;
import org.wso2.plugins.idea.runconfig.SiddhiRunConfigurationWithMain;
import org.wso2.plugins.idea.runconfig.RunConfigurationKind;
import org.wso2.plugins.idea.runconfig.ui.SiddhiApplicationSettingsEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class SiddhiApplicationConfiguration
        extends SiddhiRunConfigurationWithMain<SiddhiApplicationRunningState> {

    private static final String KIND_ATTRIBUTE_NAME = "kind";

    @NotNull
    private String myExtension = "";

    public SiddhiApplicationConfiguration(Project project, String name,
                                          @NotNull ConfigurationType configurationType) {
        super(name, new SiddhiModuleBasedConfiguration(project), configurationType.getConfigurationFactories()[0]);
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        try {
            String kindName = JDOMExternalizerUtil.getFirstChildValueAttribute(element, KIND_ATTRIBUTE_NAME);
            myRunKind = kindName != null ? RunConfigurationKind.valueOf(kindName) : RunConfigurationKind.MAIN;
        } catch (IllegalArgumentException e) {
            myRunKind = RunConfigurationKind.MAIN;
        }
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizerUtil.addElementWithValueAttribute(element, KIND_ATTRIBUTE_NAME, myRunKind.name());
    }

    @NotNull
    @Override
    protected ModuleBasedConfiguration createInstance() {
        return new SiddhiApplicationConfiguration(getProject(), getName(),
                SiddhiApplicationRunConfigurationType.getInstance());
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new SiddhiApplicationSettingsEditor(getProject());
    }

    @NotNull
    @Override
    protected SiddhiApplicationRunningState newRunningState(@NotNull ExecutionEnvironment env,
                                                            @NotNull Module module) {
        return new SiddhiApplicationRunningState(env, module, this);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkBaseConfiguration();
        super.checkFileConfiguration();
        Module module = getConfigurationModule().getModule();
        assert module != null;

        if (StringUtil.isEmptyOrSpaces(getFilePath())) {
            throw new RuntimeConfigurationError("File path is not specified.");
        }
    }

    @NotNull
    public String getExtension() {
        return myExtension;
    }

    public void setExtension(@NotNull String aExtension) {
        myExtension= aExtension;
    }
}

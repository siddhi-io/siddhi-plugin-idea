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

package org.wso2.siddhi.plugins.idea.runconfig.application;

import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.wso2.siddhi.plugins.idea.runconfig.SiddhiModuleBasedConfiguration;
import org.wso2.siddhi.plugins.idea.runconfig.SiddhiRunConfiguration;
import org.wso2.siddhi.plugins.idea.runconfig.ui.SiddhiApplicationSettingsEditor;

/**
 * Defines siddhi application configuration.
 */
public class SiddhiApplicationConfiguration
        extends SiddhiRunConfiguration<SiddhiApplicationRunningState> {

    public SiddhiApplicationConfiguration(Project project, String name,
                                          @NotNull ConfigurationType configurationType) {
        super(name, new SiddhiModuleBasedConfiguration(project), configurationType.getConfigurationFactories()[0]);
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
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
}

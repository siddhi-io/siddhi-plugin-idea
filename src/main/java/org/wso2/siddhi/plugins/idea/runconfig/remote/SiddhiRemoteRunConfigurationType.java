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

package org.wso2.siddhi.plugins.idea.runconfig.remote;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.wso2.siddhi.plugins.idea.SiddhiConstants;
import org.wso2.siddhi.plugins.idea.SiddhiIcons;
import org.wso2.siddhi.plugins.idea.runconfig.SiddhiConfigurationFactoryBase;

/**
 * Defines siddhi remote run configuration type.
 */
public class SiddhiRemoteRunConfigurationType extends ConfigurationTypeBase {

    public SiddhiRemoteRunConfigurationType() {
        super("SiddhiRemoteConfiguration", "Siddhi Remote",
                "Siddhi Remote Configuration", SiddhiIcons.APPLICATION_RUN);

        addFactory(new SiddhiConfigurationFactoryBase(this) {

            @Override
            @NotNull
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new SiddhiRemoteConfiguration(project, SiddhiConstants.SIDDHI, getInstance());
            }
        });
    }

    @NotNull
    public static SiddhiRemoteRunConfigurationType getInstance() {
        return Extensions.findExtension(CONFIGURATION_TYPE_EP, SiddhiRemoteRunConfigurationType.class);
    }
}

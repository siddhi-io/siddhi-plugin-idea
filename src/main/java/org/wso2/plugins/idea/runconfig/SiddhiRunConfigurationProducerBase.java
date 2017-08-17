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

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.wso2.plugins.idea.psi.*;
import org.wso2.plugins.idea.runconfig.application.SiddhiApplicationConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SiddhiRunConfigurationProducerBase<T extends SiddhiRunConfigurationWithMain>
        extends RunConfigurationProducer<T> implements Cloneable {

    protected SiddhiRunConfigurationProducerBase(@NotNull ConfigurationType configurationType) {
        super(configurationType);
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull T configuration, @NotNull ConfigurationContext context,
                                                    Ref<PsiElement> sourceElement) {
        PsiFile file = getFileFromContext(context);
        if (file == null) {
            return false;
        }
        // Get existing configuration if available.
        RunnerAndConfigurationSettings existingConfigurations = context.findExisting();
        if (existingConfigurations != null) {
            // Get the RunConfiguration.
            RunConfiguration existingConfiguration = existingConfigurations.getConfiguration();
            // Run configuration might be an application configuration. So we need to check the type.
            if (existingConfiguration instanceof SiddhiApplicationConfiguration) {
                // Set other configurations.
                setConfigurations((SiddhiApplicationConfiguration) existingConfiguration);
                return true;
            }
        } else if (configuration instanceof SiddhiApplicationConfiguration) {
            // If an existing configuration is not found and the configuration provided is of correct type.
            String configName = getConfigurationName(file);
            // Set the config name. This will be the file name.
            configuration.setName(configName);
            // Set the file path.
            configuration.setFilePath(file.getVirtualFile().getPath());
            // Set the module.
            Module module = context.getModule();
            if (module != null) {
                configuration.setModule(module);
            }
            // Set other configurations.
            setConfigurations((SiddhiApplicationConfiguration) configuration);
            return true;
        }
        // Return false if the provided configuration type cannot be applied.
        return false;
    }

    private void setConfigurations(@NotNull SiddhiApplicationConfiguration configuration) {
            // Set the kind to MAIN.
            configuration.setRunKind(RunConfigurationKind.MAIN);
    }

    @NotNull
    protected abstract String getConfigurationName(@NotNull PsiFile file);

    @Override
    public boolean isConfigurationFromContext(@NotNull T configuration, ConfigurationContext context) {
        SiddhiFile file = getFileFromContext(context);
        return file != null && FileUtil.pathsEqual(configuration.getFilePath(), file.getVirtualFile().getPath());
    }

    @Nullable
    private static SiddhiFile getFileFromContext(@Nullable ConfigurationContext context) {
        PsiElement contextElement = SiddhiRunUtil.getContextElement(context);
        PsiFile psiFile = contextElement != null ? contextElement.getContainingFile() : null;
        return psiFile instanceof SiddhiFile ? (SiddhiFile) psiFile : null;
    }
}

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

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.wso2.siddhi.plugins.idea.runconfig.SiddhiRunConfigurationProducerBase;

/**
 * Produce siddhi application run configuration.
 */
public class SiddhiApplicationRunConfigurationProducer
        extends SiddhiRunConfigurationProducerBase<SiddhiApplicationConfiguration> implements Cloneable {

    public SiddhiApplicationRunConfigurationProducer() {
        super(SiddhiApplicationRunConfigurationType.getInstance());
    }

    @NotNull
    @Override
    protected String getConfigurationName(@NotNull PsiFile file) {
        try {
            return file.getName();
        } catch (NullPointerException e) {
            return "";
        }
    }
}

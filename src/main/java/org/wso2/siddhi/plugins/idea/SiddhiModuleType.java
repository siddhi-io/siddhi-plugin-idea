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

package org.wso2.siddhi.plugins.idea;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectJdkForModuleStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.Nullable;
import org.wso2.siddhi.plugins.idea.sdk.SiddhiSdkType;

import javax.annotation.Nonnull;
import javax.swing.Icon;

/**
 * Defines siddhi module types.
 */
public class SiddhiModuleType extends ModuleType<SiddhiModuleBuilder> {

    public SiddhiModuleType() {
        super(SiddhiConstants.MODULE_TYPE_ID);
    }

    @Nonnull
    public static SiddhiModuleType getInstance() {
        return (SiddhiModuleType) ModuleTypeManager.getInstance().findByID(SiddhiConstants.MODULE_TYPE_ID);
    }

    @Nonnull
    @Override
    public SiddhiModuleBuilder createModuleBuilder() {
        return new SiddhiModuleBuilder();
    }

    @Nonnull
    @Override
    public String getName() {
        return "Siddhi Module";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Siddhi modules are used for developing <b>Siddhi</b> applications.";
    }

    @Nullable
    public Icon getBigIcon() {
        return SiddhiIcons.ICON;
    }

    @Nullable
    @Override
    public Icon getNodeIcon(boolean isOpened) {
        return SiddhiIcons.ICON;
    }

    @Nonnull
    @Override
    public ModuleWizardStep[] createWizardSteps(@Nonnull WizardContext wizardContext,
                                                @Nonnull SiddhiModuleBuilder moduleBuilder,
                                                @Nonnull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{new ProjectJdkForModuleStep(wizardContext, SiddhiSdkType.getInstance()) {
            @Override
            public void updateDataModel() {
                super.updateDataModel();
                moduleBuilder.setModuleJdk(getJdk());
            }
        }};
    }
}

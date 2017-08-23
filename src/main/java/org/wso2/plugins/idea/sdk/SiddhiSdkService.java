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

package org.wso2.plugins.idea.sdk;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.wso2.plugins.idea.SiddhiConstants;
import org.wso2.plugins.idea.SiddhiModuleType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Set;

public class SiddhiSdkService extends SimpleModificationTracker {

    public static final Logger LOG = Logger.getInstance(SiddhiSdkService.class);
    private static final Set<String> FEDORA_SUBDIRECTORIES = ContainerUtil.newHashSet("linux_amd64", "linux_386",
            "linux_arm");
    private Project myProject;

    protected SiddhiSdkService(@NotNull Project project) {
        myProject = project;
    }

    public static SiddhiSdkService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, SiddhiSdkService.class);
    }

    @Contract("null -> null")
    public static String getSiddhiExecutablePath(@Nullable String sdkHomePath) {
        if (sdkHomePath != null) {
            File binDirectory = new File(sdkHomePath, "bin");
            if (!binDirectory.exists() && SystemInfo.isLinux) {
                LOG.debug(sdkHomePath + "/bin doesn't exist, checking linux-specific paths");
                File siddhiFromPath = PathEnvironmentVariableUtil.findInPath(
                        SiddhiConstants.SIDDHI_EXECUTABLE_NAME);
                if (siddhiFromPath != null && siddhiFromPath.exists()) {
                    LOG.debug("Siddhi executable found at " + siddhiFromPath.getAbsolutePath());
                    return siddhiFromPath.getAbsolutePath();
                }
            }

            String executableName = SiddhiEnvironmentUtil.getBinaryFileNameForPath(
                    SiddhiConstants.SIDDHI_EXECUTABLE_NAME);
            String executable = FileUtil.join(sdkHomePath, "bin", executableName);
            if (!new File(executable).exists() && SystemInfo.isLinux) {
                LOG.debug(executable + " doesn't exists. Looking for binaries in fedora-specific directories");
                // fedora
                for (String directory : FEDORA_SUBDIRECTORIES) {
                    File file = new File(binDirectory, directory);
                    if (file.exists() && file.isDirectory()) {
                        LOG.debug("Siddhi executable found at " + file.getAbsolutePath());
                        return FileUtil.join(file.getAbsolutePath(), executableName);
                    }
                }
            }
            LOG.debug("Siddhi executable found at " + executable);
            return executable;
        }
        return null;
    }

    @Contract("null -> false")
    public static boolean isSiddhiModule(@Nullable Module module) {
        return module != null && ModuleUtil.getModuleType(module) == SiddhiModuleType.getInstance();
    }

    public String getSdkHomePath(@Nullable Module module) {
        Sdk sdk = getSiddhiSdk(module);
        return sdk != null ? sdk.getHomePath() : null;
    }

    private Sdk getSiddhiSdk(@Nullable Module module) {
        if (module != null) {
            Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
            if (sdk != null && sdk.getSdkType() instanceof SiddhiSdkType) {
                return sdk;
            }
        }
        Sdk sdk = ProjectRootManager.getInstance(myProject).getProjectSdk();
        return sdk != null && sdk.getSdkType() instanceof SiddhiSdkType ? sdk : null;
    }


    @Nullable
    public String getSdkVersion(@Nullable Module module) {
        ComponentManager holder = ObjectUtils.notNull(module, myProject);
        return CachedValuesManager.getManager(myProject).getCachedValue(holder, () -> {
            Sdk sdk = getSiddhiSdk(module);
            return CachedValueProvider.Result.create(sdk != null ? sdk.getVersionString() : null, this);
        });
    }
}

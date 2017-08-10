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
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.Function;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.wso2.plugins.idea.SiddhiConstants;
import org.wso2.plugins.idea.project.SiddhiApplicationLibrariesService;
import org.wso2.plugins.idea.project.SiddhiLibrariesService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.util.containers.ContainerUtil.newLinkedHashSet;

public class SiddhiSdkUtil {

    private static final Pattern SIDDHI_VERSION_PATTERN = Pattern.compile("(\\d+\\.\\d+(\\.\\d+)?(-.+)?)");
    private static final Key<String> VERSION_DATA_KEY = Key.create("SIDDHI_VERSION_KEY");
    private static final String SIDDHI_EXEC_PATH = "bin" + File.separator + "siddhi";

    @Nullable
    public static VirtualFile suggestSdkDirectory() {
        if (SystemInfo.isWindows) {
            return ObjectUtils.chooseNotNull(LocalFileSystem.getInstance().findFileByPath("C:\\siddhi"), null);
        }
        if (SystemInfo.isMac || SystemInfo.isLinux) {
            String fromEnv = suggestSdkDirectoryPathFromEnv();
            if (fromEnv != null) {
                return LocalFileSystem.getInstance().findFileByPath(fromEnv);
            }
            VirtualFile usrLocal = LocalFileSystem.getInstance().findFileByPath("/usr/local/siddhi");
            if (usrLocal != null) return usrLocal;
        }
        if (SystemInfo.isMac) {
            String macPorts = "/opt/local/lib/siddhi";
            String homeBrew = "/usr/local/Cellar/siddhi";
            File file = FileUtil.findFirstThatExist(macPorts, homeBrew);
            if (file != null) {
                return LocalFileSystem.getInstance().findFileByIoFile(file);
            }
        }
        return null;
    }

    @Nullable
    private static String suggestSdkDirectoryPathFromEnv() {
        File fileFromPath = PathEnvironmentVariableUtil.findInPath("siddhi");
        if (fileFromPath != null) {
            File canonicalFile;
            try {
                canonicalFile = fileFromPath.getCanonicalFile();
                String path = canonicalFile.getPath();
                if (path.endsWith(SIDDHI_EXEC_PATH)) {
                    return StringUtil.trimEnd(path, SIDDHI_EXEC_PATH);
                }
            } catch (IOException ignore) {
            }
        }
        return null;
    }

    @Nullable
    public static String retrieveSiddhiVersion(@NotNull String sdkPath) {
        try {
            VirtualFile sdkRoot = VirtualFileManager.getInstance().findFileByUrl(VfsUtilCore.pathToUrl(sdkPath));
            if (sdkRoot != null) {
                String cachedVersion = sdkRoot.getUserData(VERSION_DATA_KEY);
                if (cachedVersion != null) {
                    return !cachedVersion.isEmpty() ? cachedVersion : null;
                }

                VirtualFile versionFile = sdkRoot.findFileByRelativePath(
                        SiddhiConstants.SIDDHI_VERSION_FILE_PATH);
                // Please note that if the above versionFile is null, we can check on other locations as well.
                if (versionFile != null) {
                    String text = VfsUtilCore.loadText(versionFile);
                    String version = parseSiddhiVersion(text);
                    if (version == null) {
                        SiddhiSdkService.LOG.debug("Cannot retrieve Siddhi version from version file: " + text);
                    }
                    sdkRoot.putUserData(VERSION_DATA_KEY, StringUtil.notNullize(version));
                    return version;
                } else {
                    SiddhiSdkService.LOG.debug("Cannot find Siddhi version file in sdk path: " + sdkPath);
                }
            }
        } catch (IOException e) {
            SiddhiSdkService.LOG.debug("Cannot retrieve Siddhi version from sdk path: " + sdkPath, e);
        }
        return null;
    }

    @Nullable
    public static String parseSiddhiVersion(@NotNull String text) {
        Matcher matcher = SIDDHI_VERSION_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @NotNull
    public static Collection<VirtualFile> getSdkDirectoriesToAttach(@NotNull String sdkPath, @NotNull String
            versionString) {
        return ContainerUtil.createMaybeSingletonList(getSdkSrcDir(sdkPath, versionString));
    }

    @Nullable
    private static VirtualFile getSdkSrcDir(@NotNull String sdkPath, @NotNull String sdkVersion) {
        String srcPath = getSrcLocation(sdkVersion);
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(
                VfsUtilCore.pathToUrl(FileUtil.join(sdkPath, srcPath)));
        return file != null && file.isDirectory() ? file : null;
    }

    public static LinkedHashSet<VirtualFile> getSourcesPathsToLookup(@NotNull Project project, @Nullable Module module) {
        LinkedHashSet<VirtualFile> sdkAndGoPath = newLinkedHashSet();
        ContainerUtil.addIfNotNull(sdkAndGoPath, getSdkSrcDir(project, module));
        // Todo  - add Siddhi Path
//        ContainerUtil.addAllNotNull(sdkAndGoPath, getSiddhiPathSources(project, module));
        return sdkAndGoPath;
    }

    @NotNull
    private static String getSrcLocation(@NotNull String version) {
        return "src";
    }

    public static String getSdkHome(Project project, Module module) {
        // Get the module SDK.
        Sdk moduleSdk = ModuleRootManager.getInstance(module).getSdk();
        // If the SDK is Siddhi SDK, return the home path.
        if (moduleSdk != null && moduleSdk.getSdkType() == SiddhiSdkType.getInstance()) {
            return moduleSdk.getHomePath();
        }
        // Ge the project SDK.
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        // If the SDK is Siddhi SDK, return the home path.
        if (projectSdk != null && projectSdk.getSdkType() == SiddhiSdkType.getInstance()) {
            return projectSdk.getHomePath();
        }
        return "";
    }

    public static String getSiddhiExecutablePath(Project project, Module module) {
        String sdkHome = getSdkHome(project, module);
        if (!sdkHome.isEmpty()) {
            String execPath = sdkHome + File.separator + SIDDHI_EXEC_PATH;
            return SystemInfo.isWindows ? execPath + ".bat" : execPath;
        }
        return "";
    }

    @NotNull
    public static Collection<VirtualFile> getSiddhiPathsRootsFromEnvironment() {
        return SiddhiPathModificationTracker.getSiddhiEnvironmentPathRoots();
    }

    @NotNull
    private static List<VirtualFile> getInnerSiddhiPathSources(@NotNull Project project, @Nullable Module module) {
        return ContainerUtil.mapNotNull(getSiddhiPathRoots(project, module), new
                RetrieveSubDirectoryOrSelfFunction("src"));
    }

    @NotNull
    public static Collection<VirtualFile> getSiddhiPathRoots(@NotNull Project project, @Nullable Module module) {
        Collection<VirtualFile> roots = ContainerUtil.newArrayList();
        if (SiddhiApplicationLibrariesService.getInstance().isUseSiddhiPathFromSystemEnvironment()) {
            roots.addAll(getSiddhiPathsRootsFromEnvironment());
        }
        roots.addAll(module != null ? SiddhiLibrariesService.getUserDefinedLibraries(module) :
                SiddhiLibrariesService.getUserDefinedLibraries(project));
        return roots;
    }

    private static class RetrieveSubDirectoryOrSelfFunction implements Function<VirtualFile, VirtualFile> {
        @NotNull
        private final String mySubdirName;

        public RetrieveSubDirectoryOrSelfFunction(@NotNull String subdirName) {
            mySubdirName = subdirName;
        }

        @Override
        public VirtualFile fun(VirtualFile file) {
            return file == null || FileUtil.namesEqual(mySubdirName, file.getName()) ? file : file.findChild
                    (mySubdirName);
        }
    }

    @NotNull
    public static Collection<Module> getSiddhiModules(@NotNull Project project) {
        if (project.isDefault()) {
            return Collections.emptyList();
        }
        return ContainerUtil.filter(ModuleManager.getInstance(project).getModules(),
                SiddhiSdkService::isSiddhiModule);
    }

    @Nullable
    public static VirtualFile getSdkSrcDir(@NotNull Project project, @Nullable Module module) {
        if (module != null) {
            return CachedValuesManager.getManager(project).getCachedValue(module, () -> {
                SiddhiSdkService sdkService = SiddhiSdkService.getInstance(module.getProject());
                return CachedValueProvider.Result.create(getInnerSdkSrcDir(sdkService, module), sdkService);
            });
        }
        return CachedValuesManager.getManager(project).getCachedValue(project, () -> {
            SiddhiSdkService sdkService = SiddhiSdkService.getInstance(project);
            return CachedValueProvider.Result.create(getInnerSdkSrcDir(sdkService, null), sdkService);
        });
    }

    @Nullable
    private static VirtualFile getInnerSdkSrcDir(@NotNull SiddhiSdkService sdkService, @Nullable Module module) {
        String sdkHomePath = sdkService.getSdkHomePath(module);
        String sdkVersionString = sdkService.getSdkVersion(module);
        return sdkHomePath != null && sdkVersionString != null ? getSdkSrcDir(sdkHomePath, sdkVersionString) : null;
    }
}

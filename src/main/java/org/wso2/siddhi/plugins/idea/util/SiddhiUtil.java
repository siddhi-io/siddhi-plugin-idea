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

package org.wso2.siddhi.plugins.idea.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;

public class SiddhiUtil {

    private SiddhiUtil() {

    }

    /**
     * Returns the first library root found in the project.
     *
     * @param project project to find the library root
     * @return first library root
     */
    public static String getLibraryRoot(Project project) {
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (projectSdk != null) {
            VirtualFile[] roots = projectSdk.getSdkModificator().getRoots(OrderRootType.SOURCES);
            for (VirtualFile root : roots) {
                return root.getPath();
            }
        }
        return "";
    }

    /**
     * Returns whether the given file is a library file or not.
     *
     * @param project     project to get the library roots
     * @param virtualFile file to find in the library
     * @return {@code true} if the given file is in the libraries. {@code false} otherwise.
     */
    public static boolean isLibraryFile(Project project, VirtualFile virtualFile) {
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        String path = virtualFile.getPath();
        if (projectSdk != null) {
            VirtualFile[] roots = projectSdk.getSdkModificator().getRoots(OrderRootType.SOURCES);
            for (VirtualFile root : roots) {
                if (path.startsWith(root.getPath())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether the given file is a workspace file or not.
     *
     * @param project     project to find the file
     * @param virtualFile file to find in the project
     * @return {@code true} if the given file is in the project. {@code false} otherwise.
     */
    public static boolean isWorkspaceFile(Project project, VirtualFile virtualFile) {
        String filePath = virtualFile.getPath();
        if (filePath.startsWith(project.getBasePath())) {
            return true;
        }
        return false;
    }
}

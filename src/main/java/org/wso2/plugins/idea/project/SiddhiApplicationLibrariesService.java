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

package org.wso2.plugins.idea.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import org.wso2.plugins.idea.SiddhiConstants;
import org.wso2.plugins.idea.sdk.SiddhiSdkUtil;
import org.jetbrains.annotations.NotNull;

@State(
        name = SiddhiConstants.SIDDHI_LIBRARIES_SERVICE_NAME,
        storages = @Storage(file = StoragePathMacros.APP_CONFIG + "/" +
                SiddhiConstants.SIDDHI_LIBRARIES_CONFIG_FILE)
)
public class SiddhiApplicationLibrariesService extends
        SiddhiLibrariesService<SiddhiApplicationLibrariesService.SiddhiApplicationLibrariesState> {

    @NotNull
    @Override
    protected SiddhiApplicationLibrariesState createState() {
        return new SiddhiApplicationLibrariesState();
    }

    public static SiddhiApplicationLibrariesService getInstance() {
        return ServiceManager.getService(SiddhiApplicationLibrariesService.class);
    }

    public boolean isUseSiddhiPathFromSystemEnvironment() {
        return myState.isUseSiddhiPathFromSystemEnvironment();
    }

    public void setUseSiddhiPathFromSystemEnvironment(boolean useSiddhiPathFromSystemEnvironment) {
        if (myState.isUseSiddhiPathFromSystemEnvironment() != useSiddhiPathFromSystemEnvironment) {
            myState.setUseSiddhiPathFromSystemEnvironment(useSiddhiPathFromSystemEnvironment);
            if (!SiddhiSdkUtil.getSiddhiPathsRootsFromEnvironment().isEmpty()) {
                incModificationCount();
                ApplicationManager.getApplication().getMessageBus().syncPublisher(LIBRARIES_TOPIC)
                        .librariesChanged(getLibraryRootUrls());
            }
        }
    }

    public static class SiddhiApplicationLibrariesState extends SiddhiLibraryState {

        private boolean myUseSiddhiPathFromSystemEnvironment = true;

        public boolean isUseSiddhiPathFromSystemEnvironment() {
            return myUseSiddhiPathFromSystemEnvironment;
        }

        public void setUseSiddhiPathFromSystemEnvironment(boolean useSiddhiPathFromSystemEnvironment) {
            myUseSiddhiPathFromSystemEnvironment = useSiddhiPathFromSystemEnvironment;
        }
    }
}

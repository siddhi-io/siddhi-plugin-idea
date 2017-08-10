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

package org.wso2.plugins.idea;

import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.wm.ToolWindowId;
import org.jetbrains.annotations.NonNls;

public class SiddhiConstants {

    private SiddhiConstants() {

    }

    public static final String SIDDHI = "Siddhi";
    public static final String MAIN = "main";
    public static final String PATH = "PATH";
    public static final String MODULE_TYPE_ID = "SIDDHI_MODULE";

    @NonNls
    public static final String SIDDHI_EXECUTABLE_NAME = "siddhi";

    @NonNls
    public static final String SIDDHI_VERSION_FILE_PATH = "bin/version.txt";

    public static final NotificationGroup SIDDHI_NOTIFICATION_GROUP =
            NotificationGroup.balloonGroup("Siddhi plugin notifications");

    public static final NotificationGroup SIDDHI_EXECUTION_NOTIFICATION_GROUP =
            NotificationGroup.toolWindowGroup("Siddhi Execution", ToolWindowId.RUN);

    public static final String SIDDHI_REPOSITORY = "SIDDHI_REPOSITORY";
    public static final String SIDDHI_LIBRARIES_SERVICE_NAME = "SiddhiLibraries";
    public static final String SIDDHI_LIBRARIES_CONFIG_FILE = "SiddhiLibraries.xml";
    public static final String SIDDHI_MODULE_SESTTINGS_SERVICE_NAME = "Siddhi";
}

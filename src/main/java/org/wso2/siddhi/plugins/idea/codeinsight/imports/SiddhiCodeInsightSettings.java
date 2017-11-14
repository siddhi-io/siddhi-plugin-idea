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

package org.wso2.siddhi.plugins.idea.codeinsight.imports;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Defines code insight setting for a siddhi language.
 */
@State(
        name = "Siddhi",
        storages = @Storage(file = StoragePathMacros.APP_CONFIG + "/editor.codeinsight.xml")
)

public class SiddhiCodeInsightSettings implements PersistentStateComponent<SiddhiCodeInsightSettings> {

    public static SiddhiCodeInsightSettings getInstance() {
        return ServiceManager.getService(SiddhiCodeInsightSettings.class);
    }

    @Nullable
    @Override
    public SiddhiCodeInsightSettings getState() {
        return this;
    }

    @Override
    public void loadState(SiddhiCodeInsightSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}

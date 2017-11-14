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

package org.wso2.siddhi.plugins.idea.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ApplicationComponent;
import org.wso2.siddhi.plugins.idea.SiddhiConstants;

import javax.annotation.Nonnull;

/**
 * Provides notifications.
 */
public class SiddhiNotification implements ApplicationComponent {

    private static final String SIDDHI_PROJECT_TUTORIAL_NOTIFICATION_SHOWN =
            "learn.siddhi.notification.shown";

    @Override
    public void initComponent() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        boolean wasDisplayed;
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (propertiesComponent) {
            wasDisplayed = propertiesComponent.getBoolean(SIDDHI_PROJECT_TUTORIAL_NOTIFICATION_SHOWN, false);
            propertiesComponent.setValue(SIDDHI_PROJECT_TUTORIAL_NOTIFICATION_SHOWN, true);
        }

        if (wasDisplayed) {
            return;
        }

        Notifications.Bus.notify(SiddhiConstants.SIDDHI_NOTIFICATION_GROUP.createNotification(
                "Learn Siddhi",
                "Visit <a href=\"https://docs.wso2.com/display/CEP420\">Siddhi website<a/> to learn more about " +
                        "Siddhi.",
                NotificationType.INFORMATION,
                NotificationListener.URL_OPENING_LISTENER));
    }

    @Override
    public void disposeComponent() {

    }

    @Nonnull
    @Override
    public String getComponentName() {
        return getClass().getName();
    }
}

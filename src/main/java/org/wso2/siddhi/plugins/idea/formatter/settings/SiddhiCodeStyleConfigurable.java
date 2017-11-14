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

package org.wso2.siddhi.plugins.idea.formatter.settings;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.wso2.siddhi.plugins.idea.SiddhiLanguage;

import javax.annotation.Nonnull;

/**
 * Defines configurations for code styles in siddhi.
 */
public class SiddhiCodeStyleConfigurable extends CodeStyleAbstractConfigurable {

    public SiddhiCodeStyleConfigurable(@Nonnull CodeStyleSettings settings, CodeStyleSettings cloneSettings) {
        super(settings, cloneSettings, "Siddhi");
    }

    @Nonnull
    @Override
    protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
        return new SiddhiCodeStyleMainPanel(getCurrentSettings(), settings);
    }

    @Override
    public String getHelpTopic() {
        return null;
    }

    private static class SiddhiCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {

        private SiddhiCodeStyleMainPanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
            super(SiddhiLanguage.INSTANCE, currentSettings, settings);
        }

        @Override
        protected void addSpacesTab(CodeStyleSettings settings) {
        }

        @Override
        protected void addBlankLinesTab(CodeStyleSettings settings) {
        }

        @Override
        protected void addWrappingAndBracesTab(CodeStyleSettings settings) {
        }
    }
}

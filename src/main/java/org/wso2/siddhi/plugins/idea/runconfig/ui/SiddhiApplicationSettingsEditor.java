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

package org.wso2.siddhi.plugins.idea.runconfig.ui;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import org.jetbrains.annotations.NotNull;
import org.wso2.siddhi.plugins.idea.runconfig.SiddhiRunUtil;
import org.wso2.siddhi.plugins.idea.runconfig.application.SiddhiApplicationConfiguration;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Ui for siddhi application settings editor.
 */
public class SiddhiApplicationSettingsEditor extends SettingsEditor<SiddhiApplicationConfiguration> {

    private JPanel myPanel;
    private LabeledComponent<TextFieldWithBrowseButton> myFileField;
    private LabeledComponent<RawCommandLineEditor> myParamsField;
    private LabeledComponent<TextFieldWithBrowseButton> myWorkingDirectoryField;
    private LabeledComponent<ModulesComboBox> myModulesComboBox;
    private LabeledComponent<TextFieldWithBrowseButton> myEventInputFile;
    private Project myProject;

    public SiddhiApplicationSettingsEditor(Project project) {
        this.myProject = project;
        SiddhiRunUtil.installSiddhiWithSiddhiFileChooser(project, myFileField.getComponent());
        SiddhiRunUtil.installSiddhiWithWorkingDirectoryChooser(project, myWorkingDirectoryField.getComponent());
        SiddhiRunUtil.installSiddhiWithFileChooser(project, myEventInputFile.getComponent());
    }

    @Override
    protected void resetEditorFrom(@NotNull SiddhiApplicationConfiguration configuration) {
        myFileField.getComponent().setText(configuration.getFilePath());

        myEventInputFile.getComponent().setText(configuration.getInputFilePath());

        myModulesComboBox.getComponent().setModules(configuration.getValidModules());
        myModulesComboBox.getComponent().setSelectedModule(configuration.getConfigurationModule().getModule());

        myParamsField.getComponent().setText(configuration.getParams());
        myWorkingDirectoryField.getComponent().setText(configuration.getWorkingDirectory());
    }

    @Override
    protected void applyEditorTo(@NotNull SiddhiApplicationConfiguration configuration)
            throws ConfigurationException {
        configuration.setFilePath(myFileField.getComponent().getText());
        configuration.setInputFilePath(myEventInputFile.getComponent().getText());
        configuration.setModule(myModulesComboBox.getComponent().getSelectedModule());
        configuration.setParams(myParamsField.getComponent().getText());
        configuration.setWorkingDirectory(myWorkingDirectoryField.getComponent().getText());

        myParamsField.setVisible(true);
        myEventInputFile.setVisible(true);
        myWorkingDirectoryField.setVisible(true);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }

    private void createUIComponents() {

        myFileField = new LabeledComponent<>();
        myFileField.setComponent(new TextFieldWithBrowseButton());

        myEventInputFile = new LabeledComponent<>();
        myEventInputFile.setComponent(new TextFieldWithBrowseButton());

        myWorkingDirectoryField = new LabeledComponent<>();
        myWorkingDirectoryField.setComponent(new TextFieldWithBrowseButton());

        myParamsField = new LabeledComponent<>();
        myParamsField.setComponent(new RawCommandLineEditor());

        myModulesComboBox = new LabeledComponent<>();
        myModulesComboBox.setComponent(new ModulesComboBox());
    }
}

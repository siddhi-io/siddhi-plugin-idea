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

package org.wso2.siddhi.plugins.idea.runconfig;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jdom.Element;
import org.wso2.siddhi.plugins.idea.psi.SiddhiFile;

import javax.annotation.Nonnull;

/**
 * Defines run configuration with main.
 * @param <T>
 */
public abstract class SiddhiRunConfigurationWithMain<T extends SiddhiRunningState> extends
        SiddhiRunConfigurationBase<T> {

    private static final String FILE_PATH_ATTRIBUTE_NAME = "filePath";
    private static final String INPUT_FILE_PATH_ATTRIBUTE_NAME = "inputFilePath";
    private static final String KIND_ATTRIBUTE_NAME = "myRunKind";
    private static final String REMOTE_DEBUGGING_HOST_ATTRIBUTE_NAME = "remoteDebuggingHost";
    private static final String REMOTE_DEBUGGING_PORT_ATTRIBUTE_NAME = "remoteDebuggingPort";

    @Nonnull
    private String myFilePath = "";
    private String myInputFilePath = "";
    @Nonnull
    protected RunConfigurationKind myRunKind = RunConfigurationKind.MAIN;
    @Nonnull
    private String remoteDebugHost = "";
    @Nonnull
    private String remoteDebugPort = "";

    public SiddhiRunConfigurationWithMain(String name, SiddhiModuleBasedConfiguration configurationModule,
                                          ConfigurationFactory factory) {
        super(name, configurationModule, factory);
        myFilePath = getFilePath();
    }

    @Override
    public void readExternal(@Nonnull Element element) throws InvalidDataException {
        super.readExternal(element);
        myFilePath = StringUtil.notNullize(JDOMExternalizerUtil.getFirstChildValueAttribute(element,
                FILE_PATH_ATTRIBUTE_NAME));
        myInputFilePath = StringUtil.notNullize(JDOMExternalizerUtil.getFirstChildValueAttribute(element,
                INPUT_FILE_PATH_ATTRIBUTE_NAME));
        myRunKind = RunConfigurationKind.valueOf(StringUtil.notNullize(
                JDOMExternalizerUtil.getFirstChildValueAttribute(element, KIND_ATTRIBUTE_NAME)));
        remoteDebugHost = StringUtil.notNullize(JDOMExternalizerUtil.getFirstChildValueAttribute(element,
                REMOTE_DEBUGGING_HOST_ATTRIBUTE_NAME));
        remoteDebugPort = StringUtil.notNullize(JDOMExternalizerUtil.getFirstChildValueAttribute(element,
                REMOTE_DEBUGGING_PORT_ATTRIBUTE_NAME));
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        addNonEmptyElement(element, FILE_PATH_ATTRIBUTE_NAME, myFilePath);
        addNonEmptyElement(element, INPUT_FILE_PATH_ATTRIBUTE_NAME, myInputFilePath);
        addNonEmptyElement(element, KIND_ATTRIBUTE_NAME, myRunKind.toString());
        addNonEmptyElement(element, REMOTE_DEBUGGING_HOST_ATTRIBUTE_NAME, remoteDebugHost);
        addNonEmptyElement(element, REMOTE_DEBUGGING_PORT_ATTRIBUTE_NAME, remoteDebugPort);
    }

    protected void checkFileConfiguration() throws RuntimeConfigurationError {
        VirtualFile file = findFile(getFilePath());
        if (file == null) {
            throw new RuntimeConfigurationError("Cannot find the specified file.");
        }
        PsiFile psiFile = PsiManager.getInstance(getProject()).findFile(file);
        if (!(psiFile instanceof SiddhiFile)) {
            throw new RuntimeConfigurationError("File is not a valid Siddhi file.");
        }
    }

    protected void checkBaseConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();
    }

    @Nonnull
    public String getFilePath() {
        return myFilePath;
    }

    public void setFilePath(@Nonnull String filePath) {
        myFilePath = filePath;
    }

    public String getInputFilePath() {
        return myInputFilePath;
    }

    public void setInputFilePath(@Nonnull String inputFilePath) {
        myInputFilePath = inputFilePath;
    }

    public RunConfigurationKind getRunKind() {
        return myRunKind;
    }

    public void setRunKind(RunConfigurationKind runKind) {
        this.myRunKind = runKind;
    }

    @Nonnull
    public String getRemoteDebugHost() {
        return remoteDebugHost;
    }

    public void setRemoteDebugHost(@Nonnull String remoteDebugHost) {
        this.remoteDebugHost = remoteDebugHost;
    }

    @Nonnull
    public String getRemoteDebugPort() {
        return remoteDebugPort;
    }

    public void setRemoteDebugPort(@Nonnull String remoteDebugPort) {
        this.remoteDebugPort = remoteDebugPort;
    }
}

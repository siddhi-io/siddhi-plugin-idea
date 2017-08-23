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

package org.wso2.plugins.idea.runconfig.application;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import org.wso2.plugins.idea.runconfig.SiddhiRunningState;
import org.wso2.plugins.idea.util.SiddhiExecutor;
import org.wso2.plugins.idea.util.SiddhiHistoryProcessListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SiddhiApplicationRunningState extends SiddhiRunningState<SiddhiApplicationConfiguration> {

    private int myDebugPort = 5006;
    @Nullable
    private SiddhiHistoryProcessListener myHistoryProcessHandler;

    SiddhiApplicationRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module,
                                  @NotNull SiddhiApplicationConfiguration configuration) {
        super(env, module, configuration);
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        ProcessHandler processHandler = super.startProcess();
        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void startNotified(ProcessEvent event) {
                if (myHistoryProcessHandler != null) {
                    myHistoryProcessHandler.apply(processHandler);
                }
            }
        });
        return processHandler;
    }

    @Override
    protected SiddhiExecutor patchExecutor(@NotNull SiddhiExecutor executor) throws ExecutionException {
        String parameters = myConfiguration.getFilePath();
        SiddhiExecutor siddhiExecutor = executor.withParameters("run")
                .withParameterString(myConfiguration.getSiddhiToolParams()).withParameters(parameters);

        // If debugging mode is running, we need to add the debugging flag.
        if (isDebug()) {
            siddhiExecutor.withParameters("--siddhi.debug", String.valueOf(myDebugPort));
        }
        return siddhiExecutor;
    }

    public void setDebugPort(int debugPort) {
        myDebugPort = debugPort;
    }

    public void setHistoryProcessHandler(@Nullable SiddhiHistoryProcessListener historyProcessHandler) {
        myHistoryProcessHandler = historyProcessHandler;
    }

    /**
     * Indicates whether the debugging is invoked or not.
     *
     * @return {@code true} if debugging is running, {@code false} otherwise.
     */
    private boolean isDebug() {
        return DefaultDebugExecutor.EXECUTOR_ID.equals(getEnvironment().getExecutor().getId());
    }
}

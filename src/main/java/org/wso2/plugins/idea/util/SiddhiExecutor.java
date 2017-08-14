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

package org.wso2.plugins.idea.util;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.ExecutionModes;
import com.intellij.execution.RunContentExecutor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.execution.process.*;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.util.Consumer;
import com.intellij.util.EnvironmentUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.wso2.plugins.idea.SiddhiConstants;
import org.wso2.plugins.idea.runconfig.SiddhiConsoleFilter;
import org.wso2.plugins.idea.runconfig.SiddhiRunUtil;
import org.wso2.plugins.idea.sdk.SiddhiSdkUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class SiddhiExecutor {

    private static final Logger LOGGER = Logger.getInstance(SiddhiExecutor.class);
    @NotNull
    private final Map<String, String> myExtraEnvironment = ContainerUtil.newHashMap();
    @NotNull
    private final ParametersList myParameterList = new ParametersList();
    @NotNull
    private final ProcessOutput myProcessOutput = new ProcessOutput();
    @NotNull
    private final Project myProject;
    @Nullable
    private final Module myModule;
    @Nullable
    private String mySiddhiPath;
    @Nullable
    private String myEnvPath;
    @Nullable
    private String myWorkDirectory;
    private boolean myShowOutputOnError;
    private boolean myShowNotificationsOnError;
    private boolean myShowNotificationsOnSuccess;
    private boolean myShowSiddhiEnvVariables = true;
    private GeneralCommandLine.ParentEnvironmentType myParentEnvironmentType =
            GeneralCommandLine.ParentEnvironmentType.CONSOLE;
    private boolean myPtyDisabled;
    @Nullable
    private String myExePath;
    @Nullable
    private String myPresentableName;
    private OSProcessHandler myProcessHandler;
    private final Collection<ProcessListener> myProcessListeners = ContainerUtil.newArrayList();

    private SiddhiExecutor(@NotNull Project project, @Nullable Module module) {
        myProject = project;
        myModule = module;
    }

    public static SiddhiExecutor in(@NotNull Project project, @Nullable Module module) {
        return module != null ? in(module) : in(project);
    }

    @NotNull
    private static SiddhiExecutor in(@NotNull Project project) {
        return new SiddhiExecutor(project, null);
    }

    @NotNull
    public static SiddhiExecutor in(@NotNull Module module) {
        Project project = module.getProject();
        return new SiddhiExecutor(project, module);
    }

    @NotNull
    public SiddhiExecutor withPresentableName(@Nullable String presentableName) {
        myPresentableName = presentableName;
        return this;
    }

    @NotNull
    public SiddhiExecutor withExePath(@Nullable String exePath) {
        myExePath = exePath;
        return this;
    }

    @NotNull
    public SiddhiExecutor withWorkDirectory(@Nullable String workDirectory) {
        myWorkDirectory = workDirectory;
        return this;
    }


    @NotNull
    public SiddhiExecutor withSiddhiPath(@Nullable String siddhiPath) {
        mySiddhiPath = siddhiPath;
        return this;
    }

    @NotNull
    public SiddhiExecutor withEnvPath(@Nullable String envPath) {
        myEnvPath = envPath;
        return this;
    }

    public SiddhiExecutor withProcessListener(@NotNull ProcessListener listener) {
        myProcessListeners.add(listener);
        return this;
    }

    @NotNull
    public SiddhiExecutor withExtraEnvironment(@NotNull Map<String, String> environment) {
        myExtraEnvironment.putAll(environment);
        return this;
    }

    @NotNull
    public SiddhiExecutor withPassParentEnvironment(boolean passParentEnvironment) {
        myParentEnvironmentType = passParentEnvironment ? GeneralCommandLine.ParentEnvironmentType.CONSOLE
                : GeneralCommandLine.ParentEnvironmentType.NONE;
        return this;
    }

    @NotNull
    public SiddhiExecutor withParameterString(@NotNull String parameterString) {
        myParameterList.addParametersString(parameterString);
        return this;
    }

    @NotNull
    public SiddhiExecutor withParameters(@NotNull String... parameters) {
        myParameterList.addAll(parameters);
        return this;
    }

    public SiddhiExecutor showSiddhiEnvVariables(boolean show) {
        myShowSiddhiEnvVariables = show;
        return this;
    }

    @NotNull
    public SiddhiExecutor showOutputOnError() {
        myShowOutputOnError = true;
        return this;
    }

    @NotNull
    public SiddhiExecutor disablePty() {
        myPtyDisabled = true;
        return this;
    }

    @NotNull
    public SiddhiExecutor showNotifications(boolean onError, boolean onSuccess) {
        myShowNotificationsOnError = onError;
        myShowNotificationsOnSuccess = onSuccess;
        return this;
    }

    public boolean execute() {
        Logger.getInstance(getClass()).assertTrue(!ApplicationManager.getApplication().isDispatchThread(),
                "It's bad idea to run external tool on EDT");
        Logger.getInstance(getClass()).assertTrue(myProcessHandler == null,
                "Process has already run with this executor instance");
        Ref<Boolean> result = Ref.create(false);
        GeneralCommandLine commandLine = null;
        try {
            commandLine = createCommandLine();
            GeneralCommandLine finalCommandLine = commandLine;
            myProcessHandler = new KillableColoredProcessHandler(finalCommandLine, true) {
                @Override
                public void startNotify() {
                    if (myShowSiddhiEnvVariables) {
                        SiddhiRunUtil.printSiddhiEnvVariables(finalCommandLine, this);
                    }
                    super.startNotify();
                }
            };
            SiddhiHistoryProcessListener historyProcessListener = new SiddhiHistoryProcessListener();
            myProcessHandler.addProcessListener(historyProcessListener);
            for (ProcessListener listener : myProcessListeners) {
                myProcessHandler.addProcessListener(listener);
            }

            CapturingProcessAdapter processAdapter = new CapturingProcessAdapter(myProcessOutput) {
                @Override
                public void processTerminated(@NotNull ProcessEvent event) {
                    super.processTerminated(event);
                    boolean success = event.getExitCode() == 0 && myProcessOutput.getStderr().isEmpty();
                    boolean nothingToShow = myProcessOutput.getStdout().isEmpty()
                            && myProcessOutput.getStderr().isEmpty();
                    boolean cancelledByUser = (event.getExitCode() == -1 || event.getExitCode() == 2) && nothingToShow;
                    result.set(success);
                    if (success) {
                        if (myShowNotificationsOnSuccess) {
                            showNotification("Finished successfully", NotificationType.INFORMATION);
                        }
                    } else if (cancelledByUser) {
                        if (myShowNotificationsOnError) {
                            showNotification("Interrupted", NotificationType.WARNING);
                        }
                    } else if (myShowOutputOnError) {
                        ApplicationManager.getApplication().invokeLater(() -> showOutput(myProcessHandler,
                                historyProcessListener));
                    }
                }
            };

            myProcessHandler.addProcessListener(processAdapter);
            myProcessHandler.startNotify();
            ExecutionModes.SameThreadMode sameThreadMode = new ExecutionModes.SameThreadMode(getPresentableName());
            ExecutionHelper.executeExternalProcess(myProject, myProcessHandler, sameThreadMode, commandLine);

            LOGGER.debug("Finished `" + getPresentableName() + "` with result: " + result.get());
            return result.get();
        } catch (ExecutionException e) {
            if (myShowOutputOnError) {
                ExecutionHelper.showErrors(myProject, Collections.singletonList(e), getPresentableName(), null);
            }
            if (myShowNotificationsOnError) {
                showNotification(StringUtil.notNullize(e.getMessage(), "Unknown error, see logs for details"),
                        NotificationType.ERROR);
            }
            String commandLineInfo = commandLine != null ? commandLine.getCommandLineString() : "not constructed";
            LOGGER.debug("Finished `" + getPresentableName() + "` with an exception. Commandline: " +
                    commandLineInfo, e);
            return false;
        }
    }

    public void executeWithProgress(boolean modal) {
        //noinspection unchecked
        executeWithProgress(modal, Consumer.EMPTY_CONSUMER);
    }

    public void executeWithProgress(boolean modal, @NotNull Consumer<Boolean> consumer) {
        ProgressManager.getInstance().run(new Task.Backgroundable(myProject, getPresentableName(), true) {

            private boolean doNotStart;

            @Override
            public void onCancel() {
                doNotStart = true;
                ProcessHandler handler = getProcessHandler();
                if (handler != null) {
                    handler.destroyProcess();
                }
            }

            @Override
            public boolean shouldStartInBackground() {
                return !modal;
            }

            @Override
            public boolean isConditionalModal() {
                return modal;
            }

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                if (doNotStart || myProject == null || myProject.isDisposed()) {
                    return;
                }
                indicator.setIndeterminate(true);
                consumer.consume(execute());
            }
        });
    }

    @Nullable
    public ProcessHandler getProcessHandler() {
        return myProcessHandler;
    }

    private void showNotification(@NotNull String message, NotificationType type) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String title = getPresentableName();
            Notifications.Bus.notify(SiddhiConstants.SIDDHI_EXECUTION_NOTIFICATION_GROUP
                    .createNotification(title, message, type, null), myProject);
        });
    }

    private void showOutput(@NotNull OSProcessHandler originalHandler,
                            @NotNull SiddhiHistoryProcessListener historyProcessListener) {
        if (myShowOutputOnError) {
            BaseOSProcessHandler outputHandler = new KillableColoredProcessHandler(originalHandler.getProcess(), null);
            RunContentExecutor runContentExecutor = new RunContentExecutor(myProject, outputHandler)
                    .withTitle(getPresentableName())
                    .withActivateToolWindow(myShowOutputOnError)
                    .withFilter(new SiddhiConsoleFilter(myProject, myModule, myWorkDirectory != null ?
                            VfsUtilCore.pathToUrl(myWorkDirectory) : null));
            Disposer.register(myProject, runContentExecutor);
            runContentExecutor.run();
            historyProcessListener.apply(outputHandler);
        }
        if (myShowNotificationsOnError) {
            showNotification("Failed to run", NotificationType.ERROR);
        }
    }

    @NotNull
    public GeneralCommandLine createCommandLine() throws ExecutionException {
        GeneralCommandLine commandLine = !myPtyDisabled && PtyCommandLine.isEnabled() ?
                new PtyCommandLine() : new GeneralCommandLine();
        commandLine.setExePath(ObjectUtils.notNull(myExePath, ObjectUtils.notNull(SiddhiSdkUtil
                .getSiddhiExecutablePath(myProject, myModule))));
        commandLine.getEnvironment().putAll(myExtraEnvironment);
        //Todo - Add SIDDHI_REPOSITORY
        //        commandLine.getEnvironment().put(SiddhiConstants.SIDDHI_REPOSITORY,
        //                StringUtil.notNullize(mySiddhiPath));

        Collection<String> paths = ContainerUtil.newArrayList();
        ContainerUtil.addIfNotNull(paths, StringUtil.nullize(commandLine.getEnvironment().get(
                SiddhiConstants.PATH), true));
        ContainerUtil.addIfNotNull(paths, StringUtil.nullize(EnvironmentUtil.getValue(SiddhiConstants.PATH), true));
        ContainerUtil.addIfNotNull(paths, StringUtil.nullize(myEnvPath, true));
        commandLine.getEnvironment().put(SiddhiConstants.PATH, StringUtil.join(paths, File.pathSeparator));

        commandLine.withWorkDirectory(myWorkDirectory);
        commandLine.addParameters(myParameterList.getList());
        commandLine.withParentEnvironmentType(myParentEnvironmentType);
        commandLine.withCharset(CharsetToolkit.UTF8_CHARSET);
        return commandLine;
    }

    @NotNull
    private String getPresentableName() {
        return ObjectUtils.notNull(myPresentableName, "Siddhi Executor");
    }
}

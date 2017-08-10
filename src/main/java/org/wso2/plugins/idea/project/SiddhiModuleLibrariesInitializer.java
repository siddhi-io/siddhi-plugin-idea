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

import com.intellij.ProjectTopics;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.OrderEntryUtil;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Alarm;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBusConnection;
import org.wso2.plugins.idea.SiddhiConstants;
import org.wso2.plugins.idea.configuration.SiddhiLibrariesConfigurableProvider;
import org.wso2.plugins.idea.sdk.SiddhiSdkService;
import org.wso2.plugins.idea.sdk.SiddhiSdkUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import javax.swing.event.HyperlinkEvent;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class SiddhiModuleLibrariesInitializer implements ModuleComponent {

    private static final String SIDDHI_LIB_NAME = "SIDDHI_REPOSITORY";
    private static final String SIDDHI_LIBRARIES_NOTIFICATION_HAD_BEEN_SHOWN = "Siddhi.libraries.notification" +
            ".had.been.shown";
    private static final int UPDATE_DELAY = 300;
    private static boolean isTestingMode;

    private final Alarm myAlarm;
    private final MessageBusConnection myConnection;
    private boolean myModuleInitialized;

    @NotNull
    private final Set<VirtualFile> myLastHandledSiddhiPathSourcesRoots = ContainerUtil.newHashSet();
    @NotNull
    private final Set<VirtualFile> myLastHandledExclusions = ContainerUtil.newHashSet();
    @NotNull
    private final Set<LocalFileSystem.WatchRequest> myWatchedRequests = ContainerUtil.newHashSet();

    @NotNull
    private final Module myModule;

    @TestOnly
    public static void setTestingMode(@NotNull Disposable disposable) {
        isTestingMode = true;
        Disposer.register(disposable, () -> {
            //noinspection AssignmentToStaticFieldFromInstanceMethod
            isTestingMode = false;
        });
    }

    public SiddhiModuleLibrariesInitializer(@NotNull Module module) {
        myModule = module;
        myAlarm = ApplicationManager.getApplication().isUnitTestMode() ? new Alarm() :
                new Alarm(Alarm.ThreadToUse.POOLED_THREAD, myModule);
        myConnection = myModule.getMessageBus().connect();
    }

    @Override
    public void moduleAdded() {
        if (!myModuleInitialized) {
            myConnection.subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
                @Override
                public void rootsChanged(ModuleRootEvent event) {
                    scheduleUpdate();
                }
            });
            myConnection.subscribe(SiddhiLibrariesService.LIBRARIES_TOPIC, newRootUrls -> scheduleUpdate());
        }
        scheduleUpdate(0);
        myModuleInitialized = true;
    }

    private void scheduleUpdate() {
        scheduleUpdate(UPDATE_DELAY);
    }

    private void scheduleUpdate(int delay) {
        myAlarm.cancelAllRequests();
        UpdateRequest updateRequest = new UpdateRequest();
        if (isTestingMode) {
            ApplicationManager.getApplication().invokeLater(updateRequest);
        } else {
            myAlarm.addRequest(updateRequest, delay);
        }
    }

    private void attachLibraries(@NotNull Collection<VirtualFile> libraryRoots, Set<VirtualFile> exclusions) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        if (!libraryRoots.isEmpty()) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                ModuleRootManager model = ModuleRootManager.getInstance(myModule);
                LibraryOrderEntry siddhiLibraryEntry = OrderEntryUtil.findLibraryOrderEntry(model, getLibraryName());

                if (siddhiLibraryEntry != null && siddhiLibraryEntry.isValid()) {
                    Library library = siddhiLibraryEntry.getLibrary();
                    if (library != null && !((LibraryEx) library).isDisposed()) {
                        fillLibrary(library, libraryRoots, exclusions);
                    }
                } else {
                    LibraryTable libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(myModule
                            .getProject());
                    Library library = libraryTable.createLibrary(getLibraryName());
                    fillLibrary(library, libraryRoots, exclusions);
                    ModuleRootModificationUtil.addDependency(myModule, library);
                }
            });
            showNotification(myModule.getProject());
        } else {
            removeLibraryIfNeeded();
        }
    }

    public String getLibraryName() {
        return SIDDHI_LIB_NAME + " <" + myModule.getName() + ">";
    }

    private static void fillLibrary(@NotNull Library library, @NotNull Collection<VirtualFile> libraryRoots,
                                    Set<VirtualFile> exclusions) {
        ApplicationManager.getApplication().assertWriteAccessAllowed();

        Library.ModifiableModel libraryModel = library.getModifiableModel();
        for (String root : libraryModel.getUrls(OrderRootType.CLASSES)) {
            libraryModel.removeRoot(root, OrderRootType.CLASSES);
        }
        for (String root : libraryModel.getUrls(OrderRootType.SOURCES)) {
            libraryModel.removeRoot(root, OrderRootType.SOURCES);
        }
        for (VirtualFile libraryRoot : libraryRoots) {
            // in order to consider SIDDHI_REPOSITORY as library and show it in Ext. Libraries
            libraryModel.addRoot(libraryRoot, OrderRootType.CLASSES);
            // In order to find usages inside SIDDHI_REPOSITORY.
            libraryModel.addRoot(libraryRoot, OrderRootType.SOURCES);
        }
        for (VirtualFile root : exclusions) {
            ((LibraryEx.ModifiableModelEx) libraryModel).addExcludedRoot(root.getUrl());
        }
        libraryModel.commit();
    }

    private void removeLibraryIfNeeded() {
        ApplicationManager.getApplication().assertIsDispatchThread();

        ModifiableModelsProvider modelsProvider = ModifiableModelsProvider.SERVICE.getInstance();
        ModifiableRootModel model = modelsProvider.getModuleModifiableModel(myModule);
        LibraryOrderEntry siddhiLibraryEntry = OrderEntryUtil.findLibraryOrderEntry(model, getLibraryName());
        if (siddhiLibraryEntry != null) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                Library library = siddhiLibraryEntry.getLibrary();
                if (library != null) {
                    LibraryTable table = library.getTable();
                    if (table != null) {
                        table.removeLibrary(library);
                        model.removeOrderEntry(siddhiLibraryEntry);
                        modelsProvider.commitModuleModifiableModel(model);
                    }
                } else {
                    modelsProvider.disposeModuleModifiableModel(model);
                }
            });
        } else {
            ApplicationManager.getApplication().runWriteAction(() ->
                    modelsProvider.disposeModuleModifiableModel(model));
        }
    }

    private static void showNotification(@NotNull Project project) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        PropertiesComponent projectPropertiesComponent = PropertiesComponent.getInstance(project);
        boolean shownAlready;
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (propertiesComponent) {
            shownAlready = propertiesComponent.getBoolean(SIDDHI_LIBRARIES_NOTIFICATION_HAD_BEEN_SHOWN, false)
                    || projectPropertiesComponent.getBoolean(SIDDHI_LIBRARIES_NOTIFICATION_HAD_BEEN_SHOWN, false);
            if (!shownAlready) {
                propertiesComponent.setValue(SIDDHI_LIBRARIES_NOTIFICATION_HAD_BEEN_SHOWN, String.valueOf(true));
            }
        }

        if (!shownAlready) {
            NotificationListener.Adapter notificationListener = new NotificationListener.Adapter() {
                @Override
                protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
                    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED
                            && "configure".equals(event.getDescription())) {
                        SiddhiLibrariesConfigurableProvider.showModulesConfigurable(project);
                    }
                }
            };
            Notification notification = SiddhiConstants.SIDDHI_NOTIFICATION_GROUP.createNotification
                    ("SIDDHI_REPOSITORY was detected",
                            "We've detected some packages from your SIDDHI_REPOSITORY.\n" +
                                    "You may want to add extra packages in <a href='configure'>Siddhi Packages " +
                                    "configuration</a>.",
                            NotificationType.INFORMATION, notificationListener);
            Notifications.Bus.notify(notification, project);
        }
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {
        Disposer.dispose(myConnection);
        Disposer.dispose(myAlarm);
        myLastHandledSiddhiPathSourcesRoots.clear();
        myLastHandledExclusions.clear();
        LocalFileSystem.getInstance().removeWatchedRoots(myWatchedRequests);
        myWatchedRequests.clear();
    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {
        disposeComponent();
    }

    @NotNull
    @Override
    public String getComponentName() {
        return getClass().getName();
    }

    private class UpdateRequest implements Runnable {
        @Override
        public void run() {
            Project project = myModule.getProject();
            if (SiddhiSdkService.getInstance(project).isSiddhiModule(myModule)) {
                synchronized (myLastHandledSiddhiPathSourcesRoots) {
                    Collection<VirtualFile> siddhiPathSourcesRoots = SiddhiSdkUtil.getSiddhiPathRoots(project,
                            myModule);
                    Set<VirtualFile> excludeRoots =
                            ContainerUtil.newHashSet(ProjectRootManager.getInstance(project).getContentRoots());
                    ProgressIndicatorProvider.checkCanceled();

                    Collection<VirtualFile> includeRoots = gatherIncludeRoots(siddhiPathSourcesRoots, excludeRoots);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (!myModule.isDisposed() && SiddhiSdkService.getInstance(project)
                                .isSiddhiModule(myModule)) {
                            attachLibraries(includeRoots, excludeRoots);
                        }
                    });

                    myLastHandledSiddhiPathSourcesRoots.clear();
                    myLastHandledSiddhiPathSourcesRoots.addAll(siddhiPathSourcesRoots);

                    myLastHandledExclusions.clear();
                    myLastHandledExclusions.addAll(excludeRoots);

                    List<String> paths = ContainerUtil.map(siddhiPathSourcesRoots, VirtualFile::getPath);
                    myWatchedRequests.clear();
                    myWatchedRequests.addAll(LocalFileSystem.getInstance().addRootsToWatch(paths, true));
                }
            } else {
                synchronized (myLastHandledSiddhiPathSourcesRoots) {
                    LocalFileSystem.getInstance().removeWatchedRoots(myWatchedRequests);
                    myLastHandledSiddhiPathSourcesRoots.clear();
                    myLastHandledExclusions.clear();
                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (!myModule.isDisposed() && SiddhiSdkService.getInstance(project).
                                isSiddhiModule(myModule)) {
                            removeLibraryIfNeeded();
                        }
                    });
                }
            }
        }
    }

    @NotNull
    private static Collection<VirtualFile> gatherIncludeRoots(Collection<VirtualFile> siddhiPathSourcesRoots,
                                                              Set<VirtualFile> excludeRoots) {
        Collection<VirtualFile> includeRoots = ContainerUtil.newHashSet();
        for (VirtualFile siddhiPathSourcesDirectory : siddhiPathSourcesRoots) {
            ProgressIndicatorProvider.checkCanceled();
            boolean excludedRootIsAncestor = false;
            for (VirtualFile excludeRoot : excludeRoots) {
                ProgressIndicatorProvider.checkCanceled();
                if (VfsUtilCore.isAncestor(excludeRoot, siddhiPathSourcesDirectory, false)) {
                    excludedRootIsAncestor = true;
                    break;
                }
            }
            if (excludedRootIsAncestor) {
                continue;
            }
            ProgressIndicatorProvider.checkCanceled();
            if (siddhiPathSourcesDirectory.isDirectory() &&
                    !excludeRoots.contains(siddhiPathSourcesDirectory)) {
                includeRoots.add(siddhiPathSourcesDirectory);
            }
        }
        return includeRoots;
    }
}

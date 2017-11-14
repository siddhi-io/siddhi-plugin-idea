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

package org.wso2.siddhi.plugins.idea.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.Topic;
import com.intellij.util.xmlb.XmlSerializerUtil;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class SiddhiLibrariesService<T extends SiddhiLibraryState> extends SimpleModificationTracker
        implements PersistentStateComponent<T> {

    public static final Topic<LibrariesListener> LIBRARIES_TOPIC = Topic.create("libraries changes",
            LibrariesListener.class);
    protected final T myState = createState();

    @Nonnull
    @Override
    public T getState() {
        return myState;
    }

    @Override
    public void loadState(T state) {
        XmlSerializerUtil.copyBean(state, myState);
    }

    @Nonnull
    protected T createState() {
        //noinspection unchecked
        return (T) new SiddhiLibraryState();
    }

    @Nonnull
    public static Collection<? extends VirtualFile> getUserDefinedLibraries(@Nonnull Module module) {
        Set<VirtualFile> result = ContainerUtil.newLinkedHashSet();
        result.addAll(getUserDefinedLibraries(module.getProject()));
        return result;
    }

    @Nonnull
    public static Collection<? extends VirtualFile> getUserDefinedLibraries(@Nonnull Project project) {
        Set<VirtualFile> result = ContainerUtil.newLinkedHashSet();
        result.addAll(siddhiRootsFromUrls(SiddhiProjectLibrariesService.getInstance(project)
                .getLibraryRootUrls()));
        result.addAll(getUserDefinedLibraries());
        return result;
    }

    @Nonnull
    private static Collection<? extends VirtualFile> getUserDefinedLibraries() {
        return siddhiRootsFromUrls(SiddhiApplicationLibrariesService.getInstance().getLibraryRootUrls());
    }

    @Nonnull
    public static ModificationTracker[] getModificationTrackers(@Nonnull Project project, @Nullable Module module) {
        assert module != null;
        return new ModificationTracker[]{SiddhiProjectLibrariesService.getInstance(module.getProject()),
                SiddhiApplicationLibrariesService.getInstance()};
    }

    public void setLibraryRootUrls(@Nonnull String... libraryRootUrls) {
        setLibraryRootUrls(Arrays.asList(libraryRootUrls));
    }

    public void setLibraryRootUrls(@Nonnull Collection<String> libraryRootUrls) {
        if (!myState.getUrls().equals(libraryRootUrls)) {
            myState.setUrls(libraryRootUrls);
            incModificationCount();
            ApplicationManager.getApplication().getMessageBus().syncPublisher(LIBRARIES_TOPIC)
                    .librariesChanged(libraryRootUrls);
        }
    }

    @Nonnull
    public Collection<String> getLibraryRootUrls() {
        return myState.getUrls();
    }

    @Nonnull
    private static Collection<? extends VirtualFile> siddhiRootsFromUrls(@Nonnull Collection<String> urls) {
        return ContainerUtil.mapNotNull(urls, url -> VirtualFileManager.getInstance().findFileByUrl(url));
    }

    public interface LibrariesListener {
        void librariesChanged(@Nonnull Collection<String> newRootUrls);
    }
}

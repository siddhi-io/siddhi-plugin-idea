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

package org.wso2.siddhi.plugins.idea.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.siddhi.plugins.idea.SiddhiIcons;

import java.util.Map;
import javax.swing.Icon;

/**
 * Class which defines the custom page shown in the "Colors and Fonts" settings dialog.
 */
public class SiddhiColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Keywords", SiddhiSyntaxHighlightingColors.KEYWORD),
            new AttributesDescriptor("Strings", SiddhiSyntaxHighlightingColors.STRING),
            new AttributesDescriptor("Comments", SiddhiSyntaxHighlightingColors.LINE_COMMENT),
            new AttributesDescriptor("StreamId", SiddhiSyntaxHighlightingColors.STREAM_ID),
            new AttributesDescriptor("Semicolon", SiddhiSyntaxHighlightingColors.SEMICOLON),
            new AttributesDescriptor("Colon", SiddhiSyntaxHighlightingColors.COL),
            new AttributesDescriptor("Comma", SiddhiSyntaxHighlightingColors.COMMA),
            new AttributesDescriptor("Symbols", SiddhiSyntaxHighlightingColors.SYMBOLS),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return SiddhiIcons.ICON;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new SiddhiSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "define stream TempStream (deviceID long, roomNo int, temp double);" +
                "from TempStream \n" +
                "select roomNo, temp\n" +
                "insert into RoomTempStream;";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        AttributesDescriptor[] descriptorsCopy = DESCRIPTORS;
        return descriptorsCopy;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Siddhi";
    }
}

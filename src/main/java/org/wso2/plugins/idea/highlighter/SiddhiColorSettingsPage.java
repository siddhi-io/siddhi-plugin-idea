package org.wso2.plugins.idea.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.plugins.idea.SiddhiIcons;

import javax.swing.*;
import java.util.Map;

public class SiddhiColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            //todo: add more attributes
            new AttributesDescriptor("Keywords", SiddhiSyntaxHighlightingColors.KEYWORD),
            new AttributesDescriptor("Strings", SiddhiSyntaxHighlightingColors.STRING),
            //new AttributesDescriptor("Numbers", SiddhiSyntaxHighlightingColors.NUMBER),
            //new AttributesDescriptor("Identifiers", SiddhiSyntaxHighlightingColors.IDENTIFIER),
            new AttributesDescriptor("Comments", SiddhiSyntaxHighlightingColors.LINE_COMMENT),
            new AttributesDescriptor("streamId", SiddhiSyntaxHighlightingColors.STREAM_ID),
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
        return DESCRIPTORS;
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
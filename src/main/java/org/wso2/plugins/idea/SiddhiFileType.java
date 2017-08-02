package org.wso2.plugins.idea;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class SiddhiFileType extends LanguageFileType {

    public static final SiddhiFileType INSTANCE = new SiddhiFileType();

    private SiddhiFileType() {
        super(SiddhiLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Siddhi file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Siddhi language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "siddhi";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return SiddhiIcons.ICON;
    }
}

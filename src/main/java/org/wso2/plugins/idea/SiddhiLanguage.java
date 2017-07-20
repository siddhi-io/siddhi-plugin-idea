package org.wso2.plugins.idea;

import com.intellij.lang.Language;

public class SiddhiLanguage extends Language {

    public static final SiddhiLanguage INSTANCE = new SiddhiLanguage();

    private SiddhiLanguage() {
        super("Siddhi");
    }
}

package org.wso2.plugins.idea.highlighter;

import com.intellij.ide.highlighter.JavaHighlightingColors;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class SiddhiSyntaxHighlightingColors {

    public static final TextAttributesKey LINE_COMMENT = createTextAttributesKey("SIDDHI_LINE_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey KEYWORD = createTextAttributesKey("SIDDHI_KEYWORD",
            JavaHighlightingColors.KEYWORD);
    public static final TextAttributesKey STRING = createTextAttributesKey("SIDDHI_STRING",
            DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("SIDDHI_BAD_TOKEN",
            HighlighterColors.BAD_CHARACTER);

    private SiddhiSyntaxHighlightingColors() {

    }
}

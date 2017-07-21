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
    public static final TextAttributesKey STREAM_ID = createTextAttributesKey("SIDDHI_STREAM_TOKEN",
            DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);


//    public static final TextAttributesKey NUMBER = createTextAttributesKey("SIDDHI_NUMBER",
//            DefaultLanguageHighlighterColors.NUMBER);
//    public static final TextAttributesKey OPERATOR = createTextAttributesKey("SIDDHI_OPERATOR",
//            DefaultLanguageHighlighterColors.OPERATION_SIGN);
//    public static final TextAttributesKey IDENTIFIER = createTextAttributesKey("SIDDHI_IDENTIFIER",
//            DefaultLanguageHighlighterColors.IDENTIFIER);
//    public static final TextAttributesKey ANNOTATION = createTextAttributesKey("SIDDHI_ANNOTATION",
//            DefaultLanguageHighlighterColors.METADATA);
//    public static final TextAttributesKey CONSTANT = createTextAttributesKey("SIDDHI_CONSTANT",
//            DefaultLanguageHighlighterColors.CONSTANT);
//    public static final TextAttributesKey VALID_STRING_ESCAPE = createTextAttributesKey("SIDDHI_VALID_STRING_ESCAPE",
//            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);
//    public static final TextAttributesKey INVALID_STRING_ESCAPE = createTextAttributesKey(
//            "SIDDHI_INVALID_STRING_ESCAPE", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
//    public static final TextAttributesKey PACKAGE = createTextAttributesKey("SIDDHI_PACKAGE",
//            DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
//    public static final TextAttributesKey STATIC_FIELD = createTextAttributesKey("SIDDHI_STATIC_FIELD",
//            DefaultLanguageHighlighterColors.STATIC_FIELD);
//    public static final TextAttributesKey TEMPLATE_LANGUAGE_COLOR = createTextAttributesKey
//            ("SIDDHI_TEMPLATE_LANGUAGE_COLOR", DefaultLanguageHighlighterColors.STATIC_METHOD);

    private SiddhiSyntaxHighlightingColors() {

    }
}

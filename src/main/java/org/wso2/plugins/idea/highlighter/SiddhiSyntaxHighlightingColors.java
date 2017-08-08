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
            DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey STRING = createTextAttributesKey("SIDDHI_STRING",
            DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("SIDDHI_BAD_TOKEN",
            HighlighterColors.BAD_CHARACTER);
    public static final TextAttributesKey STREAM_ID = createTextAttributesKey("SIDDHI_STREAM_TOKEN",
            DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey SEMICOLON = createTextAttributesKey("SIDDHI_COMMA_TOKEN",
            DefaultLanguageHighlighterColors.SEMICOLON);
    public static final TextAttributesKey DOT = createTextAttributesKey("SIDDHI_DOT_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey COL = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey TRIPLE_DOT = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey PARENTHESIS = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey SQUARE_BRACKETS = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey COMMA = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.COMMA);
    public static final TextAttributesKey SYMBOLS = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    /*
    public static final TextAttributesKey ASSIGN = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey STAR = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey PLUS = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey QUESTION = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey MINUS = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey DIV = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey MOD = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey LT = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey LT_EQ = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey GT = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey GT_EQ = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey NOT_EQ = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey AT_SYMBOL = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey FOLLOWED_BY = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey HASH = createTextAttributesKey("SIDDHI_COLON_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    */

    private SiddhiSyntaxHighlightingColors() {

    }
}

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

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Defines syntax highlighting colors of siddhi language
 */
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
    public static final TextAttributesKey TRIPLE_DOT = createTextAttributesKey("SIDDHI_TRIPLE_DOT_TOKEN",
            DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey PARENTHESIS = createTextAttributesKey("SIDDHI_PARENTHESIS_TOKEN",
            DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey SQUARE_BRACKETS = createTextAttributesKey("SIDDHI_SQUARE_BRACKETS_TOKEN",
            DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey COMMA = createTextAttributesKey("SIDDHI_COMMA_TOKEN",
            DefaultLanguageHighlighterColors.COMMA);
    public static final TextAttributesKey SYMBOLS = createTextAttributesKey("SIDDHI_SYMBOLS_TOKEN",
            DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    public static final TextAttributesKey VALID_STRING_ESCAPE = createTextAttributesKey("SIDDHI_VALID_STRING_ESCAPE",
            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);
    public static final TextAttributesKey INVALID_STRING_ESCAPE = createTextAttributesKey(
            "SIDDHI_INVALID_STRING_ESCAPE", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);

    //TODO:color only comma ,#,semicolon
    private SiddhiSyntaxHighlightingColors() {

    }
}

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

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.antlr.jetbrains.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.jetbrains.adaptor.lexer.TokenIElementType;
import org.jetbrains.annotations.NotNull;
import org.wso2.siddhi.plugins.idea.SiddhiLanguage;
import org.wso2.siddhi.plugins.idea.SiddhiParserDefinition;
import org.wso2.siddhi.plugins.idea.grammar.SiddhiQLLexer;

import java.util.HashMap;
import java.util.Map;

/**
 *  A highlighter is really just a mapping from token type to
 *  some text attributes using {@link #getTokenHighlights(IElementType)}.
 *  The reason that it returns an array, TextAttributesKey[], is
 *  that you might want to mix the attributes of a few known highlighters.
 *  A {@link TextAttributesKey} is just a name for that a theme
 *  or IDE skin can set. For example, {@link com.intellij.openapi.editor.DefaultLanguageHighlighterColors#KEYWORD}
 *  is the key that maps to what identifiers look like in the editor.
 *  To change it, see dialog: Editor > Colors & Fonts > Language Defaults.
 *
 *  From <a href="http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/syntax_highlighting_and_error_highlighting.html">doc</a>:
 *  "The mapping of the TextAttributesKey to specific attributes used
 *  in an editor is defined by the EditorColorsScheme class, and can
 *  be configured by the user if the plugin provides an appropriate
 *  configuration interface.
 *  ...
 *  The syntax highlighter returns the {@link TextAttributesKey}
 * instances for each token type which needs special highlighting.
 * For highlighting lexer errors, the standard TextAttributesKey
 * for bad characters HighlighterColors.BAD_CHARACTER can be used."
 */
public class SiddhiSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<>();

    static {
        fillMap(ATTRIBUTES, SiddhiParserDefinition.COMMENTS, SiddhiSyntaxHighlightingColors.LINE_COMMENT);
        fillMap(ATTRIBUTES, SiddhiParserDefinition.KEYWORDS, SiddhiSyntaxHighlightingColors.KEYWORD);
        fillMap(ATTRIBUTES, SiddhiParserDefinition.STRING_LITERALS, SiddhiSyntaxHighlightingColors.STRING);
        fillMap(ATTRIBUTES, SiddhiParserDefinition.BAD_CHARACTER, SiddhiSyntaxHighlightingColors.BAD_CHARACTER);
        fillMap(ATTRIBUTES, SiddhiParserDefinition.SEMICOLON, SiddhiSyntaxHighlightingColors.SEMICOLON);
        fillMap(ATTRIBUTES, SiddhiParserDefinition.COLON, SiddhiSyntaxHighlightingColors.COL);
        fillMap(ATTRIBUTES, SiddhiParserDefinition.COMMA, SiddhiSyntaxHighlightingColors.COMMA);
        fillMap(ATTRIBUTES, SiddhiParserDefinition.SYMBOLS, SiddhiSyntaxHighlightingColors.SYMBOLS);
    }

    @NotNull
    public Lexer getHighlightingLexer() {
        SiddhiQLLexer lexer = new SiddhiQLLexer(null);
        return new ANTLRLexerAdaptor(SiddhiLanguage.INSTANCE, lexer);
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (!(tokenType instanceof TokenIElementType)) {
            return EMPTY_KEYS;
        }
        TokenIElementType myType = (TokenIElementType) tokenType;
        return pack(ATTRIBUTES.get(myType));
    }
}

package org.wso2.plugins.idea.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.antlr.jetbrains.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.jetbrains.adaptor.lexer.TokenIElementType;
import org.jetbrains.annotations.NotNull;
import org.wso2.plugins.idea.SiddhiLanguage;
import org.wso2.plugins.idea.SiddhiParserDefinition;
import org.wso2.plugins.idea.grammar.SiddhiQLLexer;

import java.util.HashMap;
import java.util.Map;

public class SiddhiSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<>();

    static {
        fillMap(ATTRIBUTES, SiddhiParserDefinition.COMMENTS, SiddhiSyntaxHighlightingColors.LINE_COMMENT);
        fillMap(ATTRIBUTES, SiddhiParserDefinition.KEYWORDS, SiddhiSyntaxHighlightingColors.KEYWORD);
        fillMap(ATTRIBUTES, SiddhiParserDefinition.STRING_LITERALS, SiddhiSyntaxHighlightingColors.STRING);
        fillMap(ATTRIBUTES, SiddhiParserDefinition.BAD_CHARACTER, SiddhiSyntaxHighlightingColors.BAD_CHARACTER);
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

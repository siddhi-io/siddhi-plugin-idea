package org.wso2.plugins.idea;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.antlr.jetbrains.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.jetbrains.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.jetbrains.adaptor.lexer.RuleIElementType;
import org.antlr.jetbrains.adaptor.lexer.TokenIElementType;
import org.antlr.jetbrains.adaptor.parser.ANTLRParserAdaptor;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;
import org.wso2.plugins.idea.grammar.SiddhiQLLexer;
import org.wso2.plugins.idea.grammar.SiddhiQLParser;
import org.wso2.plugins.idea.psi.SiddhiFile;

import static org.wso2.plugins.idea.grammar.SiddhiQLLexer.*;

public class SiddhiParserDefinition implements ParserDefinition {

    private static final IFileElementType FILE = new IFileElementType(SiddhiLanguage.INSTANCE);

    static {
        PSIElementTypeFactory.defineLanguageIElementTypes(SiddhiLanguage.INSTANCE, SiddhiQLParser.tokenNames,
                SiddhiQLParser.ruleNames);
    }

    public static final TokenSet KEYWORDS = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE, STREAM,
            DEFINE, FUNCTION, TRIGGER, TABLE, APP, FROM, PARTITION, WINDOW, SELECT, GROUP, BY, HAVING, INSERT,
            DELETE, UPDATE, RETURN, EVENTS, INTO, OUTPUT, EXPIRED, CURRENT, SNAPSHOT, FOR, RAW, OF, AS, AT, OR, AND,
            IN, ON, IS, NOT, WITHIN, WITH, BEGIN, END, NULL, EVERY, LAST, ALL, FIRST, JOIN, INNER, OUTER, RIGHT, LEFT,
            FULL, UNIDIRECTIONAL, YEARS, MONTHS, WEEKS, DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS, FALSE, TRUE,
            STRING, INT, LONG, FLOAT, DOUBLE, BOOL, OBJECT, AGGREGATION, AGGREGATE, PER);


    public static final TokenSet COMMENTS = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SINGLE_LINE_COMMENT, MULTILINE_COMMENT);

    public static final TokenSet WHITESPACE = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SPACES);

    public static final TokenSet STRING_LITERALS = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            STRING_LITERAL);

    public static final TokenSet BAD_CHARACTER = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            UNEXPECTED_CHAR);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        SiddhiQLLexer lexer = new SiddhiQLLexer(null);
        return new ANTLRLexerAdaptor(SiddhiLanguage.INSTANCE, lexer);
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        final SiddhiQLParser parser = new SiddhiQLParser(null);
        return new ANTLRParserAdaptor(SiddhiLanguage.INSTANCE, parser) {
            @Override
            protected ParseTree parse(Parser parser, IElementType root) {
                // Start rule depends on root passed in; sometimes we want to create an ID node etc...
                // Eg: if (root instanceof IFileElementType) { }
                return ((SiddhiQLParser) parser).siddhi_app();
            }
        };
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITESPACE;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return STRING_LITERALS;
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new SiddhiFile(viewProvider);
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        IElementType elementType = node.getElementType();
        if (elementType instanceof TokenIElementType) {
            return new ANTLRPsiNode(node);
        }
        if (!(elementType instanceof RuleIElementType)) {
            return new ANTLRPsiNode(node);
        }

        RuleIElementType ruleElType = (RuleIElementType) elementType;
        switch (ruleElType.getRuleIndex()) {
            default:
                return new ANTLRPsiNode(node);
        }
    }
}

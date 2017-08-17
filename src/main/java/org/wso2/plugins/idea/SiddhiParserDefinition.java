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
import org.wso2.plugins.idea.psi.*;

import static org.wso2.plugins.idea.grammar.SiddhiQLParser.*;

public class SiddhiParserDefinition implements ParserDefinition {

    private static final IFileElementType FILE = new IFileElementType(SiddhiLanguage.INSTANCE);

    static {
        PSIElementTypeFactory.defineLanguageIElementTypes(SiddhiLanguage.INSTANCE, SiddhiQLParser.tokenNames,
                SiddhiQLParser.ruleNames);
    }

    public static final TokenSet KEYWORDS = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE, STREAM,
            DEFINE, FUNCTION, TRIGGER, TABLE, APP, FROM, PARTITION, WINDOW, SELECT, GROUP, BY, HAVING, INSERT,
            DELETE, UPDATE, RETURN, EVENTS, INTO, OUTPUT, EXPIRED, CURRENT, SNAPSHOT, FOR, RAW, OF, AS, AT, OR, AND,
            ON, IN, IS, NOT, WITHIN, WITH, BEGIN, END, NULL, EVERY, LAST, ALL, FIRST, JOIN, INNER, OUTER, RIGHT, LEFT,
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
    public static final TokenSet SEMICOLON = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SCOL);
    public static final TokenSet DOT = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.DOT);
    public static final TokenSet COLON = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.COL);
    public static final TokenSet TRIPLE_DOT = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.TRIPLE_DOT);
    public static final TokenSet PARENTHESIS = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.OPEN_PAR,CLOSE_PAR);
    public static final TokenSet SQUARE_BRACKETS = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.OPEN_SQUARE_BRACKETS,CLOSE_SQUARE_BRACKETS);
    public static final TokenSet COMMA = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.COMMA);
    public static final TokenSet SYMBOLS = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            ASSIGN,STAR,PLUS,QUESTION,MINUS,DIV,MOD,LT,LT_EQ,GT,GT_EQ,EQ,NOT_EQ,AT_SYMBOL,FOLLOWED_BY,HASH);
    /*
    public static final TokenSet ASSIGN = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.ASSIGN);
    public static final TokenSet STAR = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.STAR);
    public static final TokenSet PLUS = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.PLUS);
    public static final TokenSet QUESTION = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.QUESTION);
    public static final TokenSet MINUS = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.MINUS);
    public static final TokenSet DIV = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.DIV);
    public static final TokenSet MOD = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.MOD);
    public static final TokenSet LT = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.LT);
    public static final TokenSet LT_EQ = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.LT_EQ);
    public static final TokenSet GT = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.GT);
    public static final TokenSet GT_EQ = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.GT_EQ);
    public static final TokenSet EQ = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.EQ);
    public static final TokenSet NOT_EQ = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.NOT_EQ);
    public static final TokenSet AT_SYMBOL = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.AT_SYMBOL);
    public static final TokenSet FOLLOWED_BY = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.FOLLOWED_BY);
    public static final TokenSet HASH = PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE,
            SiddhiQLParser.HASH); */

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
                return ((SiddhiQLParser) parser).parse();
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
            case RULE_stream_id:
                return new StreamIdNode(node);
            case RULE_attribute_type:
                return new AttributeTypeNode(node);
            case RULE_trigger_name:
                return new TriggerNameNode(node);
            case RULE_definition_trigger:
                return new TriggerDefinitionNode(node);
            case RULE_definition_window:
                return new WindowDefinitionNode(node);
            case RULE_function_operation:
                return new FunctionOperationNode(node);
            case RULE_attribute_name:
                return new AttributeNameNode(node);
            case RULE_language_name:
                return new LanguageNameNode(node);
            case RULE_function_body:
                return new FunctionBodyNode(node);
            case RULE_definition_function:
                return new FunctionDefinitionNode(node);
            case RULE_function_name:
                return new FunctionNameNode(node);
            case RULE_siddhi_app:
                return new SiddhiAppNode(node);
            case RULE_output_event_type:
                return new OutputEventTypeNode(node);
            case RULE_parse:
                return new ParseNode(node);
            case RULE_execution_element:
                return new ExecutionElementNode(node);
            case RULE_annotation_element:
                return new AnnotationElementNode(node);
            case RULE_annotation:
                return new AnnotationNode(node);
            case RULE_app_annotation:
                return new AppAnnotationNode(node);
//            case RULE_app_annotation:
//                return new AttributeTypeNode(node);
//            case RULE_annotation_element:
//                return new AttributeTypeNode(node);
            default:
                return new ANTLRPsiNode(node);
        }
    }
}
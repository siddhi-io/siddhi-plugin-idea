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

package org.wso2.siddhi.plugins.idea;

import com.intellij.psi.tree.TokenSet;
import org.antlr.jetbrains.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.jetbrains.adaptor.lexer.RuleIElementType;
import org.antlr.jetbrains.adaptor.lexer.TokenIElementType;
import org.wso2.siddhi.plugins.idea.grammar.SiddhiQLLexer;

import java.util.List;

/**
 *  Represents a token in the language of the plug-in. The "token type" of
 *  leaf nodes in jetbrains PSI tree. Corresponds to ANTLR's int token type.
 *  Intellij lexer token types are instances of IElementType:
 *
 *  "Interface for token types returned from lexical analysis and for types
 *   of nodes in the AST tree."
 *
 *  We differentiate between parse tree subtree roots and tokens with
 *  {@link RuleIElementType} and {@link TokenIElementType}, respectively.
 */
public class SiddhiTypes {

    private SiddhiTypes() {

    }

    private static final List<TokenIElementType> tokenIElementTypes =
            PSIElementTypeFactory.getTokenIElementTypes(SiddhiLanguage.INSTANCE);

    public static final TokenIElementType IDENTIFIER = tokenIElementTypes.get(SiddhiQLLexer.ID);
    public static final TokenIElementType INT_LITERAL = tokenIElementTypes.get(SiddhiQLLexer.INT_LITERAL);
    public static final TokenIElementType LONG_LITERAL = tokenIElementTypes.get(SiddhiQLLexer.LONG_LITERAL);
    public static final TokenIElementType FLOAT_LITERAL = tokenIElementTypes.get(SiddhiQLLexer.FLOAT_LITERAL);
    public static final TokenIElementType DOUBLE_LITERAL = tokenIElementTypes.get(SiddhiQLLexer.DOUBLE_LITERAL);
    public static final TokenIElementType COL = tokenIElementTypes.get(SiddhiQLLexer.COL);
    public static final TokenIElementType SCOL = tokenIElementTypes.get(SiddhiQLLexer.SCOL);
    public static final TokenIElementType DOT = tokenIElementTypes.get(SiddhiQLLexer.DOT);
    public static final TokenIElementType TRIPLE_DOT = tokenIElementTypes.get(SiddhiQLLexer.TRIPLE_DOT);
    public static final TokenIElementType OPEN_PAR = tokenIElementTypes.get(SiddhiQLLexer.OPEN_PAR);
    public static final TokenIElementType CLOSE_PAR = tokenIElementTypes.get(SiddhiQLLexer.CLOSE_PAR);
    public static final TokenIElementType OPEN_SQUARE_BRACKETS = tokenIElementTypes.get(SiddhiQLLexer.OPEN_SQUARE_BRACKETS);
    public static final TokenIElementType CLOSE_SQUARE_BRACKETS = tokenIElementTypes.get(SiddhiQLLexer.CLOSE_SQUARE_BRACKETS);
    public static final TokenIElementType COMMA = tokenIElementTypes.get(SiddhiQLLexer.COMMA);
    public static final TokenIElementType ASSIGN = tokenIElementTypes.get(SiddhiQLLexer.ASSIGN);
    public static final TokenIElementType STAR = tokenIElementTypes.get(SiddhiQLLexer.STAR);
    public static final TokenIElementType PLUS = tokenIElementTypes.get(SiddhiQLLexer.PLUS);
    public static final TokenIElementType QUESTION = tokenIElementTypes.get(SiddhiQLLexer.QUESTION);
    public static final TokenIElementType MINUS = tokenIElementTypes.get(SiddhiQLLexer.MINUS);
    public static final TokenIElementType DIV = tokenIElementTypes.get(SiddhiQLLexer.DIV);
    public static final TokenIElementType MOD = tokenIElementTypes.get(SiddhiQLLexer.MOD);
    public static final TokenIElementType LT = tokenIElementTypes.get(SiddhiQLLexer.LT);
    public static final TokenIElementType LT_EQ = tokenIElementTypes.get(SiddhiQLLexer.LT_EQ);
    public static final TokenIElementType GT = tokenIElementTypes.get(SiddhiQLLexer.GT);
    public static final TokenIElementType GT_EQ = tokenIElementTypes.get(SiddhiQLLexer.GT_EQ);
    public static final TokenIElementType EQ = tokenIElementTypes.get(SiddhiQLLexer.EQ);
    public static final TokenIElementType NOT_EQ = tokenIElementTypes.get(SiddhiQLLexer.NOT_EQ);
    public static final TokenIElementType AT_SYMBOL = tokenIElementTypes.get(SiddhiQLLexer.AT_SYMBOL);
    public static final TokenIElementType FOLLOWED_BY = tokenIElementTypes.get(SiddhiQLLexer.FOLLOWED_BY);
    public static final TokenIElementType HASH = tokenIElementTypes.get(SiddhiQLLexer.HASH);
    public static final TokenIElementType STREAM = tokenIElementTypes.get(SiddhiQLLexer.STREAM);
    public static final TokenIElementType DEFINE = tokenIElementTypes.get(SiddhiQLLexer.DEFINE);
    public static final TokenIElementType FUNCTION = tokenIElementTypes.get(SiddhiQLLexer.FUNCTION);
    public static final TokenIElementType TRIGGER = tokenIElementTypes.get(SiddhiQLLexer.TRIGGER);
    public static final TokenIElementType TABLE = tokenIElementTypes.get(SiddhiQLLexer.TABLE);
    public static final TokenIElementType APP = tokenIElementTypes.get(SiddhiQLLexer.APP);
    public static final TokenIElementType FROM = tokenIElementTypes.get(SiddhiQLLexer.FROM);
    public static final TokenIElementType PARTITION = tokenIElementTypes.get(SiddhiQLLexer.PARTITION);
    public static final TokenIElementType WINDOW = tokenIElementTypes.get(SiddhiQLLexer.WINDOW);
    public static final TokenIElementType SELECT = tokenIElementTypes.get(SiddhiQLLexer.SELECT);
    public static final TokenIElementType GROUP = tokenIElementTypes.get(SiddhiQLLexer.GROUP);
    public static final TokenIElementType BY = tokenIElementTypes.get(SiddhiQLLexer.BY);
    public static final TokenIElementType HAVING = tokenIElementTypes.get(SiddhiQLLexer.HAVING);
    public static final TokenIElementType INSERT = tokenIElementTypes.get(SiddhiQLLexer.INSERT);
    public static final TokenIElementType DELETE = tokenIElementTypes.get(SiddhiQLLexer.DELETE);
    public static final TokenIElementType UPDATE = tokenIElementTypes.get(SiddhiQLLexer.UPDATE);
    public static final TokenIElementType SET = tokenIElementTypes.get(SiddhiQLLexer.SET);
    public static final TokenIElementType RETURN = tokenIElementTypes.get(SiddhiQLLexer.RETURN);
    public static final TokenIElementType EVENTS = tokenIElementTypes.get(SiddhiQLLexer.EVENTS);
    public static final TokenIElementType INTO = tokenIElementTypes.get(SiddhiQLLexer.INTO);
    public static final TokenIElementType OUTPUT = tokenIElementTypes.get(SiddhiQLLexer.OUTPUT);
    public static final TokenIElementType EXPIRED = tokenIElementTypes.get(SiddhiQLLexer.EXPIRED);
    public static final TokenIElementType CURRENT = tokenIElementTypes.get(SiddhiQLLexer.CURRENT);
    public static final TokenIElementType SNAPSHOT = tokenIElementTypes.get(SiddhiQLLexer.SNAPSHOT);
    public static final TokenIElementType FOR = tokenIElementTypes.get(SiddhiQLLexer.FOR);
    public static final TokenIElementType RAW = tokenIElementTypes.get(SiddhiQLLexer.RAW);
    public static final TokenIElementType OF = tokenIElementTypes.get(SiddhiQLLexer.OF);
    public static final TokenIElementType AS = tokenIElementTypes.get(SiddhiQLLexer.AS);
    public static final TokenIElementType AT = tokenIElementTypes.get(SiddhiQLLexer.AT);
    public static final TokenIElementType OR = tokenIElementTypes.get(SiddhiQLLexer.OR);
    public static final TokenIElementType AND = tokenIElementTypes.get(SiddhiQLLexer.AND);
    public static final TokenIElementType IN = tokenIElementTypes.get(SiddhiQLLexer.IN);
    public static final TokenIElementType ON = tokenIElementTypes.get(SiddhiQLLexer.ON);
    public static final TokenIElementType IS = tokenIElementTypes.get(SiddhiQLLexer.IS);
    public static final TokenIElementType NOT = tokenIElementTypes.get(SiddhiQLLexer.NOT);
    public static final TokenIElementType WITHIN = tokenIElementTypes.get(SiddhiQLLexer.WITHIN);
    public static final TokenIElementType WITH = tokenIElementTypes.get(SiddhiQLLexer.WITH);
    public static final TokenIElementType BEGIN = tokenIElementTypes.get(SiddhiQLLexer.BEGIN);
    public static final TokenIElementType END = tokenIElementTypes.get(SiddhiQLLexer.END);
    public static final TokenIElementType NULL = tokenIElementTypes.get(SiddhiQLLexer.NULL);
    public static final TokenIElementType EVERY = tokenIElementTypes.get(SiddhiQLLexer.EVERY);
    public static final TokenIElementType LAST = tokenIElementTypes.get(SiddhiQLLexer.LAST);
    public static final TokenIElementType ALL = tokenIElementTypes.get(SiddhiQLLexer.ALL);
    public static final TokenIElementType FIRST = tokenIElementTypes.get(SiddhiQLLexer.FIRST);
    public static final TokenIElementType JOIN = tokenIElementTypes.get(SiddhiQLLexer.JOIN);
    public static final TokenIElementType INNER = tokenIElementTypes.get(SiddhiQLLexer.INNER);
    public static final TokenIElementType OUTER = tokenIElementTypes.get(SiddhiQLLexer.OUTER);
    public static final TokenIElementType RIGHT = tokenIElementTypes.get(SiddhiQLLexer.RIGHT);
    public static final TokenIElementType LEFT = tokenIElementTypes.get(SiddhiQLLexer.LEFT);
    public static final TokenIElementType FULL = tokenIElementTypes.get(SiddhiQLLexer.FULL);
    public static final TokenIElementType UNIDIRECTIONAL = tokenIElementTypes.get(SiddhiQLLexer.UNIDIRECTIONAL);
    public static final TokenIElementType YEARS = tokenIElementTypes.get(SiddhiQLLexer.YEARS);
    public static final TokenIElementType MONTHS = tokenIElementTypes.get(SiddhiQLLexer.MONTHS);
    public static final TokenIElementType WEEKS = tokenIElementTypes.get(SiddhiQLLexer.WEEKS);
    public static final TokenIElementType DAYS = tokenIElementTypes.get(SiddhiQLLexer.DAYS);
    public static final TokenIElementType HOURS = tokenIElementTypes.get(SiddhiQLLexer.HOURS);
    public static final TokenIElementType MINUTES = tokenIElementTypes.get(SiddhiQLLexer.MINUTES);
    public static final TokenIElementType SECONDS = tokenIElementTypes.get(SiddhiQLLexer.SECONDS);
    public static final TokenIElementType MILLISECONDS = tokenIElementTypes.get(SiddhiQLLexer.MILLISECONDS);
    public static final TokenIElementType FALSE = tokenIElementTypes.get(SiddhiQLLexer.FALSE);
    public static final TokenIElementType TRUE = tokenIElementTypes.get(SiddhiQLLexer.TRUE);
    public static final TokenIElementType STRING = tokenIElementTypes.get(SiddhiQLLexer.STRING);
    public static final TokenIElementType INT = tokenIElementTypes.get(SiddhiQLLexer.INT);
    public static final TokenIElementType LONG = tokenIElementTypes.get(SiddhiQLLexer.LONG);
    public static final TokenIElementType FLOAT = tokenIElementTypes.get(SiddhiQLLexer.FLOAT);
    public static final TokenIElementType DOUBLE = tokenIElementTypes.get(SiddhiQLLexer.DOUBLE);
    public static final TokenIElementType BOOL = tokenIElementTypes.get(SiddhiQLLexer.BOOL);
    public static final TokenIElementType OBJECT = tokenIElementTypes.get(SiddhiQLLexer.OBJECT);
    public static final TokenIElementType AGGREGATION = tokenIElementTypes.get(SiddhiQLLexer.AGGREGATION);
    public static final TokenIElementType AGGREGATE = tokenIElementTypes.get(SiddhiQLLexer.AGGREGATE);
    public static final TokenIElementType PER = tokenIElementTypes.get(SiddhiQLLexer.PER);
    public static final TokenIElementType ID_QUOTES = tokenIElementTypes.get(SiddhiQLLexer.ID_QUOTES);
    public static final TokenIElementType STRING_LITERAL = tokenIElementTypes.get(SiddhiQLLexer.STRING_LITERAL);
    public static final TokenIElementType SINGLE_LINE_COMMENT = tokenIElementTypes.get(SiddhiQLLexer.SINGLE_LINE_COMMENT);
    public static final TokenIElementType MULTILINE_COMMENT = tokenIElementTypes.get(SiddhiQLLexer.MULTILINE_COMMENT);
    public static final TokenIElementType SPACES = tokenIElementTypes.get(SiddhiQLLexer.SPACES);
    public static final TokenIElementType UNEXPECTED_CHAR = tokenIElementTypes.get(SiddhiQLLexer.UNEXPECTED_CHAR);
    public static final TokenIElementType SCRIPT = tokenIElementTypes.get(SiddhiQLLexer.SCRIPT);

    public static final TokenSet OPERATORS = TokenSet.create(ASSIGN, STAR, PLUS, MINUS, DIV, MOD, LT, LT_EQ, GT, GT_EQ,
            EQ, NOT_EQ);
}

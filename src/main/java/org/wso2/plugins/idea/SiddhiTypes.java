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

package org.wso2.plugins.idea;

import org.antlr.jetbrains.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.jetbrains.adaptor.lexer.RuleIElementType;
import org.antlr.jetbrains.adaptor.lexer.TokenIElementType;
import org.wso2.plugins.idea.grammar.SiddhiQLLexer;

import java.util.List;

import static org.wso2.plugins.idea.grammar.SiddhiQLParser.*;

public class SiddhiTypes {

    private SiddhiTypes() {

    }

//    private static final List<RuleIElementType> ruleIElementTypes =
//            PSIElementTypeFactory.getRuleIElementTypes(SiddhiLanguage.INSTANCE);

    private static final List<TokenIElementType> tokenIElementTypes =
            PSIElementTypeFactory.getTokenIElementTypes(SiddhiLanguage.INSTANCE);

    public static final TokenIElementType IDENTIFIER = tokenIElementTypes.get(SiddhiQLLexer.ID);

    // Other tokens
    public static final TokenIElementType DEFINE = tokenIElementTypes.get(SiddhiQLLexer.DEFINE);
    public static final TokenIElementType AT = tokenIElementTypes.get(SiddhiQLLexer.AT);
    public static final TokenIElementType CLOSE_PAR = tokenIElementTypes.get(SiddhiQLLexer.CLOSE_PAR);
    public static final TokenIElementType OPEN_SQUARE_BRACKETS=tokenIElementTypes.get(SiddhiQLLexer
            .OPEN_SQUARE_BRACKETS);
    public static final TokenIElementType CLOSE_SQUARE_BRACKETS=tokenIElementTypes.get(SiddhiQLLexer
            .CLOSE_SQUARE_BRACKETS);
    public static final TokenIElementType SEMI_COLON=tokenIElementTypes.get(SiddhiQLLexer
            .SCOL);
    public static final TokenIElementType RETURN = tokenIElementTypes.get(SiddhiQLLexer.RETURN);
    public static final TokenIElementType SINGLE_LINE_COMMENT = tokenIElementTypes.get(SiddhiQLLexer.SINGLE_LINE_COMMENT);
    public static final TokenIElementType MULTILINE_COMMENT = tokenIElementTypes.get(
            SiddhiQLLexer.MULTILINE_COMMENT);
    public static final TokenIElementType AT_SYMBOL = tokenIElementTypes.get(SiddhiQLLexer.AT_SYMBOL);
}

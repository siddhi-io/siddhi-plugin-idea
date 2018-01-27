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

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
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
import org.wso2.siddhi.plugins.idea.grammar.SiddhiQLLexer;
import org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser;
import org.wso2.siddhi.plugins.idea.psi.AggregationDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.AggregationNameNode;
import org.wso2.siddhi.plugins.idea.psi.AliasNode;
import org.wso2.siddhi.plugins.idea.psi.AnnotationElementNode;
import org.wso2.siddhi.plugins.idea.psi.AnnotationNode;
import org.wso2.siddhi.plugins.idea.psi.AnonymousStreamNode;
import org.wso2.siddhi.plugins.idea.psi.AppAnnotationNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeNameNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeReferenceNode;
import org.wso2.siddhi.plugins.idea.psi.AttributeTypeNode;
import org.wso2.siddhi.plugins.idea.psi.BasicSourceNode;
import org.wso2.siddhi.plugins.idea.psi.BasicSourceStreamHandlerNode;
import org.wso2.siddhi.plugins.idea.psi.BasicSourceStreamHandlersNode;
import org.wso2.siddhi.plugins.idea.psi.ConstantValueNode;
import org.wso2.siddhi.plugins.idea.psi.DefinitionElementNode;
import org.wso2.siddhi.plugins.idea.psi.DefinitionElementWithExecutionElementNode;
import org.wso2.siddhi.plugins.idea.psi.DeleteFromTableNode;
import org.wso2.siddhi.plugins.idea.psi.EndPatternNode;
import org.wso2.siddhi.plugins.idea.psi.ExecutionElementNode;
import org.wso2.siddhi.plugins.idea.psi.ExpressionNode;
import org.wso2.siddhi.plugins.idea.psi.FilterNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionBodyNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionIdNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionNameNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionOperationNode;
import org.wso2.siddhi.plugins.idea.psi.GroupByNode;
import org.wso2.siddhi.plugins.idea.psi.HavingNode;
import org.wso2.siddhi.plugins.idea.psi.IdNode;
import org.wso2.siddhi.plugins.idea.psi.JoinNode;
import org.wso2.siddhi.plugins.idea.psi.JoinSourceNode;
import org.wso2.siddhi.plugins.idea.psi.JoinStreamNode;
import org.wso2.siddhi.plugins.idea.psi.LanguageNameNode;
import org.wso2.siddhi.plugins.idea.psi.LeftSourceNode;
import org.wso2.siddhi.plugins.idea.psi.LeftUnidirectionalJoinNode;
import org.wso2.siddhi.plugins.idea.psi.LimitNode;
import org.wso2.siddhi.plugins.idea.psi.MathOperationNode;
import org.wso2.siddhi.plugins.idea.psi.NameNode;
import org.wso2.siddhi.plugins.idea.psi.NullCheckNode;
import org.wso2.siddhi.plugins.idea.psi.OnWithExpressionNode;
import org.wso2.siddhi.plugins.idea.psi.OrderByNode;
import org.wso2.siddhi.plugins.idea.psi.OrderByReferenceNode;
import org.wso2.siddhi.plugins.idea.psi.OutputAttributeNode;
import org.wso2.siddhi.plugins.idea.psi.OutputEventTypeNode;
import org.wso2.siddhi.plugins.idea.psi.OutputRateNode;
import org.wso2.siddhi.plugins.idea.psi.ParseNode;
import org.wso2.siddhi.plugins.idea.psi.PartitionNode;
import org.wso2.siddhi.plugins.idea.psi.PartitionWithStreamNode;
import org.wso2.siddhi.plugins.idea.psi.PerNode;
import org.wso2.siddhi.plugins.idea.psi.PostWindowHandlerNode;
import org.wso2.siddhi.plugins.idea.psi.PreWindowHandlerNode;
import org.wso2.siddhi.plugins.idea.psi.QueryInputNode;
import org.wso2.siddhi.plugins.idea.psi.QueryNode;
import org.wso2.siddhi.plugins.idea.psi.QueryOutputNode;
import org.wso2.siddhi.plugins.idea.psi.QuerySectionNode;
import org.wso2.siddhi.plugins.idea.psi.RightSourceNode;
import org.wso2.siddhi.plugins.idea.psi.RightUnidirectionalOrNormalJoinNode;
import org.wso2.siddhi.plugins.idea.psi.SiddhiAppNode;
import org.wso2.siddhi.plugins.idea.psi.SiddhiFile;
import org.wso2.siddhi.plugins.idea.psi.SignedIntValueNode;
import org.wso2.siddhi.plugins.idea.psi.SourceNode;
import org.wso2.siddhi.plugins.idea.psi.StandardStreamNode;
import org.wso2.siddhi.plugins.idea.psi.StartPatternNode;
import org.wso2.siddhi.plugins.idea.psi.StreamDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.StreamFunctionNode;
import org.wso2.siddhi.plugins.idea.psi.StreamIdNode;
import org.wso2.siddhi.plugins.idea.psi.StringValueNode;
import org.wso2.siddhi.plugins.idea.psi.TableDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.TargetNode;
import org.wso2.siddhi.plugins.idea.psi.TimeValueNode;
import org.wso2.siddhi.plugins.idea.psi.TriggerDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.TriggerNameNode;
import org.wso2.siddhi.plugins.idea.psi.UpdateOrInsertIntoNode;
import org.wso2.siddhi.plugins.idea.psi.UpdateTableNode;
import org.wso2.siddhi.plugins.idea.psi.WindowDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.WindowNode;
import org.wso2.siddhi.plugins.idea.psi.WithinTimeRangeNode;

import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.AGGREGATE;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.AGGREGATION;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.ALL;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.AND;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.APP;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.AS;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.ASC;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.AT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.AT_SYMBOL;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.BEGIN;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.BOOL;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.BY;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.CURRENT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.DAYS;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.DEFINE;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.DELETE;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.DESC;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.DOUBLE;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.END;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.EVENTS;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.EVERY;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.EXPIRED;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.FALSE;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.FIRST;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.FLOAT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.FOR;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.FROM;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.FULL;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.FUNCTION;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.GROUP;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.HASH;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.HAVING;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.HOURS;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.IN;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.INNER;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.INSERT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.INT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.INTO;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.IS;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.JOIN;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.LAST;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.LEFT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.LIMIT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.LONG;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.MILLISECONDS;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.MINUTES;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.MONTHS;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.MULTILINE_COMMENT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.NOT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.NULL;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.OBJECT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.OF;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.ON;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.OR;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.ORDER;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.OUTER;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.OUTPUT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.PARTITION;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.PER;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RAW;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RETURN;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RIGHT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_aggregation_name;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_alias;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_annotation;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_annotation_element;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_anonymous_stream;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_app_annotation;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_attribute;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_attribute_name;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_attribute_reference;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_attribute_type;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_basic_source;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_basic_source_stream_handler;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_basic_source_stream_handlers;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_constant_value;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_definition_aggregation;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_definition_element;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_definition_element_with_execution_element;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_definition_function;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_definition_stream;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_definition_table;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_definition_trigger;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_definition_window;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_delete_from_table;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_end_pattern;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_execution_element;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_expression;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_filter;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_function_body;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_function_id;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_function_name;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_function_operation;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_group_by;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_having;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_id;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_join;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_join_source;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_join_stream;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_language_name;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_left_source;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_left_unidirectional_join;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_limit;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_math_operation;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_name;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_null_check;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_on_with_expression;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_order_by;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_order_by_reference;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_output_attribute;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_output_event_type;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_output_rate;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_parse;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_partition;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_partition_with_stream;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_per;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_post_window_handlers;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_pre_window_handlers;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_query;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_query_input;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_query_output;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_query_section;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_right_source;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_right_unidirectional_or_normal_join;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_siddhi_app;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_signed_int_value;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_source;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_standard_stream;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_start_pattern;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_stream_function;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_stream_id;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_string_value;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_target;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_time_value;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_trigger_name;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_update_or_insert_into;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_update_table;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_window;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.RULE_within_time_range;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.SCOL;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.SECONDS;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.SELECT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.SINGLE_LINE_COMMENT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.SNAPSHOT;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.SPACES;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.STREAM;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.STRING;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.STRING_LITERAL;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.TABLE;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.TRIGGER;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.TRUE;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.UNEXPECTED_CHAR;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.UNIDIRECTIONAL;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.UPDATE;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.WEEKS;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.WINDOW;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.WITH;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.WITHIN;
import static org.wso2.siddhi.plugins.idea.grammar.SiddhiQLParser.YEARS;

/**
 * Defines the implementation of the parser for the Siddhi language.
 */
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
            STRING, INT, LONG, FLOAT, DOUBLE, BOOL, OBJECT, AGGREGATION, AGGREGATE, PER, ORDER, LIMIT, ASC, DESC);

    public static final TokenSet COMMENTS =
            PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE, SINGLE_LINE_COMMENT, MULTILINE_COMMENT);
    public static final TokenSet WHITESPACE =
            PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE, SPACES);
    public static final TokenSet STRING_LITERALS =
            PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE, STRING_LITERAL);
    public static final TokenSet BAD_CHARACTER =
            PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE, UNEXPECTED_CHAR);
    public static final TokenSet SEMICOLON =
            PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE, SCOL);
    public static final TokenSet COLON =
            PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE, SiddhiQLParser.COL);
    public static final TokenSet COMMA =
            PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE, SiddhiQLParser.COMMA);
    public static final TokenSet SYMBOLS =
            PSIElementTypeFactory.createTokenSet(SiddhiLanguage.INSTANCE, AT_SYMBOL, HASH);

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

    /**
     * Tokens of those types are automatically skipped by PsiBuilder.
     */
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

    /**
     * Create the root of your PSI tree (a PsiFile).
     * <p>
     * From IntelliJ IDEA Architectural Overview:
     * "A PSI (Program Structure Interface) file is the root of a structure
     * representing the contents of a file as a hierarchy of elements
     * in a particular programming language."
     * <p>
     * PsiFile is to be distinguished from a FileASTNode, which is a parse
     * tree node that eventually becomes a PsiFile. From PsiFile, we can get
     * it back via: {@link PsiFile#getNode}.
     */
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new SiddhiFile(viewProvider);
    }

    /**
     * Convert from *NON-LEAF* parse node (AST they call it)
     * to PSI node. Leaves are created in the AST factory.
     * Rename re-factoring can cause this to be
     * called on a TokenIElementType since we want to rename ID nodes.
     * In that case, this method is called to create the root node
     * but with ID type. Kind of strange, but we can simply create a
     * ASTWrapperPsiElement to make everything work correctly.
     * <p>
     * RuleIElementType.  Ah! It's that ID is the root
     * IElementType requested to parse, which means that the root
     * node returned from parsetree->PSI conversion.  But, it
     * must be a CompositeElement! The adaptor calls
     * rootMarker.done(root) to finish off the PSI conversion.
     * See {@link ANTLRParserAdaptor#parse(IElementType, PsiBuilder)}
     * <p>
     * If you don't care to distinguish PSI nodes by type, it is
     * sufficient to create a {@link ANTLRPsiNode} around
     * the parse tree node
     */
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
            case RULE_query_section:
                return new QuerySectionNode(node);
            case RULE_execution_element:
                return new ExecutionElementNode(node);
            case RULE_annotation_element:
                return new AnnotationElementNode(node);
            case RULE_annotation:
                return new AnnotationNode(node);
            case RULE_app_annotation:
                return new AppAnnotationNode(node);
            case RULE_query_input:
                return new QueryInputNode(node);
            case RULE_query_output:
                return new QueryOutputNode(node);
            case RULE_query:
                return new QueryNode(node);
            case RULE_partition:
                return new PartitionNode(node);
            case RULE_definition_aggregation:
                return new AggregationDefinitionNode(node);
            case RULE_definition_table:
                return new TableDefinitionNode(node);
            case RULE_definition_stream:
                return new StreamDefinitionNode(node);
            case RULE_output_rate:
                return new OutputRateNode(node);
            case RULE_name:
                return new NameNode(node);
            case RULE_source:
                return new SourceNode(node);
            case RULE_id:
                return new IdNode(node);
            case RULE_definition_element_with_execution_element:
                return new DefinitionElementWithExecutionElementNode(node);
            case RULE_standard_stream:
                return new StandardStreamNode(node);
            case RULE_join_stream:
                return new JoinStreamNode(node);
            case RULE_join_source:
                return new JoinSourceNode(node);
            case RULE_join:
                return new JoinNode(node);
            case RULE_basic_source:
                return new BasicSourceNode(node);
            case RULE_target:
                return new TargetNode(node);
            case RULE_attribute_reference:
                return new AttributeReferenceNode(node);
            case RULE_output_attribute:
                return new OutputAttributeNode(node);
            case RULE_definition_element:
                return new DefinitionElementNode(node);
            case RULE_having:
                return new HavingNode(node);
            case RULE_expression:
                return new ExpressionNode(node);
            case RULE_math_operation:
                return new MathOperationNode(node);
            case RULE_constant_value:
                return new ConstantValueNode(node);
            case RULE_signed_int_value:
                return new SignedIntValueNode(node);
            case RULE_group_by:
                return new GroupByNode(node);
            case RULE_time_value:
                return new TimeValueNode(node);
            case RULE_update_or_insert_into:
                return new UpdateOrInsertIntoNode(node);
            case RULE_delete_from_table:
                return new DeleteFromTableNode(node);
            case RULE_update_table:
                return new UpdateTableNode(node);
            case RULE_basic_source_stream_handlers:
                return new BasicSourceStreamHandlersNode(node);
            case RULE_basic_source_stream_handler:
                return new BasicSourceStreamHandlerNode(node);
            case RULE_stream_function:
                return new StreamFunctionNode(node);
            case RULE_function_id:
                return new FunctionIdNode(node);
            case RULE_window:
                return new WindowNode(node);
            case RULE_filter:
                return new FilterNode(node);
            case RULE_per:
                return new PerNode(node);
            case RULE_null_check:
                return new NullCheckNode(node);
            case RULE_right_source:
                return new RightSourceNode(node);
            case RULE_on_with_expression:
                return new OnWithExpressionNode(node);
            case RULE_left_unidirectional_join:
                return new LeftUnidirectionalJoinNode(node);
            case RULE_right_unidirectional_or_normal_join:
                return new RightUnidirectionalOrNormalJoinNode(node);
            case RULE_pre_window_handlers:
                return new PreWindowHandlerNode(node);
            case RULE_post_window_handlers:
                return new PostWindowHandlerNode(node);
            case RULE_alias:
                return new AliasNode(node);
            case RULE_left_source:
                return new LeftSourceNode(node);
            case RULE_within_time_range:
                return new WithinTimeRangeNode(node);
            case RULE_start_pattern:
                return new StartPatternNode(node);
            case RULE_end_pattern:
                return new EndPatternNode(node);
            case RULE_anonymous_stream:
                return new AnonymousStreamNode(node);
            case RULE_attribute:
                return new AttributeNode(node);
            case RULE_partition_with_stream:
                return new PartitionWithStreamNode(node);
            case RULE_string_value:
                return new StringValueNode(node);
            case RULE_order_by:
                return new OrderByNode(node);
            case RULE_order_by_reference:
                return new OrderByReferenceNode(node);
            case RULE_limit:
                return new LimitNode(node);
            case RULE_aggregation_name:
                return new AggregationNameNode(node);
            default:
                return new ANTLRPsiNode(node);
        }
    }
}

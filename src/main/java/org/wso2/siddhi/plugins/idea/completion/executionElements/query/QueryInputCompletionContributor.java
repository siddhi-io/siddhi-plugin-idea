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
package org.wso2.siddhi.plugins.idea.completion.executionElements.query;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import javax.annotation.Nonnull;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.psi.AliasNode;
import org.wso2.siddhi.plugins.idea.psi.AnonymousStreamNode;
import org.wso2.siddhi.plugins.idea.psi.BasicSourceStreamHandlerNode;
import org.wso2.siddhi.plugins.idea.psi.EndPatternNode;
import org.wso2.siddhi.plugins.idea.psi.JoinStreamNode;
import org.wso2.siddhi.plugins.idea.psi.LeftSourceNode;
import org.wso2.siddhi.plugins.idea.psi.LeftUnidirectionalJoinNode;
import org.wso2.siddhi.plugins.idea.psi.OnWithExpressionNode;
import org.wso2.siddhi.plugins.idea.psi.OutputEventTypeNode;
import org.wso2.siddhi.plugins.idea.psi.PerNode;
import org.wso2.siddhi.plugins.idea.psi.PreWindowHandlerNode;
import org.wso2.siddhi.plugins.idea.psi.QueryInputNode;
import org.wso2.siddhi.plugins.idea.psi.RightSourceNode;
import org.wso2.siddhi.plugins.idea.psi.RightUnidirectionalOrNormalJoinNode;
import org.wso2.siddhi.plugins.idea.psi.SourceNode;
import org.wso2.siddhi.plugins.idea.psi.StandardStreamNode;
import org.wso2.siddhi.plugins.idea.psi.StartPatternNode;
import org.wso2.siddhi.plugins.idea.psi.StreamIdNode;
import org.wso2.siddhi.plugins.idea.psi.WindowNode;
import org.wso2.siddhi.plugins.idea.psi.WithinTimeRangeNode;

import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addAsKeywordWithDummyAlias;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addComma;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addEnterYourExpressionClause;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addEveryKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addFilterSuggestion;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addFromKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOutputEventTypeKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addPerKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addStreamFunctions;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addSuggestionsAfterQueryInput;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addSuggestionsRelatedToJoins;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addUnidirectionalKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addWindowTypesWithWindowKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addWithinKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.onWithExpressionKeyword;
import static org.wso2.siddhi.plugins.idea.completion.util.KeywordCompletionUtils.isExpression;

public class QueryInputCompletionContributor {

    public static void queryInputCompletion(@Nonnull CompletionResultSet result, PsiElement element,
                                            PsiElement prevVisibleSibling, IElementType
                                                    prevVisibleSiblingElementType, PsiElement
                                                    prevPreVisibleSibling) {
        //Suggestions related to QueryInputNode
        if (PsiTreeUtil.getParentOfType(element, QueryInputNode.class) != null) {
            if (PsiTreeUtil.getParentOfType(element, StreamIdNode.class) != null && prevVisibleSiblingElementType
                    == SiddhiTypes.FROM) {
                addEveryKeyword(result);
                addFromKeyword(result);//This is for the anonymous stream beginning
                return;
            }
            //Suggestions related to Standard stream
            if (PsiTreeUtil.getParentOfType(element, StandardStreamNode.class) != null) {
                standardStreamCompletion(result, element, prevVisibleSibling, prevVisibleSiblingElementType,
                        prevPreVisibleSibling);
                return;
            }
        }
        //suggestions related to a join stream node
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, JoinStreamNode.class) != null) {
            joinStreamCompletion(result, element, prevVisibleSibling, prevVisibleSiblingElementType,
                    prevPreVisibleSibling);
            return;
        }
        /*
        * for anonymous stream it is basically build with query elements without query_output rule.
        * So we only restrict keyword suggestions of query_output rule in queries.
        * see @SiddhiCompletionUtils.addBeginingOfQueryOutputKeywords() and
        * @SiddhiCompletionUtils.addSuggestionsAfterQueryInput() for more.
        * */
        //suggestions related to a anonymous stream node
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, AnonymousStreamNode.class) != null) {
            //Suggesting  output event types after RETURN keyword in the QueryOutputNode
            if (prevVisibleSiblingElementType == SiddhiTypes.RETURN) {
                addOutputEventTypeKeywords(result);
                return;
            }
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, OutputEventTypeNode.class) != null) {
                addSuggestionsAfterQueryInput(result);
                return;
            }
        }
    }

    private static void standardStreamCompletion(@Nonnull CompletionResultSet result, PsiElement element,
                                                 PsiElement prevVisibleSibling, IElementType
                                                         prevVisibleSiblingElementType, PsiElement
                                                         prevPreVisibleSibling) {
        //Suggestions related to Standard stream
        if (PsiTreeUtil.getParentOfType(element, StandardStreamNode.class) != null) {
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, SourceNode.class) != null) {
                //Suggesting join types after the initial source declaration of the query. we provide these
                // suggestions here because at the moment antlr doesn't know which stream user is going to enter
                addSuggestionsRelatedToJoins(result);
                addUnidirectionalKeyword(result);

                //suggestions after the source declaration of the standard stream
                addWindowTypesWithWindowKeyword(result);
                addStreamFunctions(result);
                addFilterSuggestion(result);
                addSuggestionsAfterQueryInput(result);
                return;
            }
            //suggestions after the Pre Window Handler of the standard stream
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, PreWindowHandlerNode.class) != null) {
                addWindowTypesWithWindowKeyword(result);
                addStreamFunctions(result);
                addFilterSuggestion(result);
                addSuggestionsAfterQueryInput(result);
                //Adding following code suggestions related to join stream, because at this point we cant' decide
                // that user is typing exactly a standard stream. It can be a join stream as well
                addAsKeywordWithDummyAlias(result);
                addSuggestionsRelatedToJoins(result);
                addUnidirectionalKeyword(result);
                return;
            }
            //suggestions after the Window declaration of the standard stream
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, WindowNode.class) != null) {
                addStreamFunctions(result);
                addFilterSuggestion(result);
                addSuggestionsAfterQueryInput(result);
                //Adding following code suggestions related to join stream, because at this point we cant' decide
                // that user is typing exactly a standard stream. It can be a join stream as well
                addAsKeywordWithDummyAlias(result);
                addSuggestionsRelatedToJoins(result);
                addUnidirectionalKeyword(result);
                return;
            }
        }
        //suggestions after a standard stream node(suggesting keywords in the beginning of a query_section rule)
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, StandardStreamNode.class) != null) {
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, SourceNode.class) != null ||
                    PsiTreeUtil.getParentOfType(prevVisibleSibling, BasicSourceStreamHandlerNode.class) != null ||
                    PsiTreeUtil.getParentOfType(prevVisibleSibling, WindowNode.class) != null) {
                addSuggestionsAfterQueryInput(result);
                return;
            }
        }
    }

    private static void joinStreamCompletion(@Nonnull CompletionResultSet result, PsiElement element,
                                                 PsiElement prevVisibleSibling, IElementType
                                                         prevVisibleSiblingElementType, PsiElement
                                                         prevPreVisibleSibling) {
        //suggestions related to a join stream node
        if (PsiTreeUtil.getParentOfType(prevVisibleSibling, JoinStreamNode.class) != null) {
            //suggestions after a Left source node. antlr identifies that the user is typing exactly a join
            // stream after typing a as with an alias in a source node. We have to provide suggestions after alias.
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, LeftSourceNode.class) != null
                    && PsiTreeUtil.getParentOfType(prevVisibleSibling, AliasNode.class) != null) {
                addSuggestionsRelatedToJoins(result);
                addUnidirectionalKeyword(result);
                return;
            }
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, RightUnidirectionalOrNormalJoinNode.class) != null) {
                //suggestions after a right source in Right Unidirectional Or Normal Join node
                if (PsiTreeUtil.getParentOfType(prevVisibleSibling, RightSourceNode.class) != null) {
                    if (PsiTreeUtil.getParentOfType(prevVisibleSibling, SourceNode.class) != null) {
                        addUnidirectionalKeyword(result);
                        addWindowTypesWithWindowKeyword(result);
                        addStreamFunctions(result);
                        addFilterSuggestion(result);
                        addAsKeywordWithDummyAlias(result);
                        addSuggestionsAfterQueryInput(result);
                        onWithExpressionKeyword(result);
                        addWithinKeyword(result);
                        return;
                    }
                    if (PsiTreeUtil.getParentOfType(prevVisibleSibling, BasicSourceStreamHandlerNode.class) !=
                            null) {
                        addUnidirectionalKeyword(result);
                        addWindowTypesWithWindowKeyword(result);
                        addStreamFunctions(result);
                        addFilterSuggestion(result);
                        addAsKeywordWithDummyAlias(result);
                        addSuggestionsAfterQueryInput(result);
                        onWithExpressionKeyword(result);
                        addWithinKeyword(result);
                        return;
                    }
                    if (PsiTreeUtil.getParentOfType(prevVisibleSibling, WindowNode.class) != null) {
                        addAsKeywordWithDummyAlias(result);
                        addSuggestionsAfterQueryInput(result);
                        addUnidirectionalKeyword(result);
                        onWithExpressionKeyword(result);
                        addWithinKeyword(result);
                        return;
                    }
                    IElementType prevPreVisibleSiblingElementType = ((LeafPsiElement) prevPreVisibleSibling)
                            .getElementType();
                    if (PsiTreeUtil.getParentOfType(prevVisibleSibling, AliasNode.class) != null
                            && prevPreVisibleSiblingElementType == SiddhiTypes.AS) {
                        addSuggestionsAfterQueryInput(result);
                        addUnidirectionalKeyword(result);
                        onWithExpressionKeyword(result);
                        addWithinKeyword(result);
                        return;
                    }

                }
                //TODO:configure giving attribute after select in join streams
                //TODO: on sensorStream.sensorId like suggestions in on_with_expression rule
                //TODO:https://wso2.github.io/siddhi/documentation/siddhi-4.0/#join-stream
                //suggestions after UNIDIRECTIONAL keyword in Right Unidirectional Or Normal Join node
                if (prevVisibleSiblingElementType == SiddhiTypes.UNIDIRECTIONAL) {
                    addSuggestionsAfterQueryInput(result);
                    onWithExpressionKeyword(result);
                    addWithinKeyword(result);
                    return;
                }
            }
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, LeftUnidirectionalJoinNode.class) != null) {
                if (prevVisibleSiblingElementType == SiddhiTypes.UNIDIRECTIONAL) {
                    addSuggestionsRelatedToJoins(result);
                    return;
                }
                //suggestions after the right source declaration of the Left Unidirectional Join Node
                if (PsiTreeUtil.getParentOfType(prevVisibleSibling, RightSourceNode.class) != null) {
                    if (PsiTreeUtil.getParentOfType(prevVisibleSibling, SourceNode.class) != null) {
                        addWindowTypesWithWindowKeyword(result);
                        addStreamFunctions(result);
                        addFilterSuggestion(result);
                        addAsKeywordWithDummyAlias(result);
                        addSuggestionsAfterQueryInput(result);
                        onWithExpressionKeyword(result);
                        addWithinKeyword(result);
                        return;
                    }
                    if (PsiTreeUtil.getParentOfType(prevVisibleSibling, BasicSourceStreamHandlerNode.class) !=
                            null) {
                        addWindowTypesWithWindowKeyword(result);
                        addStreamFunctions(result);
                        addFilterSuggestion(result);
                        addAsKeywordWithDummyAlias(result);
                        addSuggestionsAfterQueryInput(result);
                        onWithExpressionKeyword(result);
                        addWithinKeyword(result);
                        return;
                    }
                    if (PsiTreeUtil.getParentOfType(prevVisibleSibling, WindowNode.class) != null) {
                        addAsKeywordWithDummyAlias(result);
                        addSuggestionsAfterQueryInput(result);
                        onWithExpressionKeyword(result);
                        addWithinKeyword(result);
                        return;
                    }
                    IElementType prevPreVisibleSiblingElementType = ((LeafPsiElement) prevPreVisibleSibling)
                            .getElementType();
                    if (PsiTreeUtil.getParentOfType(prevVisibleSibling, AliasNode.class) != null
                            && prevPreVisibleSiblingElementType == SiddhiTypes.AS) {
                        addSuggestionsAfterQueryInput(result);
                        addWithinKeyword(result);
                        onWithExpressionKeyword(result);
                        return;
                    }
                }
            }
            //suggestions related to within time range node
            if (PsiTreeUtil.getParentOfType(prevVisibleSibling, WithinTimeRangeNode.class) != null) {
                if (prevVisibleSiblingElementType == SiddhiTypes.WITHIN) {
                    addEnterYourExpressionClause(result);
                    return;
                }
                if (PsiTreeUtil.getParentOfType(prevVisibleSibling, StartPatternNode.class) != null) {
                    addComma(result);
                    addPerKeyword(result);
                    return;
                }
                if (PsiTreeUtil.getParentOfType(prevPreVisibleSibling, StartPatternNode.class) != null
                        && prevVisibleSiblingElementType == SiddhiTypes.COMMA) {
                    addEnterYourExpressionClause(result);
                    return;
                }
                if (PsiTreeUtil.getParentOfType(prevVisibleSibling, EndPatternNode.class) != null) {
                    addPerKeyword(result);
                    return;
                }
            }
            //suggestions after per keyword in a join stream
            if (prevVisibleSiblingElementType == SiddhiTypes.PER && isExpression(prevPreVisibleSibling)) {
                addEnterYourExpressionClause(result);
                return;
            }
            //suggestions after 'per and expression clause' in a join stream
            IElementType prevPreVisibleSiblingElementType = ((LeafPsiElement) prevPreVisibleSibling)
                    .getElementType();
            if (prevPreVisibleSiblingElementType == SiddhiTypes.PER && isExpression(prevVisibleSibling)) {
                addSuggestionsAfterQueryInput(result);
                return;
            }
            //suggestions after a on with expression node
            if ((PsiTreeUtil.getParentOfType(prevVisibleSibling, OnWithExpressionNode.class) != null) &&
                    isExpression(prevVisibleSibling)) {
                addSuggestionsAfterQueryInput(result);
                addWithinKeyword(result);
                return;
            }
            //suggestions after 'per and an expression' clause
            if ((PsiTreeUtil.getParentOfType(prevVisibleSibling, PerNode.class) != null) &&
                    isExpression(prevVisibleSibling)) {
                addSuggestionsAfterQueryInput(result);
                return;
            }
        }
    }
}

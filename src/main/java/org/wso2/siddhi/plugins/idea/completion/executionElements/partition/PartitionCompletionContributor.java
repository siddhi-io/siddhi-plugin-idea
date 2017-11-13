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
package org.wso2.siddhi.plugins.idea.completion.executionElements.partition;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.antlr.jetbrains.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.psi.DeleteFromTableNode;
import org.wso2.siddhi.plugins.idea.psi.ExpressionNode;
import org.wso2.siddhi.plugins.idea.psi.MathOperationNode;
import org.wso2.siddhi.plugins.idea.psi.OutputEventTypeNode;
import org.wso2.siddhi.plugins.idea.psi.PartitionNode;
import org.wso2.siddhi.plugins.idea.psi.StreamIdNode;
import org.wso2.siddhi.plugins.idea.psi.StringValueNode;
import org.wso2.siddhi.plugins.idea.psi.TargetNode;
import org.wso2.siddhi.plugins.idea.psi.UpdateOrInsertIntoNode;
import org.wso2.siddhi.plugins.idea.psi.UpdateTableNode;

import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addAsKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addBeginKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addEndKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addFromKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOfKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOrKeyword;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addOutputEventTypeKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addSemicolon;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addWithKeywordAndParentheses;
import static org.wso2.siddhi.plugins.idea.completion.util.KeywordCompletionUtils.getPreviousVisibleSiblingSkippingComments;
import static org.wso2.siddhi.plugins.idea.completion.util.KeywordCompletionUtils.isExpression;
import static org.wso2.siddhi.plugins.idea.completion.util.KeywordCompletionUtils.isMathOperation;

public class PartitionCompletionContributor {

    public static void partitionCompletion(@NotNull CompletionResultSet result, PsiElement element,
                                           PsiElement prevVisibleSibling, IElementType
                                                   prevVisibleSiblingElementType, PsiElement
                                                   prevPrevVisibleSibling){
        //keyword completion related to partitions
        //suggestions after 'partition' keyword
        if (PsiTreeUtil.getParentOfType(element, PartitionNode.class) != null) {
            if(prevVisibleSiblingElementType == SiddhiTypes.PARTITION) {
                addWithKeywordAndParentheses(result);
                return;
            }
            //suggestions after open parentheses'(' of partition and
            //suggestions after "partition_with_stream , expression" clause
            if (isMathOperation(prevVisibleSibling)) {
                IElementType prevSiblingOfMathOperationNodeElementType =
                        getElementTypeOfPreviousVisibleSiblingOfGivenNode(prevVisibleSibling, MathOperationNode.class);
                if(prevSiblingOfMathOperationNodeElementType == SiddhiTypes.OPEN_PAR
                        || prevSiblingOfMathOperationNodeElementType == SiddhiTypes.COMMA) {
                    addAsKeyword(result);
                    addOfKeyword(result);
                    return;
                }
            }
            //suggestions after 'AS string_value'
            IElementType prevPreVisibleSiblingElementType = ((LeafPsiElement) prevPrevVisibleSibling).getElementType();
            if(PsiTreeUtil.getParentOfType(prevVisibleSibling, StringValueNode.class) != null
                    && prevPreVisibleSiblingElementType==SiddhiTypes.AS){
                addOrKeyword(result);
                addOfKeyword(result);
                return;
            }
            //suggestions after 'OR expression' clause
            if(isExpression(prevVisibleSibling)){
                IElementType prevSiblingOfExpressionNodeElementType =
                        getElementTypeOfPreviousVisibleSiblingOfGivenNode(prevVisibleSibling, ExpressionNode.class);
                if(prevSiblingOfExpressionNodeElementType == SiddhiTypes.OR) {
                    addAsKeyword(result);
                    return;
                }
            }
            if(prevVisibleSiblingElementType==SiddhiTypes.CLOSE_PAR
                    && PsiTreeUtil.getParentOfType(prevPrevVisibleSibling, StreamIdNode.class) != null){
                addBeginKeyword(result);
                return;
            }

            if(prevVisibleSiblingElementType==SiddhiTypes.BEGIN
                    && prevPreVisibleSiblingElementType==SiddhiTypes.CLOSE_PAR){
                PsiElement prevPrevPrevSibling=getPreviousVisibleSiblingSkippingComments(prevPrevVisibleSibling);
                if(PsiTreeUtil.getParentOfType(prevPrevPrevSibling, StreamIdNode.class) != null){
                    addFromKeyword(result);
                    return;
                }
            }
            //suggesting end keyword after a query(which is inside the partition)
            if(isEndOfAQueryOutput(prevVisibleSibling,prevPrevVisibleSibling,result)){
                addEndKeyword(result);
                addSemicolon(result);
                return;
            }
            PsiElement prevPrevPrevVisibleSibling = getPreviousVisibleSiblingSkippingComments(prevPrevVisibleSibling);
            if(prevVisibleSiblingElementType==SiddhiTypes.SCOL
                    && isEndOfAQueryOutput(prevPrevVisibleSibling,prevPrevPrevVisibleSibling,result)){
                addFromKeyword(result);
                addEndKeyword(result);
                return;
            }
        }
    }

    /**
     * Checks whether the elements are in the end of a Query Output rule
     * */
    public static boolean isEndOfAQueryOutput(PsiElement prevSibling, PsiElement prevPrevSibling, @Nullable
            CompletionResultSet result){
        IElementType prevVisibleSiblingElementType = ((LeafPsiElement) prevSibling).getElementType();
        IElementType prevPrevVisibleSiblingElementType = ((LeafPsiElement) prevPrevSibling).getElementType();
        if(PsiTreeUtil.getParentOfType(prevSibling,TargetNode.class) != null
                && prevPrevVisibleSiblingElementType==SiddhiTypes.INTO){
            return true;
        }
        if(isExpression(prevSibling)){
            IElementType prevSiblingOfExpressionNodeElementType =
                    getElementTypeOfPreviousVisibleSiblingOfGivenNode(prevSibling, ExpressionNode.class);
            if(prevSiblingOfExpressionNodeElementType==SiddhiTypes.ON
                    && (PsiTreeUtil.getParentOfType(prevSibling,DeleteFromTableNode.class) != null
                    || PsiTreeUtil.getParentOfType(prevSibling,UpdateOrInsertIntoNode.class) != null
                    || PsiTreeUtil.getParentOfType(prevSibling,UpdateTableNode.class) != null)) {
                return true;
            }
        }
        if(prevVisibleSiblingElementType==SiddhiTypes.RETURN){
            if(result!=null) addOutputEventTypeKeywords(result);
            return true;
        }
        if(PsiTreeUtil.getParentOfType(prevSibling,OutputEventTypeNode.class) != null){
            IElementType prevSiblingOfOutputEventTypeNodeElementType =
                    getElementTypeOfPreviousVisibleSiblingOfGivenNode(prevSibling, OutputEventTypeNode.class);
            if(prevSiblingOfOutputEventTypeNodeElementType == SiddhiTypes.RETURN) {
                return true;
            }
        }
        return false;
    }

    public static IElementType getElementTypeOfPreviousVisibleSiblingOfGivenNode(PsiElement element, @NotNull Class aClass){
        PsiElement prevSiblingOfGivenNode=getPrevSiblingOfGivenType(element,aClass);
        IElementType prevSiblingOfExpressionNodeElementType = null;
        if (prevSiblingOfGivenNode != null) {
            prevSiblingOfExpressionNodeElementType = ((LeafPsiElement) prevSiblingOfGivenNode)
                    .getElementType();
        }
        return prevSiblingOfExpressionNodeElementType;
    }

    public static PsiElement getPrevSiblingOfGivenType(PsiElement element, @NotNull Class aClass){
        PsiElement GivenNodeElement = PsiTreeUtil.getParentOfType(element, aClass);
        PsiElement prevSiblingOfGivenNode = null;
        if (GivenNodeElement != null) {
            prevSiblingOfGivenNode = getPreviousVisibleSiblingSkippingComments(GivenNodeElement);
        }
        return prevSiblingOfGivenNode;
    }
}

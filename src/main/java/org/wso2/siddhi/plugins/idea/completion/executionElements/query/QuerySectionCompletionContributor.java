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
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import javax.annotation.Nonnull;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.psi.OutputAttributeNode;
import org.wso2.siddhi.plugins.idea.psi.QuerySectionNode;

import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addBeginingOfQueryOutputKeywords;
import static org.wso2.siddhi.plugins.idea.completion.SiddhiCompletionUtils.addByKeyword;

public class QuerySectionCompletionContributor {

    public static void querySectionCompletion(@Nonnull CompletionResultSet result, PsiElement element,
                                              PsiElement prevVisibleSibling, IElementType
                                                      prevVisibleSiblingElementType){
        //Suggestions related to QuerySectionNode
        if (PsiTreeUtil.getParentOfType(element, QuerySectionNode.class) != null) {
            //This provides suggestions after ->(SELECT ('*'| (output_attribute (',' output_attribute)* ))) in
            // query_section1 rule
            if ((prevVisibleSiblingElementType != SiddhiTypes.AS && PsiTreeUtil
                    .getParentOfType(prevVisibleSibling, OutputAttributeNode.class) != null) ||
                    prevVisibleSiblingElementType == SiddhiTypes.STAR) {
                addBeginingOfQueryOutputKeywords(result);
                return;
            }
            //suggesting 'by' keyword after 'group' keyword
            if (prevVisibleSiblingElementType == SiddhiTypes.GROUP) {
                addByKeyword(result);
            }
        }
    }
}

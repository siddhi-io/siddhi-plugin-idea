package org.wso2.plugins.idea.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.wso2.plugins.idea.SiddhiLanguage;
import org.wso2.plugins.idea.SiddhiTypes;

public class SiddhiCompletionContributor extends CompletionContributor {

    public SiddhiCompletionContributor() {
//        extend(CompletionType.SMART,
//                PlatformPatterns.psiElement(SiddhiTypes.DEFINITION_STREAM).withLanguage(SiddhiLanguage.INSTANCE),
//                new CompletionProvider<CompletionParameters>() {
//                    public void addCompletions(@NotNull CompletionParameters parameters,
//                                               ProcessingContext context,
//                                               @NotNull CompletionResultSet resultSet) {
//                        resultSet.addElement(LookupElementBuilder.create("DEFINE"));
//                    }
//                }
//        );
    }
}
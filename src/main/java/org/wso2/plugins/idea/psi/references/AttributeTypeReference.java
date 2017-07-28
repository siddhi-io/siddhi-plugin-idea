//package org.wso2.plugins.idea.psi.references;
//
//import com.intellij.codeInsight.lookup.LookupElement;
//import org.jetbrains.annotations.NotNull;
//import org.wso2.plugins.idea.completion.SiddhiCompletionUtils;
//import org.wso2.plugins.idea.psi.IdentifierPSINode;
//
//import java.util.LinkedList;
//import java.util.List;
//
//
//public class AttributeTypeReference extends SiddhiElementReference {
//
//    public AttributeTypeReference(@NotNull IdentifierPSINode element) {
//        super(element);
//    }
//
//    @NotNull
//    @Override
//    public Object[] getVariants() {
//        List<LookupElement> results = new LinkedList<>();
//        SiddhiCompletionUtils.createCommonKeywords();
//        return results.toArray();
//
//    }
//}

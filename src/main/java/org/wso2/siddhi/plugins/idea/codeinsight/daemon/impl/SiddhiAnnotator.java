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

package org.wso2.siddhi.plugins.idea.codeinsight.daemon.impl;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.highlighter.SiddhiSyntaxHighlightingColors;
import org.wso2.siddhi.plugins.idea.psi.StreamIdNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

/**
 * Add annotations to files in siddhi language.
 */
public class SiddhiAnnotator implements Annotator {

    private static final String VALID_ESCAPE_CHARACTERS = "\\\\[btnfr\"'\\\\]|\\\\u[0-f]{4}|\\\\[0-3][0-7]{2}" +
            "|\\\\[0-7]{1,2}";
    private static final Pattern VALID_ESCAPE_CHAR_PATTERN = Pattern.compile(VALID_ESCAPE_CHARACTERS);
    private static final String INVALID_ESCAPE_CHARACTERS = "((\\\\\\\\)+|(\\\\([^btnfru\"'\\\\0-7]" +
            "|(u[0-f]{0,3}[^0-f]))))|(\\\\(?!.))";
    private static final Pattern INVALID_ESCAPE_CHAR_PATTERN = Pattern.compile(INVALID_ESCAPE_CHARACTERS);

    @Override
    public void annotate(@Nonnull PsiElement element, @Nonnull AnnotationHolder holder) {
        if (element instanceof StreamIdNode) {
            annotateStreamIdNodes(element, holder);
        } else if (element instanceof LeafPsiElement) {
            annotateLeafPsiElementNodes(element, holder);
        }
    }

    private void annotateStreamIdNodes(@Nonnull PsiElement element, @Nonnull AnnotationHolder holder) {
        Annotation annotation = holder.createInfoAnnotation(element.getTextRange(), null);
        annotation.setTextAttributes(SiddhiSyntaxHighlightingColors.STREAM_ID);
    }

    private void annotateLeafPsiElementNodes(@Nonnull PsiElement element, @Nonnull AnnotationHolder holder) {
        IElementType elementType = ((LeafPsiElement) element).getElementType();
        if (elementType == SiddhiTypes.STRING_LITERAL) {
            // In here, we annotate valid escape characters.
            String text = element.getText();
            Matcher matcher = VALID_ESCAPE_CHAR_PATTERN.matcher(text);
            // Get the start offset of the element.
            int startOffset = ((LeafPsiElement) element).getStartOffset();
            // Iterate through each match.
            while (matcher.find()) {
                // Get the matching group.
                String group = matcher.group(0);
                // Calculate the start and end offsets and create the range.
                TextRange range = new TextRange(startOffset + matcher.start(),
                        startOffset + matcher.start() + group.length());
                // Create the annotation.
                Annotation annotation = holder.createInfoAnnotation(range, null);
                annotation.setTextAttributes(SiddhiSyntaxHighlightingColors.VALID_STRING_ESCAPE);
            }

            // Annotate invalid escape characters.
            matcher = INVALID_ESCAPE_CHAR_PATTERN.matcher(text);
            // Get the start offset of the element.
            startOffset = ((LeafPsiElement) element).getStartOffset();
            // Iterate through each match.
            while (matcher.find()) {
                // Get the matching group.
                String group = matcher.group(3);
                if (group != null) {
                    // Calculate the start and end offsets and create the range.
                    TextRange range = new TextRange(startOffset + matcher.start(3),
                            startOffset + matcher.start(3) + group.length());
                    // Create the annotation.
                    Annotation annotation = holder.createInfoAnnotation(range, "Invalid string escape");
                    annotation.setTextAttributes(SiddhiSyntaxHighlightingColors.INVALID_STRING_ESCAPE);
                }
            }
        }
    }
}

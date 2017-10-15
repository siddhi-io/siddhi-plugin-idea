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

package org.wso2.siddhi.plugins.idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.LeafElement;
import org.wso2.siddhi.plugins.idea.util.SiddhiStringLiteralEscaper;
import org.jetbrains.annotations.NotNull;
import org.wso2.siddhi.plugins.idea.psi.QuotedLiteralString;


public class SiddhiPsiImplUtil {



    private SiddhiPsiImplUtil() {

    }

    @NotNull
    public static QuotedLiteralString updateText(@NotNull QuotedLiteralString quotedLiteralString,
                                                 @NotNull String text) {
        if (text.length() > 2) {
            StringBuilder outChars = new StringBuilder();
            SiddhiStringLiteralEscaper.escapeString(text.substring(1, text.length() - 1), outChars);
            outChars.insert(0, '"');
            outChars.append('"');
            text = outChars.toString();
        }

        ASTNode valueNode = quotedLiteralString.getNode();
        assert valueNode instanceof LeafElement;

        ((LeafElement) valueNode).replaceWithText(text);
        return quotedLiteralString;
    }

}

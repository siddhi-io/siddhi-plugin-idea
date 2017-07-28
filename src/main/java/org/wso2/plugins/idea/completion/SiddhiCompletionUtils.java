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

package org.wso2.plugins.idea.completion;

import com.intellij.codeInsight.completion.AddSpaceInsertHandler;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedList;
import java.util.List;

public class SiddhiCompletionUtils {

    private static final int VARIABLE_PRIORITY = 20;
    private static final int VALUE_TYPES_PRIORITY = VARIABLE_PRIORITY - 1;
    private static final int KEYWORDS_PRIORITY = VALUE_TYPES_PRIORITY - 2;


    //keywords
    private static final LookupElementBuilder STREAM;
    private static final LookupElementBuilder DEFINE;
    private static final LookupElementBuilder TABLE;
    private static final LookupElementBuilder FROM;
    private static final LookupElementBuilder PARTITION;
    private static final LookupElementBuilder WINDOW;
    private static final LookupElementBuilder SELECT;
    private static final LookupElementBuilder GROUP;
    private static final LookupElementBuilder BY;
    private static final LookupElementBuilder HAVING;
    private static final LookupElementBuilder INSERT;
    private static final LookupElementBuilder DELETE;
    private static final LookupElementBuilder UPDATE;
    private static final LookupElementBuilder RETURN;
    private static final LookupElementBuilder EVENTS;
    private static final LookupElementBuilder INTO;
    private static final LookupElementBuilder OUTPUT;
    private static final LookupElementBuilder EXPIRED;
    private static final LookupElementBuilder CURRENT;
    private static final LookupElementBuilder SNAPSHOT;
    private static final LookupElementBuilder FOR;
    private static final LookupElementBuilder RAW;
    private static final LookupElementBuilder OF;
    private static final LookupElementBuilder AS;
    private static final LookupElementBuilder OR;
    private static final LookupElementBuilder AND;
    private static final LookupElementBuilder ON;
    private static final LookupElementBuilder IN;
    private static final LookupElementBuilder IS;
    private static final LookupElementBuilder NOT;
    private static final LookupElementBuilder WITHIN;
    private static final LookupElementBuilder WITH;
    private static final LookupElementBuilder BEGIN;
    private static final LookupElementBuilder END;
    private static final LookupElementBuilder NULL;
    private static final LookupElementBuilder EVERY;
    private static final LookupElementBuilder LAST;
    private static final LookupElementBuilder ALL;
    private static final LookupElementBuilder FIRST;
    private static final LookupElementBuilder JOIN;
    private static final LookupElementBuilder INNER;
    private static final LookupElementBuilder OUTER;
    private static final LookupElementBuilder RIGHT;
    private static final LookupElementBuilder LEFT;
    private static final LookupElementBuilder FULL;
    private static final LookupElementBuilder UNIDIRECTIONAL;
    private static final LookupElementBuilder YEARS;
    private static final LookupElementBuilder MONTHS;
    private static final LookupElementBuilder WEEKS;
    private static final LookupElementBuilder DAYS;
    private static final LookupElementBuilder HOURS;
    private static final LookupElementBuilder MINUTES;
    private static final LookupElementBuilder SECONDS;
    private static final LookupElementBuilder MILLISECONDS;
    private static final LookupElementBuilder FALSE;
    private static final LookupElementBuilder TRUE;
    private static final LookupElementBuilder STRING;
    private static final LookupElementBuilder INT;
    private static final LookupElementBuilder LONG;
    private static final LookupElementBuilder FLOAT;
    private static final LookupElementBuilder DOUBLE;
    private static final LookupElementBuilder BOOL;
    private static final LookupElementBuilder OBJECT;
    private static final LookupElementBuilder AGGREGATION;
    private static final LookupElementBuilder AGGREGATE;
    private static final LookupElementBuilder PER;


    static {

        STREAM= createKeywordLookupElement("true");
        DEFINE= createKeywordLookupElement("define");
        TABLE= createKeywordLookupElement("table");
        FROM= createKeywordLookupElement("from");
        PARTITION= createKeywordLookupElement("partition");
        WINDOW= createKeywordLookupElement("window");
        SELECT= createKeywordLookupElement("select");
        GROUP= createKeywordLookupElement("group");
        BY= createKeywordLookupElement("by");
        HAVING= createKeywordLookupElement("having");
        INSERT= createKeywordLookupElement("insert");
        DELETE= createKeywordLookupElement("delete");
        UPDATE= createKeywordLookupElement("update");
        RETURN= createKeywordLookupElement("return");
        EVENTS= createKeywordLookupElement("events");
        INTO= createKeywordLookupElement("into");
        OUTPUT= createKeywordLookupElement("output");
        EXPIRED= createKeywordLookupElement("expired");
        CURRENT= createKeywordLookupElement("current");
        SNAPSHOT= createKeywordLookupElement("snapshot");
        FOR= createKeywordLookupElement("for");
        RAW= createKeywordLookupElement("raw");
        OF= createKeywordLookupElement("of");
        AS= createKeywordLookupElement("as");
        OR= createKeywordLookupElement("or");
        AND= createKeywordLookupElement("and");
        ON= createKeywordLookupElement("on");
        IN= createKeywordLookupElement("in");
        IS= createKeywordLookupElement("is");
        NOT= createKeywordLookupElement("not");
        WITHIN= createKeywordLookupElement("within");
        WITH= createKeywordLookupElement("with");
        BEGIN= createKeywordLookupElement("begin");
        END= createKeywordLookupElement("end");
        NULL= createKeywordLookupElement("null");
        EVERY= createKeywordLookupElement("every");
        LAST= createKeywordLookupElement("last");
        ALL= createKeywordLookupElement("all");
        FIRST= createKeywordLookupElement("first");
        JOIN= createKeywordLookupElement("join");
        INNER= createKeywordLookupElement("inner");
        OUTER= createKeywordLookupElement("outer");
        RIGHT= createKeywordLookupElement("right");
        LEFT= createKeywordLookupElement("left");
        FULL= createKeywordLookupElement("full");
        UNIDIRECTIONAL= createKeywordLookupElement("unidirectional");
        YEARS= createKeywordLookupElement("years");
        MONTHS= createKeywordLookupElement("months");
        WEEKS= createKeywordLookupElement("weeks");
        DAYS= createKeywordLookupElement("days");
        HOURS= createKeywordLookupElement("hours");
        MINUTES= createKeywordLookupElement("minutes");
        SECONDS= createKeywordLookupElement("seconds");
        MILLISECONDS= createKeywordLookupElement("milliseconds");
        FALSE= createKeywordLookupElement("false");
        TRUE= createKeywordLookupElement("true");
        OBJECT= createKeywordLookupElement("object");
        AGGREGATION= createKeywordLookupElement("aggregation");
        AGGREGATE= createKeywordLookupElement("aggregate");
        PER= createKeywordLookupElement("per");


        STRING = createTypeLookupElement("string", AddSpaceInsertHandler.INSTANCE);
        INT = createTypeLookupElement("int", AddSpaceInsertHandler.INSTANCE);
        LONG = createTypeLookupElement("long", AddSpaceInsertHandler.INSTANCE);
        FLOAT = createTypeLookupElement("float", AddSpaceInsertHandler.INSTANCE);
        DOUBLE = createTypeLookupElement("double", AddSpaceInsertHandler.INSTANCE);
        BOOL = createTypeLookupElement("bool", AddSpaceInsertHandler.INSTANCE);
    }

    private SiddhiCompletionUtils() {

    }

    /**
     * Creates a lookup element.
     *
     * @param name          name of the lookup
     * @param insertHandler insert handler of the lookup
     * @return {@link LookupElementBuilder} which will be used to create the lookup element.
     */
    @NotNull
    private static LookupElementBuilder createLookupElement(@NotNull String name,
                                                            @Nullable InsertHandler<LookupElement> insertHandler) {
        return LookupElementBuilder.create(name).withBoldness(true).withInsertHandler(insertHandler);
    }

    /**
     * Creates a keyword lookup element.
     *
     * @param name name of the lookup
     * @return {@link LookupElementBuilder} which will be used to create the lookup element.
     */
    @NotNull
    private static LookupElementBuilder createKeywordLookupElement(@NotNull String name) {
        return createLookupElement(name, createTemplateBasedInsertHandler("siddhi_lang_" + name));
    }


    @NotNull
    private static InsertHandler<LookupElement> createTemplateBasedInsertHandler(@NotNull String templateId) {
        return (context, item) -> {
            Template template = TemplateSettings.getInstance().getTemplateById(templateId);
            Editor editor = context.getEditor();
            if (template != null) {
                editor.getDocument().deleteString(context.getStartOffset(), context.getTailOffset());
                TemplateManager.getInstance(context.getProject()).startTemplate(editor, template);
            } else {
                int currentOffset = editor.getCaretModel().getOffset();
                CharSequence documentText = editor.getDocument().getImmutableCharSequence();
                if (documentText.length() <= currentOffset || documentText.charAt(currentOffset) != ' ') {
                    EditorModificationUtil.insertStringAtCaret(editor, " ");
                } else {
                    EditorModificationUtil.moveCaretRelatively(editor, 1);
                }
            }
        };
    }

    /**
     * Creates a <b>Type</b> lookup element.
     *
     * @param name          name of the lookup
     * @param insertHandler insert handler of the lookup
     * @return {@link LookupElementBuilder} which will be used to create the lookup element.
     */
    @NotNull
    private static LookupElementBuilder createTypeLookupElement(@NotNull String name,
                                                                @Nullable InsertHandler<LookupElement> insertHandler) {
        return createLookupElement(name, insertHandler).withTypeText("Type");
    }


    /**
     * Adds value types as lookups.
     *
     * @param resultSet result list which is used to add lookups
     */
    static void addValueTypesAsLookups(@NotNull CompletionResultSet resultSet) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(LONG, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(INT, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(FLOAT, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(STRING, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(BOOL, VALUE_TYPES_PRIORITY));
        resultSet.addElement(PrioritizedLookupElement.withPriority(DOUBLE, VALUE_TYPES_PRIORITY));
    }

    /**
     * Adds a keyword as a lookup.
     *
     * @param resultSet     result list which is used to add lookups
     * @param lookupElement lookup element which needs to be added to the result list
     */
    private static void addKeywordAsLookup(@NotNull CompletionResultSet resultSet, @NotNull LookupElement
            lookupElement) {
        resultSet.addElement(PrioritizedLookupElement.withPriority(lookupElement, KEYWORDS_PRIORITY));
    }

    private static LookupElement createKeywordAsLookup(@NotNull LookupElement lookupElement) {
        return PrioritizedLookupElement.withPriority(lookupElement, KEYWORDS_PRIORITY);
    }

    /**
     * Adds common keywords like if, else as lookup elements.
     *
     * @param resultSet result list which is used to add lookups
     */
    static void addCommonKeywords(@NotNull CompletionResultSet resultSet) {

        addKeywordAsLookup(resultSet, STREAM);
        addKeywordAsLookup(resultSet, DEFINE);
        addKeywordAsLookup(resultSet, TABLE);
        addKeywordAsLookup(resultSet, FROM);
        addKeywordAsLookup(resultSet, PARTITION);
        addKeywordAsLookup(resultSet, WINDOW);
        addKeywordAsLookup(resultSet, SELECT);
        addKeywordAsLookup(resultSet, GROUP);
        addKeywordAsLookup(resultSet, BY);
        addKeywordAsLookup(resultSet, HAVING);
        addKeywordAsLookup(resultSet,INSERT);
        addKeywordAsLookup(resultSet, DELETE);
        addKeywordAsLookup(resultSet, UPDATE);
        addKeywordAsLookup(resultSet, RETURN);
        addKeywordAsLookup(resultSet, EVENTS);
        addKeywordAsLookup(resultSet, INTO);
        addKeywordAsLookup(resultSet, OUTPUT);
        addKeywordAsLookup(resultSet, EXPIRED);
        addKeywordAsLookup(resultSet, CURRENT);
        addKeywordAsLookup(resultSet, SNAPSHOT);
        addKeywordAsLookup(resultSet, FOR);
        addKeywordAsLookup(resultSet, RAW);
        addKeywordAsLookup(resultSet, OF);
        addKeywordAsLookup(resultSet, AS);
        addKeywordAsLookup(resultSet, OR);
        addKeywordAsLookup(resultSet, AND);
        addKeywordAsLookup(resultSet, ON);
        addKeywordAsLookup(resultSet, IN);
        addKeywordAsLookup(resultSet, IS);
        addKeywordAsLookup(resultSet, NOT);
        addKeywordAsLookup(resultSet, WITHIN);
        addKeywordAsLookup(resultSet, WITH);
        addKeywordAsLookup(resultSet, BEGIN);
        addKeywordAsLookup(resultSet, END);
        addKeywordAsLookup(resultSet, NULL);
        addKeywordAsLookup(resultSet, EVERY);
        addKeywordAsLookup(resultSet, LAST);
        addKeywordAsLookup(resultSet, ALL);
        addKeywordAsLookup(resultSet, FIRST);
        addKeywordAsLookup(resultSet, JOIN);
        addKeywordAsLookup(resultSet, INNER);
        addKeywordAsLookup(resultSet, OUTER);
        addKeywordAsLookup(resultSet, RIGHT);
        addKeywordAsLookup(resultSet, LEFT);
        addKeywordAsLookup(resultSet, FULL);
        addKeywordAsLookup(resultSet, UNIDIRECTIONAL);
        addKeywordAsLookup(resultSet, YEARS);
        addKeywordAsLookup(resultSet, MONTHS);
        addKeywordAsLookup(resultSet, WEEKS);
        addKeywordAsLookup(resultSet, DAYS);
        addKeywordAsLookup(resultSet, HOURS);
        addKeywordAsLookup(resultSet, MINUTES);
        addKeywordAsLookup(resultSet,SECONDS);
        addKeywordAsLookup(resultSet, MILLISECONDS);
        addKeywordAsLookup(resultSet, OBJECT);
        addKeywordAsLookup(resultSet, AGGREGATION);
        addKeywordAsLookup(resultSet, AGGREGATE);
        addKeywordAsLookup(resultSet, PER);

    }

    @NotNull
    public static List<LookupElement> createCommonKeywords() {
        List<LookupElement> lookupElements = new LinkedList<>();

        lookupElements.add(createKeywordAsLookup (STREAM));
        lookupElements.add(createKeywordAsLookup (DEFINE));
        lookupElements.add(createKeywordAsLookup (TABLE));
        lookupElements.add(createKeywordAsLookup (FROM));
        lookupElements.add(createKeywordAsLookup (PARTITION));
        lookupElements.add(createKeywordAsLookup (WINDOW));
        lookupElements.add(createKeywordAsLookup (SELECT));
        lookupElements.add(createKeywordAsLookup (GROUP));
        lookupElements.add(createKeywordAsLookup (BY));
        lookupElements.add(createKeywordAsLookup (HAVING));
        lookupElements.add(createKeywordAsLookup (INSERT));
        lookupElements.add(createKeywordAsLookup (DELETE));
        lookupElements.add(createKeywordAsLookup (UPDATE));
        lookupElements.add(createKeywordAsLookup (RETURN));
        lookupElements.add(createKeywordAsLookup (EVENTS));
        lookupElements.add(createKeywordAsLookup (INTO));
        lookupElements.add(createKeywordAsLookup (OUTPUT));
        lookupElements.add(createKeywordAsLookup (EXPIRED));
        lookupElements.add(createKeywordAsLookup (CURRENT));
        lookupElements.add(createKeywordAsLookup (SNAPSHOT));
        lookupElements.add(createKeywordAsLookup (FOR));
        lookupElements.add(createKeywordAsLookup (RAW));
        lookupElements.add(createKeywordAsLookup (OF));
        lookupElements.add(createKeywordAsLookup (AS));
        lookupElements.add(createKeywordAsLookup (OR));
        lookupElements.add(createKeywordAsLookup (AND));
        lookupElements.add(createKeywordAsLookup (ON));
        lookupElements.add(createKeywordAsLookup (IN));
        lookupElements.add(createKeywordAsLookup (IS));
        lookupElements.add(createKeywordAsLookup (NOT));
        lookupElements.add(createKeywordAsLookup (WITHIN));
        lookupElements.add(createKeywordAsLookup (WITH));
        lookupElements.add(createKeywordAsLookup (BEGIN));
        lookupElements.add(createKeywordAsLookup (END));
        lookupElements.add(createKeywordAsLookup (NULL));
        lookupElements.add(createKeywordAsLookup (EVERY));
        lookupElements.add(createKeywordAsLookup (LAST));
        lookupElements.add(createKeywordAsLookup (ALL));
        lookupElements.add(createKeywordAsLookup (FIRST));
        lookupElements.add(createKeywordAsLookup (JOIN));
        lookupElements.add(createKeywordAsLookup (INNER));
        lookupElements.add(createKeywordAsLookup (OUTER));
        lookupElements.add(createKeywordAsLookup (RIGHT));
        lookupElements.add(createKeywordAsLookup (LEFT));
        lookupElements.add(createKeywordAsLookup (FULL));
        lookupElements.add(createKeywordAsLookup (UNIDIRECTIONAL));
        lookupElements.add(createKeywordAsLookup (YEARS));
        lookupElements.add(createKeywordAsLookup (MONTHS));
        lookupElements.add(createKeywordAsLookup (WEEKS));
        lookupElements.add(createKeywordAsLookup (DAYS));
        lookupElements.add(createKeywordAsLookup (HOURS));
        lookupElements.add(createKeywordAsLookup (MINUTES));
        lookupElements.add(createKeywordAsLookup (SECONDS));
        lookupElements.add(createKeywordAsLookup (MILLISECONDS));
        lookupElements.add(createKeywordAsLookup (OBJECT));
        lookupElements.add(createKeywordAsLookup (AGGREGATION));
        lookupElements.add(createKeywordAsLookup (AGGREGATE));
        lookupElements.add(createKeywordAsLookup (PER));

        return lookupElements;
    }

    static void addValueKeywords(@NotNull CompletionResultSet resultSet) {
        addKeywordAsLookup(resultSet, TRUE);
        addKeywordAsLookup(resultSet, FALSE);
        addKeywordAsLookup(resultSet, NULL);
    }

    @NotNull
    public static List<LookupElement> createValueKeywords() {
        List<LookupElement> lookupElements = new LinkedList<>();
        lookupElements.add(createKeywordAsLookup(TRUE));
        lookupElements.add(createKeywordAsLookup(FALSE));
        lookupElements.add(createKeywordAsLookup(NULL));
        return lookupElements;
    }

}

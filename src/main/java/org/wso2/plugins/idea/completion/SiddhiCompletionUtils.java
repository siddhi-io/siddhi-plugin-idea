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
import com.intellij.codeInsight.completion.CompletionParameters;
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
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
//import org.wso2.plugins.idea.documentation.BallerinaDocumentationProvider;
//import org.wso2.plugins.idea.psi.FieldDefinitionNode;
//import org.wso2.plugins.idea.psi.FunctionDefinitionNode;
//import org.wso2.plugins.idea.psi.IdentifierPSINode;
//import org.wso2.plugins.idea.psi.TypeNameNode;
//import org.wso2.plugins.idea.util.BallerinaUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.plugins.idea.SiddhiIcons;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SiddhiCompletionUtils {

    private static final int VARIABLE_PRIORITY = 20;
//    private static final int FUNCTION_PRIORITY = VARIABLE_PRIORITY - 1;
    private static final int VALUE_TYPES_PRIORITY = VARIABLE_PRIORITY - 1;
//    private static final int REFERENCE_TYPES_PRIORITY = VARIABLE_PRIORITY - 1;
//    private static final int PACKAGE_PRIORITY = VALUE_TYPES_PRIORITY - 1;
//    private static final int UNIMPORTED_PACKAGE_PRIORITY = PACKAGE_PRIORITY - 1;
//    private static final int STRUCT_PRIORITY = VALUE_TYPES_PRIORITY - 1;
//    private static final int CONNECTOR_PRIORITY = VALUE_TYPES_PRIORITY - 1;
//    private static final int ACTION_PRIORITY = VALUE_TYPES_PRIORITY - 1;
//    private static final int ANNOTATION_PRIORITY = VALUE_TYPES_PRIORITY - 1;
    private static final int KEYWORDS_PRIORITY = VALUE_TYPES_PRIORITY - 2;
    private static final int STREAMID_PRIORITY = VALUE_TYPES_PRIORITY - 2;


//    // File level keywords
//    private static final LookupElementBuilder PACKAGE;
//    private static final LookupElementBuilder IMPORT;
//    private static final LookupElementBuilder CONST;
//    private static final LookupElementBuilder SERVICE;
//    private static final LookupElementBuilder RESOURCE;
//    private static final LookupElementBuilder FUNCTION;
//    private static final LookupElementBuilder CONNECTOR;
//    private static final LookupElementBuilder ACTION;
//    private static final LookupElementBuilder STRUCT;
//    private static final LookupElementBuilder TYPEMAPPER;
//    private static final LookupElementBuilder ANNOTATION;
//    private static final LookupElementBuilder ATTACH;
//    private static final LookupElementBuilder PARAMETER;
//    private static final LookupElementBuilder XMLNS;
//
//    // Any type
//    private static final LookupElementBuilder ANY;
//
//    // Simple types
//    private static final LookupElementBuilder BOOLEAN;
//    private static final LookupElementBuilder INT;
//    private static final LookupElementBuilder FLOAT;
//    private static final LookupElementBuilder STRING;
//    private static final LookupElementBuilder BLOB;
//
//    // Reference types
//    private static final LookupElementBuilder MESSAGE;
//    private static final LookupElementBuilder MAP;
//    private static final LookupElementBuilder XML;
//    private static final LookupElementBuilder XML_DOCUMENT;
//    private static final LookupElementBuilder JSON;
//    private static final LookupElementBuilder DATATABLE;

//    // Other keywords
//    private static final LookupElementBuilder REPLY;
//    private static final LookupElementBuilder RETURN;
//    private static final LookupElementBuilder IF;
//    private static final LookupElementBuilder ELSE;
//    private static final LookupElementBuilder CREATE;
//    private static final LookupElementBuilder FORK;
//    private static final LookupElementBuilder JOIN;
//    private static final LookupElementBuilder ALL;
//    private static final LookupElementBuilder SOME;
//    private static final LookupElementBuilder TIMEOUT;
//    private static final LookupElementBuilder WORKER;
//    private static final LookupElementBuilder TRANSFORM;
//    private static final LookupElementBuilder TRANSACTION;
//    private static final LookupElementBuilder ABORT;
//    private static final LookupElementBuilder ABORTED;
//    private static final LookupElementBuilder COMMITTED;
//    private static final LookupElementBuilder TRY;
//    private static final LookupElementBuilder CATCH;
//    private static final LookupElementBuilder FINALLY;
//    private static final LookupElementBuilder ITERATE;
//    private static final LookupElementBuilder WHILE;
//    private static final LookupElementBuilder CONTINUE;
//    private static final LookupElementBuilder BREAK;
//    private static final LookupElementBuilder THROW;
//
//    private static final LookupElementBuilder TRUE;
//    private static final LookupElementBuilder FALSE;
//    private static final LookupElementBuilder NULL;

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
//        PACKAGE = createKeywordLookupElement("package");
//        IMPORT = createKeywordLookupElement("import");
//        CONST = createKeywordLookupElement("const");
//        SERVICE = createKeywordLookupElement("service");
//        RESOURCE = createKeywordLookupElement("resource");
//        FUNCTION = createKeywordLookupElement("function");
//        CONNECTOR = createKeywordLookupElement("connector");
//        ACTION = createKeywordLookupElement("action");
//        STRUCT = createKeywordLookupElement("struct");
//        TYPEMAPPER = createKeywordLookupElement("typemapper");
//        ANNOTATION = createKeywordLookupElement("annotation");
//        ATTACH = createKeywordLookupElement("attach");
//        PARAMETER = createKeywordLookupElement("parameter");
//        XMLNS = createKeywordLookupElement("xmlns");
//
//        ANY = createTypeLookupElement("any", AddSpaceInsertHandler.INSTANCE);
//
//        BOOLEAN = createTypeLookupElement("boolean", AddSpaceInsertHandler.INSTANCE);
//        INT = createTypeLookupElement("int", AddSpaceInsertHandler.INSTANCE);
//        FLOAT = createTypeLookupElement("float", AddSpaceInsertHandler.INSTANCE);
//        STRING = createTypeLookupElement("string", AddSpaceInsertHandler.INSTANCE);
//        BLOB = createTypeLookupElement("blob", AddSpaceInsertHandler.INSTANCE);
//
//        MESSAGE = createTypeLookupElement("message", AddSpaceInsertHandler.INSTANCE);
//        MAP = createTypeLookupElement("map", AddSpaceInsertHandler.INSTANCE);
//        XML = createTypeLookupElement("xml", AddSpaceInsertHandler.INSTANCE);
//        XML_DOCUMENT = createTypeLookupElement("xmlDocument", AddSpaceInsertHandler.INSTANCE);
//        JSON = createTypeLookupElement("json", AddSpaceInsertHandler.INSTANCE);
//        DATATABLE = createTypeLookupElement("datatable", AddSpaceInsertHandler.INSTANCE);
//
//        REPLY = createKeywordLookupElement("reply");
//        RETURN = createKeywordLookupElement("return");
//        IF = createKeywordLookupElement("if");
//        ELSE = createKeywordLookupElement("else");
//        CREATE = createKeywordLookupElement("create");
//        FORK = createKeywordLookupElement("fork");
//        JOIN = createKeywordLookupElement("join");
//        ALL = createKeywordLookupElement("all");
//        SOME = createKeywordLookupElement("some");
//        TIMEOUT = createKeywordLookupElement("timeout");
//        WORKER = createKeywordLookupElement("worker");
//        TRANSFORM = createKeywordLookupElement("transform");
//        TRANSACTION = createKeywordLookupElement("transaction");
//        ABORT = createKeywordLookupElement("abort");
//        ABORTED = createKeywordLookupElement("aborted");
//        COMMITTED = createKeywordLookupElement("committed");
//        TRY = createKeywordLookupElement("try");
//        CATCH = createKeywordLookupElement("catch");
//        FINALLY = createKeywordLookupElement("finally");
//        ITERATE = createKeywordLookupElement("iterate");
//        WHILE = createKeywordLookupElement("while");
//        CONTINUE = createKeywordLookupElement("continue");
//        BREAK = createKeywordLookupElement("break");
//        THROW = createKeywordLookupElement("throw");
//
//        TRUE = createKeywordLookupElement("true");
//        FALSE = createKeywordLookupElement("false");
//        NULL = createKeywordLookupElement("null");

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
//        STRING= createKeywordLookupElement("string");
//        INT= createKeywordLookupElement("int");
//        LONG= createKeywordLookupElement("long");
//        FLOAT= createKeywordLookupElement("float");
//        DOUBLE= createKeywordLookupElement("double");
//        BOOL= createKeywordLookupElement("bool");
        OBJECT= createKeywordLookupElement("object"); //todo:verify this
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
        return createLookupElement(name, createTemplateBasedInsertHandler("ballerina_lang_" + name));
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

//    /**
//     * Add type names as lookups. Types include <b>any, simple types, reference types</b>.
//     *
//     * @param resultSet result list which is used to add lookups
//     */
//    static void addTypeNamesAsLookups(@NotNull CompletionResultSet resultSet) {
//        addAnyTypeAsLookup(resultSet);
//        addXmlnsAsLookup(resultSet);
//        addValueTypesAsLookups(resultSet);
//        addReferenceTypesAsLookups(resultSet);
//    }
//
//    /**
//     * Adds any type as a lookup.
//     *
//     * @param resultSet result list which is used to add lookups
//     */
//    static void addAnyTypeAsLookup(@NotNull CompletionResultSet resultSet) {
//        resultSet.addElement(PrioritizedLookupElement.withPriority(ANY, VALUE_TYPES_PRIORITY));
//    }
//
//    static void addXmlnsAsLookup(@NotNull CompletionResultSet resultSet) {
//        resultSet.addElement(PrioritizedLookupElement.withPriority(XMLNS, VALUE_TYPES_PRIORITY));
//    }

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

//    /**
//     * Adds reference types as lookups.
//     *
//     * @param resultSet result list which is used to add lookups
//     */
//    static void addReferenceTypesAsLookups(@NotNull CompletionResultSet resultSet) {
//        resultSet.addElement(PrioritizedLookupElement.withPriority(MESSAGE, REFERENCE_TYPES_PRIORITY));
//        resultSet.addElement(PrioritizedLookupElement.withPriority(MAP, REFERENCE_TYPES_PRIORITY));
//        resultSet.addElement(PrioritizedLookupElement.withPriority(XML, REFERENCE_TYPES_PRIORITY));
//        resultSet.addElement(PrioritizedLookupElement.withPriority(XML_DOCUMENT, REFERENCE_TYPES_PRIORITY));
//        resultSet.addElement(PrioritizedLookupElement.withPriority(JSON, REFERENCE_TYPES_PRIORITY));
//        resultSet.addElement(PrioritizedLookupElement.withPriority(DATATABLE, REFERENCE_TYPES_PRIORITY));
//    }
//
//    /**
//     * Adds file level keywords as lookup elements.
//     *
//     * @param resultSet   result list which is used to add lookups
//     * @param withPackage indicates whether to add 'package' keyword
//     * @param withImport  indicates whether to add 'import' keyword
//     */
//    static void addFileLevelKeywordsAsLookups(@NotNull CompletionResultSet resultSet, boolean withPackage,
//                                              boolean withImport) {
//        if (withPackage) {
//            resultSet.addElement(PrioritizedLookupElement.withPriority(PACKAGE, KEYWORDS_PRIORITY));
//        }
//        if (withImport) {
//            resultSet.addElement(PrioritizedLookupElement.withPriority(IMPORT, KEYWORDS_PRIORITY));
//        }
//        resultSet.addElement(PrioritizedLookupElement.withPriority(CONST, KEYWORDS_PRIORITY));
//        resultSet.addElement(PrioritizedLookupElement.withPriority(SERVICE, KEYWORDS_PRIORITY));
//        resultSet.addElement(PrioritizedLookupElement.withPriority(FUNCTION, KEYWORDS_PRIORITY));
//        resultSet.addElement(PrioritizedLookupElement.withPriority(CONNECTOR, KEYWORDS_PRIORITY));
//        resultSet.addElement(PrioritizedLookupElement.withPriority(STRUCT, KEYWORDS_PRIORITY));
//        resultSet.addElement(PrioritizedLookupElement.withPriority(TYPEMAPPER, KEYWORDS_PRIORITY));
//        resultSet.addElement(PrioritizedLookupElement.withPriority(ANNOTATION, KEYWORDS_PRIORITY));
//        resultSet.addElement(PrioritizedLookupElement.withPriority(XMLNS, KEYWORDS_PRIORITY));
//    }
//
//    public static List<LookupElement> createAttachmentPointsAsLookups() {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        lookupElements.add(createKeywordAsLookup(SERVICE));
//        lookupElements.add(createKeywordAsLookup(RESOURCE));
//        lookupElements.add(createKeywordAsLookup(CONNECTOR));
//        lookupElements.add(createKeywordAsLookup(ACTION));
//        lookupElements.add(createKeywordAsLookup(FUNCTION));
//        lookupElements.add(createKeywordAsLookup(TYPEMAPPER));
//        lookupElements.add(createKeywordAsLookup(STRUCT));
//        lookupElements.add(createKeywordAsLookup(CONST));
//        lookupElements.add(createKeywordAsLookup(PARAMETER));
//        lookupElements.add(createKeywordAsLookup(ANNOTATION));
//        return lookupElements;
//    }
//
//    @NotNull
//    public static List<String> getAllLambdaFunctions() {
//        List<String> lambdaFunctions = new LinkedList<>();
//        lambdaFunctions.add("foreach");
//        lambdaFunctions.add("apply");
//        lambdaFunctions.add("filter");
//        lambdaFunctions.add("count");
//        lambdaFunctions.add("sum");
//        lambdaFunctions.add("average");
//        lambdaFunctions.add("min");
//        lambdaFunctions.add("max");
//        return lambdaFunctions;
//    }

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

//        addKeywordAsLookup(resultSet, IF);
//        addKeywordAsLookup(resultSet, ELSE);
//        addKeywordAsLookup(resultSet, FORK);
//        addKeywordAsLookup(resultSet, JOIN);
//        addKeywordAsLookup(resultSet, TIMEOUT);
//        addKeywordAsLookup(resultSet, WORKER);
//        addKeywordAsLookup(resultSet, TRANSFORM);
//        addKeywordAsLookup(resultSet, TRANSACTION);
//        addKeywordAsLookup(resultSet, ABORT);
//        addKeywordAsLookup(resultSet, ABORTED);
//        addKeywordAsLookup(resultSet, COMMITTED);
//        addKeywordAsLookup(resultSet, TRY);
//        addKeywordAsLookup(resultSet, CATCH);
//        addKeywordAsLookup(resultSet, FINALLY);
//        addKeywordAsLookup(resultSet, ITERATE);
//        addKeywordAsLookup(resultSet, WHILE);
//        addKeywordAsLookup(resultSet, CONTINUE);
//        addKeywordAsLookup(resultSet, BREAK);
//        addKeywordAsLookup(resultSet, THROW);
//        addKeywordAsLookup(resultSet, IF);
//        addKeywordAsLookup(resultSet, ELSE);
//        addKeywordAsLookup(resultSet, FORK);
//        addKeywordAsLookup(resultSet, JOIN);
//        addKeywordAsLookup(resultSet, TIMEOUT);
//        addKeywordAsLookup(resultSet, WORKER);
//        addKeywordAsLookup(resultSet, TRANSFORM);
//        addKeywordAsLookup(resultSet, TRANSACTION);
//        addKeywordAsLookup(resultSet, ABORT);
//        addKeywordAsLookup(resultSet, ABORTED);
//        addKeywordAsLookup(resultSet, COMMITTED);
//        addKeywordAsLookup(resultSet, TRY);
//        addKeywordAsLookup(resultSet, CATCH);
//        addKeywordAsLookup(resultSet, FINALLY);
//        addKeywordAsLookup(resultSet, ITERATE);
//        addKeywordAsLookup(resultSet, WHILE);
//        addKeywordAsLookup(resultSet, CONTINUE);
//        addKeywordAsLookup(resultSet, BREAK);
//        addKeywordAsLookup(resultSet, THROW);
//        addKeywordAsLookup(resultSet, IF);
//        addKeywordAsLookup(resultSet, ELSE);
//        addKeywordAsLookup(resultSet, FORK);
//        addKeywordAsLookup(resultSet, JOIN);
//        addKeywordAsLookup(resultSet, TIMEOUT);
//        addKeywordAsLookup(resultSet, WORKER);
//        addKeywordAsLookup(resultSet, TRANSFORM);
//        addKeywordAsLookup(resultSet, TRANSACTION);
//        addKeywordAsLookup(resultSet, ABORT);
//        addKeywordAsLookup(resultSet, ABORTED);
//        addKeywordAsLookup(resultSet, COMMITTED);
//        addKeywordAsLookup(resultSet, TRY);
//        addKeywordAsLookup(resultSet, CATCH);
//        addKeywordAsLookup(resultSet, FINALLY);
//        addKeywordAsLookup(resultSet, ITERATE);
//        addKeywordAsLookup(resultSet, WHILE);
//        addKeywordAsLookup(resultSet, CONTINUE);
//        addKeywordAsLookup(resultSet, BREAK);
//        addKeywordAsLookup(resultSet, THROW);
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


//
//        lookupElements.add(createKeywordAsLookup(IF));
//        lookupElements.add(createKeywordAsLookup(ELSE));
//        lookupElements.add(createKeywordAsLookup(FORK));
//        lookupElements.add(createKeywordAsLookup(JOIN));
//        lookupElements.add(createKeywordAsLookup(TIMEOUT));
//        lookupElements.add(createKeywordAsLookup(WORKER));
//        lookupElements.add(createKeywordAsLookup(TRANSFORM));
//        lookupElements.add(createKeywordAsLookup(TRANSACTION));
//        lookupElements.add(createKeywordAsLookup(ABORT));
//        lookupElements.add(createKeywordAsLookup(ABORTED));
//        lookupElements.add(createKeywordAsLookup(COMMITTED));
//        lookupElements.add(createKeywordAsLookup(TRY));
//        lookupElements.add(createKeywordAsLookup(CATCH));
//        lookupElements.add(createKeywordAsLookup(FINALLY));
//        lookupElements.add(createKeywordAsLookup(ITERATE));
//        lookupElements.add(createKeywordAsLookup(WHILE));
//        lookupElements.add(createKeywordAsLookup(CONTINUE));
//        lookupElements.add(createKeywordAsLookup(BREAK));
//        lookupElements.add(createKeywordAsLookup(THROW));
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

//    /**
//     * Adds keywords related to join conditions.
//     *
//     * @param resultSet result list which is used to add lookups
//     */
//    static List<LookupElement> addJoinConditionKeywords(@NotNull CompletionResultSet resultSet) {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        lookupElements.add(createKeywordAsLookup(ALL));
//        lookupElements.add(createKeywordAsLookup(SOME));
//        return lookupElements;
//    }
//
//    static void addCreateKeyword(@NotNull CompletionResultSet resultSet) {
//        addKeywordAsLookup(resultSet, CREATE);
//    }
//
//    static void addAttachKeyword(@NotNull CompletionResultSet resultSet) {
//        addKeywordAsLookup(resultSet, ATTACH);
//    }

//    /**
//     * Adds function specific keywords like <b>return</b> as lookup elements.
//     *
//     * @param parameters parameters which contain details of completion invocation
//     * @param resultSet  result list which is used to add lookups
//     */
//    static void addFunctionSpecificKeywords(@NotNull CompletionParameters parameters,
//                                            @NotNull CompletionResultSet resultSet) {
//        PsiFile file = parameters.getOriginalFile();
//        PsiElement element = file.findElementAt(parameters.getOffset());
//        FunctionDefinitionNode functionNode = PsiTreeUtil.getParentOfType(element, FunctionDefinitionNode.class);
//        if (functionNode != null) {
//            addKeywordAsLookup(resultSet, RETURN);
//        }
//    }
//
//    static List<LookupElement> getResourceSpecificKeywords() {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        lookupElements.add(createKeywordAsLookup(REPLY));
//        return lookupElements;
//    }
//
//    static List<LookupElement> createServiceSpecificKeywords() {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        lookupElements.add(createKeywordAsLookup(RESOURCE));
//        return lookupElements;
//    }
//
//    static void addConnectorSpecificKeywords(@NotNull CompletionResultSet resultSet) {
//        addKeywordAsLookup(resultSet, ACTION);
//    }
//
//    /**
//     * Helper method to add packages as lookup elements which will be used in package declaration nodes and import
//     * declaration nodes.
//     *
//     * @param resultSet     result list which is used to add lookups
//     * @param directory     directory which needs to be added as a lookup element
//     * @param insertHandler insert handler of the lookup element
//     */
//    static void addPackagesAsLookups(@NotNull CompletionResultSet resultSet, @NotNull PsiDirectory directory,
//                                     InsertHandler<LookupElement> insertHandler) {
//        LookupElementBuilder builder = LookupElementBuilder.create(directory.getName())
//                .withTypeText("Package").withIcon(SiddhiIcons.PACKAGE).withInsertHandler(insertHandler);
//        resultSet.addElement(builder);
//    }
//
//    @NotNull
//    public static LookupElementBuilder createPackageLookupElement(@NotNull PsiDirectory directory,
//                                                                  @NotNull String name) {
//        String suggestedImportPath = SiddhiUtil.suggestPackageNameForDirectory(directory);
//        return LookupElementBuilder.createWithSmartPointer(name, directory)
//                .withTypeText("Package").withIcon(SiddhiIcons.PACKAGE)
//                .withTailText("(" + suggestedImportPath + ")", true)
//                .withInsertHandler(directory.getFiles().length == 0 ?
//                        PackageCompletionInsertHandler.INSTANCE_WITH_AUTO_POPUP : null);
//    }
//
//    @NotNull
//    public static LookupElement createPackageLookupElement(@NotNull PsiDirectory directory, @NotNull String name,
//                                                           @Nullable InsertHandler<LookupElement> handler) {
//        return createPackageLookupElement(directory, name).withInsertHandler(handler);
//    }
//
//    @NotNull
//    public static LookupElement createAnnotationLookupElement(@NotNull PsiElement element) {
//        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(element.getText(), element)
//                .withTypeText("Annotation").withIcon(SiddhiIcons.ANNOTATION)
//                .withInsertHandler(BracesInsertHandler.INSTANCE_WITH_AUTO_POPUP);
//        return PrioritizedLookupElement.withPriority(builder, ANNOTATION_PRIORITY);
//    }
//
//    @NotNull
//    public static List<LookupElement> createAnnotationLookupElements(@NotNull List<PsiElement> annotations) {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        for (PsiElement annotation : annotations) {
//            LookupElement lookupElement = org.wso2.plugins.idea.completion.SiddhiCompletionUtils.createAnnotationLookupElement(annotation);
//            lookupElements.add(lookupElement);
//        }
//        return lookupElements;
//    }
//
//    @NotNull
//    public static LookupElement createFunctionsLookupElement(@NotNull PsiElement element,
//                                                             @Nullable InsertHandler<LookupElement> insertHandler) {
//        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(element.getText(), element)
//                .withTypeText("Function").withIcon(SiddhiIcons.FUNCTION).bold()
//                .withTailText(SiddhiDocumentationProvider.getParametersAndReturnTypes(element.getParent()))
//                .withInsertHandler(insertHandler);
//        return PrioritizedLookupElement.withPriority(builder, FUNCTION_PRIORITY);
//    }
//
//    @NotNull
//    public static List<LookupElement> createFunctionsLookupElements(@NotNull List<PsiElement> functions) {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        for (PsiElement function : functions) {
//            LookupElement lookupElement = createFunctionsLookupElement(function, ParenthesisInsertHandler.INSTANCE);
//            lookupElements.add(lookupElement);
//        }
//        return lookupElements;
//    }
//
//    @NotNull
//    public static List<LookupElement> createFunctionsLookupElements(@NotNull List<PsiElement> functions,
//                                                                    @Nullable InsertHandler<LookupElement>
//                                                                            insertHandler) {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        for (PsiElement function : functions) {
//            LookupElement lookupElement = createFunctionsLookupElement(function, insertHandler);
//            lookupElements.add(lookupElement);
//        }
//        return lookupElements;
//    }
//
//    @NotNull
//    public static List<LookupElement> createLambdaFunctionLookupElements(@Nullable InsertHandler<LookupElement>
//                                                                                 insertHandler) {
//        @NotNull List<String> lambdaFunctions = getAllLambdaFunctions();
//        List<LookupElement> lookupElements = new LinkedList<>();
//        for (String function : lambdaFunctions) {
//            LookupElement lookupElement = createLambdaFunctionLookupElement(function, insertHandler);
//            lookupElements.add(lookupElement);
//        }
//        return lookupElements;
//    }
//
//    @NotNull
//    public static LookupElement createLambdaFunctionLookupElement(@NotNull String element,
//                                                                  @Nullable InsertHandler<LookupElement>
//                                                                          insertHandler) {
//        LookupElementBuilder builder = LookupElementBuilder.create(element)
//                .withTypeText("Function").withIcon(SiddhiIcons.FUNCTION).bold()
//                .withInsertHandler(insertHandler);
//        return PrioritizedLookupElement.withPriority(builder, FUNCTION_PRIORITY);
//    }
//
//    @NotNull
//    public static LookupElement createConnectorLookupElement(@NotNull PsiElement element,
//                                                             @Nullable InsertHandler<LookupElement> insertHandler) {
//        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(element.getText(), element)
//                .withTypeText("Connector").withIcon(SiddhiIcons.CONNECTOR).bold()
//                .withTailText(SiddhiDocumentationProvider.getParameterString(element.getParent(), true))
//                .withInsertHandler(insertHandler);
//        return PrioritizedLookupElement.withPriority(builder, CONNECTOR_PRIORITY);
//    }
//
//    @NotNull
//    public static List<LookupElement> createConnectorLookupElements(@NotNull List<PsiElement> connectors,
//                                                                    @Nullable InsertHandler<LookupElement>
//                                                                            insertHandler) {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        for (PsiElement connector : connectors) {
//            LookupElement lookupElement = createConnectorLookupElement(connector, insertHandler);
//            lookupElements.add(lookupElement);
//        }
//        return lookupElements;
//    }
//
//    @NotNull
//    public static List<LookupElement> createActionLookupElements(@NotNull List<PsiElement> actions) {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        for (PsiElement action : actions) {
//            LookupElementBuilder builder = LookupElementBuilder.create(action.getText())
//                    .withTypeText("Action").withIcon(SiddhiIcons.ACTION).bold()
//                    .withTailText(SiddhiDocumentationProvider.getParametersAndReturnTypes(action.getParent()))
//                    .withInsertHandler(ParenthesisInsertHandler.INSTANCE);
//            lookupElements.add(PrioritizedLookupElement.withPriority(builder, ACTION_PRIORITY));
//        }
//        return lookupElements;
//    }
//
//    @NotNull
//    public static LookupElement createStructLookupElement(@NotNull PsiElement element) {
//        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(element.getText(), element)
//                .withTypeText("Struct").withIcon(SiddhiIcons.STRUCT)
//                .withInsertHandler(AddSpaceInsertHandler.INSTANCE);
//        return PrioritizedLookupElement.withPriority(builder, STRUCT_PRIORITY);
//    }
//
//    @NotNull
//    public static List<LookupElement> createStructLookupElements(@NotNull List<PsiElement> structs) {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        for (PsiElement struct : structs) {
//            lookupElements.add(createStructLookupElement(struct));
//        }
//        return lookupElements;
//    }
//
//    @NotNull
//    public static LookupElement createVariableLookupElement(@NotNull PsiElement element) {
//        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(element.getText(), element)
//                .withTypeText("Variable").withIcon(SiddhiIcons.VARIABLE);
//        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);
//    }
//
//    @NotNull
//    public static List<LookupElement> createVariableLookupElements(@NotNull List<PsiElement> variables) {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        for (PsiElement variable : variables) {
//            LookupElement lookupElement = org.wso2.plugins.idea.completion.SiddhiCompletionUtils.createVariableLookupElement(variable);
//            lookupElements.add(lookupElement);
//        }
//        return lookupElements;
//    }
//
//    @NotNull
//    public static LookupElement createParameterLookupElement(@NotNull PsiElement element) {
//        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(element.getText(), element)
//                .withTypeText("Parameter").withIcon(SiddhiIcons.PARAMETER);
//        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);
//    }
//
//    @NotNull
//    public static List<LookupElement> createParameterLookupElements(@NotNull List<PsiElement> parameters) {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        for (PsiElement parameter : parameters) {
//            LookupElement lookupElement = createParameterLookupElement(parameter);
//            lookupElements.add(lookupElement);
//        }
//        return lookupElements;
//    }
//
//    @NotNull
//    public static LookupElement createConstantLookupElement(@NotNull PsiElement element) {
//        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(element.getText(), element)
//                .withTypeText("Constant").withIcon(SiddhiIcons.CONSTANT);
//        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);
//    }
//
//    @NotNull
//    public static List<LookupElement> createConstantLookupElements(@NotNull List<PsiElement> constants) {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        for (PsiElement constant : constants) {
//            LookupElement lookupElement = org.wso2.plugins.idea.completion.SiddhiCompletionUtils.createConstantLookupElement(constant);
//            lookupElements.add(lookupElement);
//        }
//        return lookupElements;
//    }
//
//    @NotNull
//    public static LookupElement createGlobalVariableLookupElement(@NotNull PsiElement element) {
//        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(element.getText(), element)
//                .withTypeText("Variable").withIcon(SiddhiIcons.GLOBAL_VARIABLE);
//        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);
//    }
//
//    @NotNull
//    public static List<LookupElement> createGlobalVariableLookupElements(@NotNull List<PsiElement> variables) {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        for (PsiElement variable : variables) {
//            LookupElement lookupElement = createGlobalVariableLookupElement(variable);
//            lookupElements.add(lookupElement);
//        }
//        return lookupElements;
//    }
//
//    @NotNull
//    public static LookupElement createNamespaceLookupElement(@NotNull PsiElement element) {
//        LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(element.getText(), element)
//                .withTypeText("Namespace").withIcon(SiddhiIcons.NAMESPACE);
//        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);
//    }
//
//    @NotNull
//    public static List<LookupElement> createNamespaceLookupElements(@NotNull List<PsiElement> namespaces) {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        for (PsiElement namespace : namespaces) {
//            LookupElement lookupElement = org.wso2.plugins.idea.completion.SiddhiCompletionUtils.createNamespaceLookupElement(namespace);
//            lookupElements.add(lookupElement);
//        }
//        return lookupElements;
//    }
//
//    @NotNull
//    public static LookupElementBuilder createFieldLookupElement(@NotNull IdentifierPSINode fieldName,
//                                                                @NotNull TypeNameNode fieldType,
//                                                                @NotNull IdentifierPSINode ownerName) {
//        return LookupElementBuilder.createWithSmartPointer(fieldName.getText(), fieldName)
//                .withTypeText(fieldType.getText()).withIcon(SiddhiIcons.FIELD)
//                .withTailText(" -> " + ownerName.getText(), true);
//    }
//
//    @NotNull
//    public static LookupElement createFieldLookupElement(@NotNull IdentifierPSINode fieldName,
//                                                         @NotNull TypeNameNode fieldType,
//                                                         @NotNull IdentifierPSINode ownerName,
//                                                         @Nullable InsertHandler<LookupElement> insertHandler) {
//        LookupElementBuilder builder = createFieldLookupElement(fieldName, fieldType, ownerName)
//                .withInsertHandler(insertHandler);
//        return PrioritizedLookupElement.withPriority(builder, VARIABLE_PRIORITY);
//    }
//
//    @NotNull
//    public static List<LookupElement> createFieldLookupElements(@NotNull Collection<FieldDefinitionNode>
//                                                                        fieldDefinitionNodes,
//                                                                @NotNull IdentifierPSINode definitionName,
//                                                                @Nullable InsertHandler<LookupElement> insertHandler) {
//        List<LookupElement> lookupElements = new LinkedList<>();
//        for (FieldDefinitionNode fieldDefinitionNode : fieldDefinitionNodes) {
//            IdentifierPSINode fieldName = PsiTreeUtil.getChildOfType(fieldDefinitionNode, IdentifierPSINode.class);
//            TypeNameNode fieldType = PsiTreeUtil.getChildOfType(fieldDefinitionNode, TypeNameNode.class);
//            if (fieldName == null || fieldType == null) {
//                continue;
//            }
//            LookupElement lookupElement = org.wso2.plugins.idea.completion.SiddhiCompletionUtils.createFieldLookupElement(fieldName, fieldType,
//                    definitionName, insertHandler);
//            lookupElements.add(lookupElement);
//        }
//        return lookupElements;
//    }

    /**
     * Returns the non empty element which is a previous sibling of the current node.
     *
     * @param originalFile file which contains the current element
     * @param offset       offset of the current node
     * @return {@code null} if there is no previous non empty node. Otherwise returns the corresponding
     * {@link PsiElement} node.
     */
    public static PsiElement getPreviousNonEmptyElement(PsiFile originalFile, int offset) {
        int count = 1;
        PsiElement prevElement = originalFile.findElementAt(offset - count++);
        while (prevElement instanceof PsiWhiteSpace && prevElement.getTextOffset() > 0) {
            prevElement = originalFile.findElementAt(offset - count++);
        }
        return prevElement;
    }
}

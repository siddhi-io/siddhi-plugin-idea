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

package org.wso2.siddhi.plugins.idea.structureview;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.wso2.siddhi.plugins.idea.psi.AggregationDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.ExecutionElementNode;
import org.wso2.siddhi.plugins.idea.psi.FunctionDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.SiddhiFile;
import org.wso2.siddhi.plugins.idea.psi.StreamDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.TableDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.TriggerDefinitionNode;
import org.wso2.siddhi.plugins.idea.psi.WindowDefinitionNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An element in the structure view tree model.
 */
public class SiddhiStructureViewElement implements StructureViewTreeElement, SortableTreeElement {

    protected final PsiElement element;

    SiddhiStructureViewElement(PsiElement element) {
        this.element = element;
    }

    @Override
    public Object getValue() {
        return element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (element instanceof NavigationItem) {
            ((NavigationItem) element).navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return element instanceof NavigationItem && ((NavigationItem) element).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element instanceof NavigationItem && ((NavigationItem) element).canNavigateToSource();
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        String s = element instanceof PsiNamedElement ? ((PsiNamedElement) element).getName() : null;
        if (s == null) {
            return "unknown key";
        }
        return s;
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new SiddhiItemPresentation(element);
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        if (element instanceof SiddhiFile) {
            List<TreeElement> treeElements = new ArrayList<>();
            // Add stream definitions.
            Collection<StreamDefinitionNode> streams = PsiTreeUtil.findChildrenOfType(element,
                    StreamDefinitionNode.class);
            for (PsiElement stream : streams) {
                // In here, instead of using the service, we use service.getParent(). This is done because we
                // want to show resources under a service node. This is how the sub nodes can be added.
                treeElements.add(new SiddhiStructureViewElement(stream));
            }
            // Add function definitions.
            Collection<FunctionDefinitionNode> functions = PsiTreeUtil.findChildrenOfType(element,
                    FunctionDefinitionNode.class);
            for (PsiElement function : functions) {
                treeElements.add(new SiddhiStructureViewElement(function));
            }
            // Add table definitions.
            Collection<TableDefinitionNode> tables = PsiTreeUtil.findChildrenOfType(element,
                    TableDefinitionNode.class);
            for (PsiElement table : tables) {
                // In here, instead of using the connector, we use connector.getParent(). This is done because we
                // want to show actions under a connector node. This is how the sub nodes can be added.
                treeElements.add(new SiddhiStructureViewElement(table));
            }
            // Add window definitions.
            Collection<WindowDefinitionNode> windows = PsiTreeUtil.findChildrenOfType(element,
                    WindowDefinitionNode.class);
            for (PsiElement window : windows) {
                treeElements.add(new SiddhiStructureViewElement(window));
            }
            // Add aggregation definitions
            Collection<AggregationDefinitionNode> aggregations = PsiTreeUtil.findChildrenOfType(element,
                    AggregationDefinitionNode.class);
            for (PsiElement aggregation : aggregations) {
                treeElements.add(new SiddhiStructureViewElement(aggregation));
            }
            // Add trigger definitions
            Collection<TriggerDefinitionNode> triggers = PsiTreeUtil.findChildrenOfType(element,
                    TriggerDefinitionNode.class);
            for (PsiElement trigger : triggers) {
                treeElements.add(new SiddhiStructureViewElement(trigger));
            }
            // Add executions(queries)
            Collection<ExecutionElementNode> executions = PsiTreeUtil.findChildrenOfType(element,
                    ExecutionElementNode.class);
            for (PsiElement execution : executions) {
                treeElements.add(new SiddhiStructureViewElement(execution));
            }
            // Convert the list to an array and return.
            return treeElements.toArray(new TreeElement[treeElements.size()]);
        }
        // If the element type other than what we check above, return an empty array.
        return new TreeElement[0];
    }
}

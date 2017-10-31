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

package org.wso2.siddhi.plugins.idea.debugger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import com.intellij.xdebugger.frame.XValueMarkerProvider;
import com.intellij.xdebugger.impl.actions.XDebuggerActions;
import com.intellij.xdebugger.stepping.XSmartStepIntoHandler;
import com.intellij.xdebugger.ui.XDebugTabLayouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.siddhi.plugins.idea.SiddhiTypes;
import org.wso2.siddhi.plugins.idea.debugger.breakpoint.SiddhiBreakPointTypeIN;
import org.wso2.siddhi.plugins.idea.debugger.breakpoint.SiddhiBreakPointTypeOUT;
import org.wso2.siddhi.plugins.idea.debugger.breakpoint.SiddhiBreakpointProperties;
import org.wso2.siddhi.plugins.idea.debugger.dto.BreakPoint;
import org.wso2.siddhi.plugins.idea.debugger.dto.Message;
import org.wso2.siddhi.plugins.idea.debugger.protocol.Command;
import org.wso2.siddhi.plugins.idea.debugger.protocol.Response;
import org.wso2.siddhi.plugins.idea.psi.QueryInputNode;
import org.wso2.siddhi.plugins.idea.psi.QueryOutputNode;

import javax.swing.event.HyperlinkListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SiddhiDebugProcess extends XDebugProcess {

    private static final Logger LOGGER = Logger.getInstance(SiddhiDebugProcess.class);
    private static final Gson GSON = new Gson();

    private final XDebugSession mySession;
    private final String myDebugFilePath;
    private final ProcessHandler myProcessHandler;
    private final ExecutionConsole myExecutionConsole;
    private final SiddhiDebuggerEditorsProvider myEditorsProvider;
    private final SiddhiInBreakpointHandler myInBreakPointHandler;
    private final SiddhiOutBreakpointHandler myOutBreakPointHandler;
    private final SiddhiWebSocketConnector myConnector;
    private boolean isDisconnected = false;
    private boolean isRemoteDebugMode = false;

    private final AtomicBoolean breakpointsInitiated = new AtomicBoolean();

    public SiddhiDebugProcess(@NotNull XDebugSession session,@NotNull String debugFilePath, @NotNull
            SiddhiWebSocketConnector connector,
                                 @Nullable ExecutionResult executionResult) {
        super(session);
        mySession=session;
        myDebugFilePath=debugFilePath;
        myConnector = connector;
        myProcessHandler = executionResult == null ? super.getProcessHandler() : executionResult.getProcessHandler();
        myExecutionConsole = executionResult == null ? super.createConsole() : executionResult.getExecutionConsole();
        myEditorsProvider = new SiddhiDebuggerEditorsProvider();
        myInBreakPointHandler = new SiddhiInBreakpointHandler();
        myOutBreakPointHandler = new SiddhiOutBreakpointHandler();
        if (executionResult == null) {
            isRemoteDebugMode = true;
        }
    }

    @Nullable
    @Override
    protected ProcessHandler doGetProcessHandler() {
        return myProcessHandler;
    }

    @NotNull
    @Override
    public ExecutionConsole createConsole() {
        return myExecutionConsole;
    }

    @NotNull
    @Override
    public XBreakpointHandler<?>[] getBreakpointHandlers() {
        return new XBreakpointHandler[]{myInBreakPointHandler,myOutBreakPointHandler};
    }

    @NotNull
    @Override
    public XDebuggerEditorsProvider getEditorsProvider() {
        return myEditorsProvider;
    }

    @Override
    public void sessionInitialized() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            while (!isDisconnected) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (!myConnector.isConnected()) {
                    LOGGER.debug("Not connected. Retrying...");
                    myConnector.createConnection(this::debugHit);
                    if (myConnector.isConnected()) {
                        if (isRemoteDebugMode) {
                            getSession().getConsoleView().print("Connected to the remote server at " +
                                    myConnector.getDebugServerAddress() + ".\n", ConsoleViewContentType.SYSTEM_OUTPUT);
                        }
                        LOGGER.debug("Connection created.");
                        startDebugSession();
                        break;
                    }
                } else {
                    LOGGER.debug("Connection already created.");
                    startDebugSession();
                    break;
                }
                if (isRemoteDebugMode) {
                    break;
                }
            }
            if (!myConnector.isConnected()) {
                getSession().getConsoleView().print("Connection to debug server at " +
                                myConnector.getDebugServerAddress() + " could not be established.\n",
                        ConsoleViewContentType.ERROR_OUTPUT);
                getSession().stop();
            }
        });
    }

    private void startDebugSession() {
        myConnector.sendCommand(Command.START);
        LOGGER.debug("Sending start command.");
        setQueryInOutPositions();
        initBreakpointHandlersAndSetBreakpoints();
        LOGGER.debug("Sending breakpoints.");
        myConnector.sendCommand(Command.CMD_SEND_EVENT);
        LOGGER.debug("Sending event request.");
    }

    @Override
    public void startStepOver(@Nullable XSuspendContext context) {
        String threadId = getThreadId(context);
        if (threadId != null) {
            myConnector.sendCommand(Command.STEP_OVER);
        }
    }

    @Override
    public void stop() {
        // If we don't call this using the executeOnPooledThread(), the UI will hang until the debug server is stopped.
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            XDebugSession session = getSession();
            if (!isRemoteDebugMode) {
                XSuspendContext suspendContext = session.getSuspendContext();
                if (suspendContext != null) {
                    XExecutionStack activeExecutionStack = suspendContext.getActiveExecutionStack();
                    if (activeExecutionStack instanceof SiddhiSuspendContext.SiddhiExecutionStack) {
                        String threadId = ((SiddhiSuspendContext.SiddhiExecutionStack) activeExecutionStack)
                                .getThreadId();
                        if (threadId != null) {
                            myConnector.sendCommand(Command.STOP);
                        }
                    }
                } else {
                    session.stop();
                    return;
                }
            } else {
                myConnector.sendCommand(Command.STOP);
                session.stop();
                getSession().getConsoleView().print("Disconnected from the debug server.\n",
                        ConsoleViewContentType.SYSTEM_OUTPUT);
            }
            isDisconnected = true;
            myConnector.close();
        });
    }

    @Override
    public void resume(@Nullable XSuspendContext context) {
        String threadId = getThreadId(context);
        if (threadId != null) {
            myConnector.sendCommand(Command.RESUME);
        }
    }

    @Nullable

    private String getThreadId(@Nullable XSuspendContext context) {
        if (context != null) {
            XExecutionStack activeExecutionStack = context.getActiveExecutionStack();
            if (activeExecutionStack instanceof SiddhiSuspendContext.SiddhiExecutionStack) {
                return ((SiddhiSuspendContext.SiddhiExecutionStack) activeExecutionStack).getThreadId();
            }
        }
        getSession().getConsoleView().print("Error occurred while getting the thread ID.",
                ConsoleViewContentType.ERROR_OUTPUT);
        getSession().stop();
        return null;
    }

    @Nullable
    @Override
    public XSmartStepIntoHandler<?> getSmartStepIntoHandler() {
        return super.getSmartStepIntoHandler();
    }

    @Override
    public void runToPosition(@NotNull XSourcePosition position, @Nullable XSuspendContext context) {
    }

    @Override
    public boolean checkCanInitBreakpoints() {
        // We manually initializes the breakpoints after connecting to the debug server.
        return false;
    }

    private void debugHit(String response) {
        LOGGER.debug("Received: " + response);
        Message message;
        try {
            message = GSON.fromJson(response, Message.class);
        } catch (JsonSyntaxException e) {
            LOGGER.debug(e);
            return;
        }
        String code = message.getCode();
        if (Response.DEBUG_HIT.name().equals(code)) {
            ApplicationManager.getApplication().runReadAction(() -> {
                int queryIndex=message.getLocation().getQueryIndex();
                String queryTerminal=message.getLocation().getQueryTerminal();
                if(queryTerminal.equalsIgnoreCase("in")){
                    int lineNumber=queryInLinePositions.get(queryIndex);
                    message.getLocation().setLineNumber(lineNumber);
                }else if(queryTerminal.equalsIgnoreCase("out")){
                    int lineNumber=queryOutLinePositions.get(queryIndex);
                    message.getLocation().setLineNumber(lineNumber);
                }

                XBreakpoint<SiddhiBreakpointProperties> breakpoint = findBreakPoint(message.getLocation());
                SiddhiSuspendContext context = new SiddhiSuspendContext(SiddhiDebugProcess.this, message);
                XDebugSession session = getSession();
                if (breakpoint == null) {
                    session.positionReached(context);
                } else {
                    session.breakpointReached(breakpoint, null, context);
                }
            });
        } else if (Response.EXIT.name().equals(code) || Response.COMPLETE.name().equals(code)) {
            if (isRemoteDebugMode) {
                // If we don't call executeOnPooledThread() here, session will not be stopped correctly since this is
                // called from netty. It seems like this is a blocking action and netty throws an exception.
                ApplicationManager.getApplication().executeOnPooledThread(
                        () -> {
                            XDebugSession session = getSession();
                            if (session != null) {
                                session.sessionResumed();
                                session.stop();
                            }
                            getSession().getConsoleView().print("Remote debugging finished.\n",
                                    ConsoleViewContentType.SYSTEM_OUTPUT);
                        }
                );
            }
        }
    }

    private void initBreakpointHandlersAndSetBreakpoints() {
        if (!breakpointsInitiated.compareAndSet(false, true)) {
            return;
        }
        doSetBreakpoints();
    }

    private void doSetBreakpoints() {
        AccessToken token = ReadAction.start();
        try {
            getSession().initBreakpoints();
        } finally {
            token.finish();
            token.close();
        }
    }

    private XBreakpoint<SiddhiBreakpointProperties> findBreakPoint(@NotNull BreakPoint breakPoint) {
        String fileName = breakPoint.getFileName();
        int lineNumber = breakPoint.getLineNumber();

        List<XBreakpoint<SiddhiBreakpointProperties>> breakpoints = ContainerUtil.createConcurrentList();
        breakpoints.addAll(inBreakpoints);
        breakpoints.addAll(outBreakpoints);
        for (XBreakpoint<SiddhiBreakpointProperties> breakpoint : breakpoints) {
            XSourcePosition breakpointPosition = breakpoint.getSourcePosition();
            if (breakpointPosition == null) {
                continue;
            }
            VirtualFile file = breakpointPosition.getFile();
            int line = breakpointPosition.getLine() + 1;
            String filePath = file.getName();

            if (filePath.equals(fileName) && line == lineNumber) {
                return breakpoint;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public XValueMarkerProvider<?, ?> createValueMarkerProvider() {
        return super.createValueMarkerProvider();
    }

    @Override
    public String getCurrentStateMessage() {
        return myConnector.getState();
    }

    @Nullable
    @Override
    public HyperlinkListener getCurrentStateHyperlinkListener() {
        return super.getCurrentStateHyperlinkListener();
    }

    @NotNull
    @Override
    public XDebugTabLayouter createTabLayouter() {
        return super.createTabLayouter();
    }

    @Nullable
    @Override
    public XDebuggerEvaluator getEvaluator() {
        return super.getEvaluator();
    }

    @Override
    public void registerAdditionalActions(@NotNull DefaultActionGroup leftToolbar,
                                          @NotNull DefaultActionGroup topToolbar,
                                          @NotNull DefaultActionGroup settings) {
        topToolbar.removeAll();
        topToolbar.add(ActionManager.getInstance().getAction(XDebuggerActions.SHOW_EXECUTION_POINT));
        topToolbar.add(ActionManager.getInstance().getAction(XDebuggerActions.STEP_OVER));
        leftToolbar.remove(ActionManager.getInstance().getAction(XDebuggerActions.PAUSE));
    }

    private final List<Integer> queryInLinePositions=new ArrayList<>();//arrayList Index=>queryIndex value=>Line number
    private final List<Integer> queryOutLinePositions=new ArrayList<>();//arrayList Index=>queryIndex value=>Line number

    private void setQueryInOutPositions(){
        File localFile =new File(myDebugFilePath);
        VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(localFile);
        ApplicationManager.getApplication().runReadAction(() -> {
            PsiFile psiFile = null;
            if (file != null) {
                psiFile = PsiManager.getInstance(getSession().getProject()).findFile(file);
            }
            Document document = null;
            if (psiFile != null) {
                document = PsiDocumentManager.getInstance(mySession.getProject()).getDocument(psiFile);
            }

            List queryInList = Arrays.asList(PsiTreeUtil.findChildrenOfType(psiFile, QueryInputNode.class).toArray());
            for (int i = 0; i < queryInList.size(); i++) {
                PsiElement psiElement = (PsiElement) queryInList.get(i);
                PsiElement prevVisSibling = PsiTreeUtil.prevVisibleLeaf(psiElement);
                if (prevVisSibling != null && document != null) {
                    int lineNumber = document.getLineNumber(prevVisSibling.getTextRange().getStartOffset());
                    queryInLinePositions.add(i, lineNumber + 1);
                }
            }

            List queryOutList = Arrays.asList(PsiTreeUtil.findChildrenOfType(psiFile, QueryOutputNode.class).toArray());
            for (int i = 0; i < queryOutList.size(); i++) {
                PsiElement psiElement = (PsiElement) queryOutList.get(i);
                if (psiElement != null && document != null) {
                    int lineNumber = document.getLineNumber(psiElement.getTextRange().getStartOffset());
                    queryOutLinePositions.add(i, lineNumber + 1);
                }
            }
        });
    }

    private final List<XBreakpoint<SiddhiBreakpointProperties>> inBreakpoints = ContainerUtil.createConcurrentList();
    private final List<XBreakpoint<SiddhiBreakpointProperties>> outBreakpoints = ContainerUtil.createConcurrentList();

    private class SiddhiInBreakpointHandler extends
            XBreakpointHandler<XLineBreakpoint<SiddhiBreakpointProperties>> {

        SiddhiInBreakpointHandler() {
            super(SiddhiBreakPointTypeIN.class);
        }

        @Override
        public void registerBreakpoint(@NotNull XLineBreakpoint<SiddhiBreakpointProperties> breakpoint) {
            XSourcePosition breakpointPosition = breakpoint.getSourcePosition();
            if (breakpointPosition == null) {
                return;
            }
            VirtualFile file = breakpointPosition.getFile();
            if(!file.getPath().equalsIgnoreCase(myDebugFilePath)){
                return;
            }
            PsiFile psiFile = PsiManager.getInstance(getSession().getProject()).findFile(file);
            int offset=breakpointPosition.getOffset();
            PsiElement element= null;
            if (psiFile != null) {
                element = psiFile.findElementAt(offset);
            }
            if (!((element != null) && (((LeafPsiElement) element).getElementType().equals(SiddhiTypes.FROM)))) {
                return;
            }
            inBreakpoints.add(breakpoint);
            sendBreakpoints();
            getSession().updateBreakpointPresentation(breakpoint, AllIcons.Debugger.Db_verified_breakpoint , null);
        }

        @Override
        public void unregisterBreakpoint(@NotNull XLineBreakpoint<SiddhiBreakpointProperties> breakpoint,
                                         boolean temporary) {
            XSourcePosition breakpointPosition = breakpoint.getSourcePosition();
            if (breakpointPosition == null) {
                return;
            }
            inBreakpoints.remove(breakpoint);
            sendRemovedBreakpoint(breakpoint);
        }

        void sendRemovedBreakpoint(@NotNull XLineBreakpoint<SiddhiBreakpointProperties> breakpoint){
            XSourcePosition breakpointPosition = breakpoint.getSourcePosition();
            if (breakpointPosition == null) {
                return;
            }
            VirtualFile file = breakpointPosition.getFile();
            if(!file.getPath().equalsIgnoreCase(myDebugFilePath)){
                return;
            }
            int line = breakpointPosition.getLine();
            String name = file.getName();
            String terminal="IN";
            int queryIndex=-1;
            for(int j=0;j<queryInLinePositions.size();j++){
                if((queryInLinePositions.get(j)-1)==line){
                    queryIndex=j;
                    break;
                }
            }
            String stringBuilder = "{\"command\":\"" + Command.REMOVE_BREAKPOINT +
                    "\", \"points\": [" +
                    "{\"fileName\":\"" + name + "\", " +
                    "\"queryIndex\":" + queryIndex + ", " +
                    "\"queryTerminal\":\"" + terminal + "\"}" +
                    "]}";
            myConnector.send(stringBuilder);
        }

        void sendBreakpoints() {
            StringBuilder stringBuilder = new StringBuilder("{\"command\":\"").append(Command.SET_POINTS)
                    .append("\", \"points\": [");
            if (!getSession().areBreakpointsMuted()) {
                ApplicationManager.getApplication().runReadAction(() -> {
                    int size = inBreakpoints.size();
                    for (int i = 0; i < size; i++) {
                        XSourcePosition breakpointPosition = inBreakpoints.get(i).getSourcePosition();
                        if (breakpointPosition == null) {
                            return;
                        }
                        VirtualFile file = breakpointPosition.getFile();
                        int line = breakpointPosition.getLine();
                        String name = file.getName();
                        String terminal="IN";
                        int queryIndex=-1;
                        for(int j=0;j<queryInLinePositions.size();j++){
                            if((queryInLinePositions.get(j)-1)==line){
                                queryIndex=j;
                                break;
                            }
                        }
                        stringBuilder.append("{\"fileName\":\"").append(name).append("\", ");
                        stringBuilder.append("\"queryIndex\":").append(queryIndex).append(", ");
                        stringBuilder.append("\"queryTerminal\":\"").append(terminal).append("\"}");
                        if (i < size - 1) {
                            stringBuilder.append(",");
                        }
                    }
                });
            }
            stringBuilder.append("]}");
            myConnector.send(stringBuilder.toString());
        }
    }

    private class SiddhiOutBreakpointHandler extends
            XBreakpointHandler<XLineBreakpoint<SiddhiBreakpointProperties>> {

        SiddhiOutBreakpointHandler() {
            super(SiddhiBreakPointTypeOUT.class);
        }

        @Override
        public void registerBreakpoint(@NotNull XLineBreakpoint<SiddhiBreakpointProperties> breakpoint) {
            XSourcePosition breakpointPosition = breakpoint.getSourcePosition();
            if (breakpointPosition == null) {
                return;
            }

            VirtualFile file = breakpointPosition.getFile();
            if(!file.getPath().equalsIgnoreCase(myDebugFilePath)){
                return;
            }
            PsiFile psiFile = PsiManager.getInstance(getSession().getProject()).findFile(file);

            int offset=breakpointPosition.getOffset();
            PsiElement element= null;
            if (psiFile != null) {
                element = psiFile.findElementAt(offset);
            }
            if (!(element != null && ((LeafPsiElement) element).getElementType().equals(SiddhiTypes.INSERT))) {
                return;
            }
            outBreakpoints.add(breakpoint);
            sendBreakpoints();
            getSession().updateBreakpointPresentation(breakpoint, AllIcons.Debugger.Db_verified_breakpoint, null);
        }

        @Override
        public void unregisterBreakpoint(@NotNull XLineBreakpoint<SiddhiBreakpointProperties> breakpoint,
                                         boolean temporary) {
            XSourcePosition breakpointPosition = breakpoint.getSourcePosition();
            if (breakpointPosition == null) {
                return;
            }
            outBreakpoints.remove(breakpoint);
            sendRemovedBreakpoint(breakpoint);
        }

        void sendRemovedBreakpoint(@NotNull XLineBreakpoint<SiddhiBreakpointProperties> breakpoint){
            XSourcePosition breakpointPosition = breakpoint.getSourcePosition();
            if (breakpointPosition == null) {
                return;
            }
            VirtualFile file = breakpointPosition.getFile();
            if(!file.getPath().equalsIgnoreCase(myDebugFilePath)){
                return;
            }
            int line = breakpointPosition.getLine();
            String name = file.getName();
            String terminal="OUT";
            int queryIndex=-1;
            for(int j=0;j<queryOutLinePositions.size();j++){
                if((queryOutLinePositions.get(j)-1)==line){
                    queryIndex=j;
                    break;
                }
            }
            String stringBuilder = "{\"command\":\"" + Command.REMOVE_BREAKPOINT +
                    "\", \"points\": [" +
                    "{\"fileName\":\"" + name + "\", " +
                    "\"queryIndex\":" + queryIndex + ", " +
                    "\"queryTerminal\":\"" + terminal + "\"}" +
                    "]}";
            myConnector.send(stringBuilder);
        }

        void sendBreakpoints() {
            StringBuilder stringBuilder = new StringBuilder("{\"command\":\"").append(Command.SET_POINTS)
                    .append("\", \"points\": [");
            if (!getSession().areBreakpointsMuted()) {
                ApplicationManager.getApplication().runReadAction(() -> {
                    int size = outBreakpoints.size();
                    for (int i = 0; i < size; i++) {
                        XSourcePosition breakpointPosition = outBreakpoints.get(i).getSourcePosition();
                        if (breakpointPosition == null) {
                            return;
                        }

                        VirtualFile file = breakpointPosition.getFile();
                        int line = breakpointPosition.getLine();
                        String name = file.getName();
                        String terminal="OUT";
                        int queryIndex=-1;
                        for(int j=0;j<queryOutLinePositions.size();j++){
                            if((queryOutLinePositions.get(j)-1)==line){
                                queryIndex=j;
                                break;
                            }
                        }

                        stringBuilder.append("{\"fileName\":\"").append(name).append("\", ");
                        stringBuilder.append("\"queryIndex\":").append(queryIndex).append(", ");
                        stringBuilder.append("\"queryTerminal\":\"").append(terminal).append("\"}");
                        if (i < size - 1) {
                            stringBuilder.append(",");
                        }
                    }
                });
            }
            stringBuilder.append("]}");
            myConnector.send(stringBuilder.toString());
        }
    }
}

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

package org.wso2.plugins.idea.debugger.dto;

import java.util.Map;

public class Frame {

    private String frameName, fileName;

    private String queryName;

    private Map<String, Object> queryState;

    private Object eventInfo;

    private BreakPoint location;

    public Frame(String frameName, String fileName){
        this.frameName=frameName;
        this.fileName=fileName;
    }

    public String getFrameName() {
        return frameName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public Map<String, Object> getQueryState() {
        return queryState;
    }

    public void setQueryState(Map<String, Object> queryState) {
        this.queryState = queryState;
    }


    public BreakPoint getLocation() {
        return location;
    }

    public void setLocation(BreakPoint location) {
        this.location = location;
    }

    public Object getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(Object eventInfo) {
        this.eventInfo = eventInfo;
    }

}

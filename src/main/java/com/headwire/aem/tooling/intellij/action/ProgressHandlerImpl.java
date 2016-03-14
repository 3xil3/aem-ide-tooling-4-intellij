/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.headwire.aem.tooling.intellij.action;

import com.intellij.openapi.progress.ProgressIndicator;

/**
 * Created by schaefa on 3/14/16.
 */
public class ProgressHandlerImpl
    implements ProgressHandler
{
    private int steps;
    private double currentStep;
    private String title;
    private ProgressHandlerImpl subTasks;
    private ProgressIndicator progressIndicator;

    public ProgressHandlerImpl(String title) {
        this(null, 0, title);
    }

    public ProgressHandlerImpl(ProgressIndicator progressIndicator, String title) {
        this(progressIndicator, 0, title);
    }

    public ProgressHandlerImpl(ProgressIndicator progressIndicator, int steps, String title) {
        this.steps = steps;
        this.title = title;
        this.progressIndicator = progressIndicator;
    }

    @Override
    public ProgressHandler startSubTasks(int Steps, String title) {
        if(subTasks != null) {
            // Finish any existing sub task
            subTasks.finish();
        }
        subTasks = new ProgressHandlerImpl(progressIndicator, steps, title);
        if(progressIndicator != null) {
            progressIndicator.pushState();
            progressIndicator.setText(title);
        }
        return subTasks;
    }

    @Override
    public void next(String task) {
        if(subTasks != null) {
            subTasks.finish();
        }
        if(progressIndicator != null) {
            progressIndicator.setText2(task);
            if(steps > 0) {
                // Set fraction as the ratio between the actual step and the total steps
                progressIndicator.setFraction(currentStep++ / steps);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    private void finish() {
        if(subTasks != null) {
            // First finish any sub tasks (recursively)
            subTasks.finish();
        }
        if(progressIndicator != null) {
            // Then pop the indicator state to go back to where this one was
            progressIndicator.popState();
            progressIndicator = null;
        }
    }
}

// Copyright 2016 Google, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package com.google.codelabs.migratingtojobs.jobscheduler_complete;

import android.app.Application;

import com.google.codelabs.migratingtojobs.shared.AppModule;
import com.google.codelabs.migratingtojobs.shared.EventBus;
import com.google.codelabs.migratingtojobs.shared.SharedInitializer;

import javax.inject.Inject;

public class JobSchedulerGlobalState {
    private static JobSchedulerGlobalState sInstance;

    public static JobSchedulerComponent get(Application app) {
        if (sInstance == null) {
            synchronized (JobSchedulerGlobalState.class) {
                if (sInstance == null) {
                    sInstance = new JobSchedulerGlobalState(app);

                    sInstance.init();
                }
            }
        }

        return sInstance.get();
    }

    private final JobSchedulerComponent component;
    @Inject
    SharedInitializer mSharedInitializer;
    @Inject
    EventBus bus;
    @Inject
    JobSchedulingErrorListener jobSchedulingErrorListener;

    public JobSchedulerGlobalState(Application app) {
        component = DaggerJobSchedulerComponent.builder()
                .appModule(new AppModule(app))
                .build();

        component.inject(this);
    }

    private void init() {
        mSharedInitializer.init();

        bus.register(jobSchedulingErrorListener);
    }

    public JobSchedulerComponent get() {
        return component;
    }
}

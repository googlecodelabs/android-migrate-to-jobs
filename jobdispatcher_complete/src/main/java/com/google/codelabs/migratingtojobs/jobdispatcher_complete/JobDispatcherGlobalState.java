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

package com.google.codelabs.migratingtojobs.jobdispatcher_complete;

import android.app.Application;

import com.google.codelabs.migratingtojobs.shared.AppModule;
import com.google.codelabs.migratingtojobs.shared.EventBus;
import com.google.codelabs.migratingtojobs.shared.SharedInitializer;

import javax.inject.Inject;

public class JobDispatcherGlobalState {
    private static JobDispatcherGlobalState sInstance;

    public static JobDispatcherComponent get(Application app) {
        if (sInstance == null) {
            synchronized (JobDispatcherGlobalState.class) {
                if (sInstance == null) {
                    sInstance = new JobDispatcherGlobalState(app);

                    sInstance.init();
                }
            }
        }

        return sInstance.get();
    }

    private final JobDispatcherComponent mComponent;

    @Inject
    SharedInitializer mSharedInitializer;

    @Inject
    EventBus mBus;

    @Inject
    JobDispatchingErrorListener mJobDispatchingErrorListener;

    public JobDispatcherGlobalState(Application app) {
        mComponent = DaggerJobDispatcherComponent.builder()
                .appModule(new AppModule(app))
                .build();

        mComponent.inject(this);
    }

    private void init() {
        mSharedInitializer.init();

        mBus.register(mJobDispatchingErrorListener);
    }

    public JobDispatcherComponent get() {
        return mComponent;
    }
}

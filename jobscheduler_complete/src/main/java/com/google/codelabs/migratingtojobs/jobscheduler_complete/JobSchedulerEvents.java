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

import com.google.codelabs.migratingtojobs.shared.EventBus;

public final class JobSchedulerEvents {
    static final int DOWNLOAD_JOB_FINISHED = 1 << EventBus.FIRST_UNUSED;
    static final int DOWNLOAD_JOB_FAILED = 1 << (EventBus.FIRST_UNUSED + 1);

    private JobSchedulerEvents() {
    }

    public static void postDownloadJobFinished(EventBus bus) {
        bus.send(DOWNLOAD_JOB_FINISHED);
    }

    public static void postDownloadJobFailed(EventBus bus) {
        bus.send(DOWNLOAD_JOB_FAILED);
    }
}

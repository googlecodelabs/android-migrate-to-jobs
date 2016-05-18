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

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.codelabs.migratingtojobs.shared.BaseEventListener;
import com.google.codelabs.migratingtojobs.shared.CatalogItem;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

public class JobSchedulingErrorListener extends BaseEventListener {
    public final static int DOWNLOAD_JOB_ID = 1;

    private static final String TAG = "JS D/L Handler";

    private final ExecutorService executorService;
    /**
     * All calls to the JobScheduler go through IPC, so we do it all on a worker thread in this
     * Runnable.
     */
    private final Runnable scheduleRunnable;
    /**
     * Whether we've already scheduled a job. We should avoid making too many expensive IPC calls,
     * so check here (synchronize on `this`) first.
     */
    private boolean jobScheduled = false;

    @Inject
    public JobSchedulingErrorListener(@NonNull Context appContext,
                                      @Named("worker") ExecutorService executorService) {
        this.scheduleRunnable = new ScheduleRunnable(appContext);
        this.executorService = executorService;
    }

    @Override
    public void onItemDownloadFailed(CatalogItem item) {
        // most checks shouldn't have to wait for the synch lock
        if (!jobScheduled) {
            synchronized (this) {
                if (!jobScheduled) {
                    executorService.submit(scheduleRunnable);
                    jobScheduled = true;
                }
            }
        }
    }

    @Override
    public void handle(Message msg) {
        if (msg.what == JobSchedulerEvents.DOWNLOAD_JOB_FINISHED) {
            synchronized (this) {
                jobScheduled = false;
            }
        }
    }

    private static class ScheduleRunnable implements Runnable {
        private final Context appContext;
        private final JobInfo jobToSchedule;

        public ScheduleRunnable(Context appContext) {
            this.appContext = appContext;
            this.jobToSchedule = new JobInfo.Builder(
                    DOWNLOAD_JOB_ID, new ComponentName(appContext, DownloaderJobService.class))
                    // Normally you'd want this be NETWORK_TYPE_UNMETERED, but the
                    // ConnectivityManager hack we're using in Downloader only checks for "a"
                    // connection, so let's emulate that here.
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
        }

        @Override
        public void run() {
            JobScheduler js = (JobScheduler) appContext
                    .getSystemService(Context.JOB_SCHEDULER_SERVICE);

            if (js == null) {
                Log.e(TAG, "unable to retrieve JobScheduler. What's going on?");
                return;
            }

            Log.i(TAG, "scheduling new job");
            if (js.schedule(jobToSchedule) == JobScheduler.RESULT_FAILURE) {
                Log.e(TAG, "encountered unknown error scheduling job");
            }
        }
    }
}

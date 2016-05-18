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

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.google.codelabs.migratingtojobs.shared.BaseEventListener;
import com.google.codelabs.migratingtojobs.shared.CatalogItem;
import com.google.codelabs.migratingtojobs.shared.CatalogItemStore;
import com.google.codelabs.migratingtojobs.shared.EventBus;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

/**
 * DownloaderJobService is responsible for kicking off the download process via DownloaderThreads.
 */
public final class DownloaderJobService extends JobService {

    /**
     * List of all listeners we register, so we can make sure they get unregistered when this
     * service goes away.
     */
    final List<EventListener> eventListeners = new LinkedList<>();
    @Inject
    EventBus bus;
    @Inject
    CatalogItemStore itemStore;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize everything if it's not already, plus inject dependencies.
        JobSchedulerGlobalState.get(getApplication()).inject(this);
    }

    @Override
    public void onDestroy() {
        synchronized (eventListeners) {
            for (EventListener listener : eventListeners) {
                // unregistering prevents leaks.
                bus.unregister(listener);
            }
        }

        super.onDestroy();
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        EventListener listener = new EventListener(this, jobParameters, bus);
        synchronized (eventListeners) {
            eventListeners.add(listener);
            bus.register(listener);
        }

        // TRIGGER WORK
        bus.postRetryDownloads(itemStore);

        return true; // true because there's more work being done on a separate thread
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        // if we haven't finished yet, it's safe to assume that there's more work to be done.
        // True means we'd like to be rescheduled.
        return true;
    }

    private final static class EventListener extends BaseEventListener {
        private final JobService service;
        private final JobParameters jobParameters;
        private final EventBus bus;

        public EventListener(JobService service, JobParameters jobParameters, EventBus bus) {
            this.service = service;
            this.jobParameters = jobParameters;
            this.bus = bus;
        }

        @Override
        public void onItemDownloadFailed(CatalogItem item) {
            service.jobFinished(jobParameters, true);
            JobSchedulerEvents.postDownloadJobFailed(bus);
        }

        @Override
        public void onAllDownloadsFinished() {
            service.jobFinished(jobParameters, false);
            JobSchedulerEvents.postDownloadJobFinished(bus);
        }
    }
}

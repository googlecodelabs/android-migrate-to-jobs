package com.google.codelabs.migratingtojobs.jobdispatcher_complete;

import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.google.codelabs.migratingtojobs.shared.BaseEventListener;
import com.google.codelabs.migratingtojobs.shared.CatalogItem;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

public class JobDispatchingErrorListener extends BaseEventListener {
    @NonNull
    private final ExecutorService executorService;

    private final ScheduleRunnable scheduleRunnable;

    private boolean jobScheduled = false;

    @Inject public JobDispatchingErrorListener(@NonNull FirebaseJobDispatcher dispatcher,
                                               @NonNull @Named("worker") ExecutorService executorService) {
        this.executorService = executorService;
        this.scheduleRunnable = new ScheduleRunnable(dispatcher);
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
    public void handle(int what) {
        if (what == JobDispatcherEvents.DOWNLOAD_JOB_FINISHED) {
            synchronized (this) {
                jobScheduled = false;
            }
        }
    }

    private static class ScheduleRunnable implements Runnable {
        private static final String TAG = "FJD D/L Handler";

        private final FirebaseJobDispatcher dispatcher;
        private final Job jobToSchedule;

        public ScheduleRunnable(FirebaseJobDispatcher dispatcher) {
            this.dispatcher = dispatcher;
            this.jobToSchedule = dispatcher.newJobBuilder()
                    .setTag("downloader_job")
                    .setService(DownloaderJobService.class)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .build();
        }

        @Override
        public void run() {
            Log.v(TAG, "Scheduling download job");
            dispatcher.mustSchedule(this.jobToSchedule);
        }
    }
}

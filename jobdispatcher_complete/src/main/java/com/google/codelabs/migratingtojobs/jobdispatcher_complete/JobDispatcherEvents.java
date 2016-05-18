package com.google.codelabs.migratingtojobs.jobdispatcher_complete;

import com.google.codelabs.migratingtojobs.shared.EventBus;

public class JobDispatcherEvents {
    public static final int DOWNLOAD_JOB_FINISHED = EventBus.FIRST_UNUSED;
    public static final int DOWNLOAD_JOB_FAILED = EventBus.FIRST_UNUSED + 1;

    public static void postDownloadJobFinished(EventBus bus) {
        bus.send(DOWNLOAD_JOB_FINISHED);
    }

    public static void postDownloadJobFailed(EventBus bus) {
        bus.send(DOWNLOAD_JOB_FAILED);
    }
}

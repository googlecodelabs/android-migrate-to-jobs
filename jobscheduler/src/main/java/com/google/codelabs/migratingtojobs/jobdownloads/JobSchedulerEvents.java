package com.google.codelabs.migratingtojobs.jobdownloads;

import com.google.codelabs.migratingtojobs.common.EventBus;

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

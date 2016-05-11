package com.google.codelabs.migratingtojobs.jobdownloads;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * DownloaderJobService is responsible for kicking off the download process via DownloaderThreads.
 */
public class DownloaderJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // start a separate thread to handle the downloads
        new DownloaderThread(jobParameters, this).start();

        return true; // true because there's more work being done on a separate thread
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        // if we haven't finished yet, it's safe to assume that there's more work to be done.
        // True means we'd like to be rescheduled.
        return true;
    }

}

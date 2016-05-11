package com.google.codelabs.migratingtojobs.jobdownloads;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.google.codelabs.migratingtojobs.common.CatalogItem;
import com.google.codelabs.migratingtojobs.common.Downloader;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * DownloaderThread handles downloading all pending books and reporting the
 * result back to the provided JobService.
 */
class DownloaderThread extends Thread implements Downloader.OnCompleteCallback {
    /** Our logging tag. */
    private static final String TAG = "DownloaderThread";

    /** A latch configured with the number of downloads running. */
    private CountDownLatch mLatch;
    /** The list of books to download. */
    private List<CatalogItem> mDownloadQueue;
    /** An array to capture the results of each download. */
    private int[] mResults;
    /** An index into our results array. */
    private int mResultIndex = 0;

    /** We need this in order to report the results back to the JobService. */
    private final JobParameters mJobParams;
    /** We need this to have something to report results back to. */
    private final JobService mJobService;

    /** This is our project's downloader. It downloads books. */
    private Downloader mDownloader;

    public DownloaderThread(JobParameters jobParameters, JobService jobService) {
        initDependencies((App) jobService.getApplication());

        // we need these two to let the JobScheduler know we're done
        mJobParams = jobParameters;
        mJobService = jobService;
    }

    private void initDependencies(App app) {
        // this is the object that actually does the downloads
        mDownloader = app.getDownloader();
        // grab the list of things to be downloaded
        mDownloadQueue = app.getCatalogStore().getDownloadQueue();
        // we need to wait for each of the download requests to finish
        mLatch = new CountDownLatch(mDownloadQueue.size());
        // create an array to store the results of each download
        mResults = new int[mDownloadQueue.size()];
    }

    @Override
    public void run() {
        startDownloads();

        waitForDownloads();

        reportResults();
    }

    private void startDownloads() {
        for (CatalogItem item : mDownloadQueue) {
            mDownloader.start(item, this);
        }
    }

    private void waitForDownloads() {
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "Unexpected exception waiting for all the downloads to finish");
        }
    }

    private void reportResults() {
        // verify there were no failures
        for (int mResult : mResults) {
            if (mResult == Downloader.FAILURE) {
                mJobService.jobFinished(mJobParams, true /* something failed, reschedule */);
            }
        }

        mJobService.jobFinished(mJobParams,
                false /* everything went smoothly, no need to reschedule */);
    }

    /**
     * Called by the Downloader when a download completes.
     */
    @Override
    public synchronized void onComplete(CatalogItem item, int status) {
        mLatch.countDown();

        mResults[mResultIndex] = status;
        mResultIndex++;
    }
}

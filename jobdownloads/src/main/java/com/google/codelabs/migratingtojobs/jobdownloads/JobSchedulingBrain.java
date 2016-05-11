package com.google.codelabs.migratingtojobs.jobdownloads;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.google.codelabs.migratingtojobs.common.BasicBrain;
import com.google.codelabs.migratingtojobs.common.CatalogItem;
import com.google.codelabs.migratingtojobs.common.Downloader;

public class JobSchedulingBrain extends BasicBrain {
    public final static int DOWNLOAD_JOB_ID = 1;

    private final JobScheduler mJobScheduler;
    private final Context mContext;
    private final JobInfo mJob;

    public JobSchedulingBrain(Downloader downloader, Context context) {
        super(downloader);

        mContext = context;
        mJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        mJob = new JobInfo.Builder(
                DOWNLOAD_JOB_ID, new ComponentName(mContext, DownloaderJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build();
    }

    @Override
    public void onComplete(CatalogItem item, int status) {
        super.onComplete(item, status);

        if (status != Downloader.SUCCESS) {
            Log.i(TAG, "Scheduling new job");
            // schedule a job to download the items at a later time
            mJobScheduler.schedule(mJob);
        }
    }
}

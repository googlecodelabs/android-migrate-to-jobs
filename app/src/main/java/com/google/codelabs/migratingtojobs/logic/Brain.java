package com.google.codelabs.migratingtojobs.logic;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.codelabs.migratingtojobs.model.CatalogItem;
import com.google.codelabs.migratingtojobs.model.CatalogItemStore;

import java.util.Timer;
import java.util.TimerTask;

public final class Brain {
    private Timer mTimer;
    private boolean mIsRunning = false;
    private final CatalogItemStore mItemStore;
    private final ConnectivityManager mConnManager;
    private DownloadTask mTask = new DownloadTask();

    public Brain(CatalogItemStore itemStore, ConnectivityManager connManager) {

        mItemStore = itemStore;
        mConnManager = connManager;

        mTimer = new Timer();
    }

    public void didClickOn(CatalogItem item) {
        item.progress.set(0);

        switch (item.status.get()) {
            case CatalogItem.AVAILABLE:
                item.status.set(CatalogItem.UNAVAILABLE);
                break;

            case CatalogItem.DOWNLOADING:
                item.status.set(CatalogItem.UNAVAILABLE);
                break;

            case CatalogItem.UNAVAILABLE:
                item.status.set(CatalogItem.DOWNLOADING);
                break;
        }

        ensureValidTimerState();
    }

    private void ensureValidTimerState() {
        synchronized (mItemStore) {
            boolean shouldBeRunning = shouldTimerBeRunning();

            if (shouldBeRunning && !mIsRunning) {
                startDownloadTask();
            } else if (mIsRunning && !shouldBeRunning) {
                stopDownloadTask();
            }
        }
    }

    private void startDownloadTask() {
        mIsRunning = true;

        mTask = new DownloadTask();
        mTimer.scheduleAtFixedRate(mTask, 0, 100);
    }

    private void stopDownloadTask() {
        mIsRunning = false;

        mTask.cancel();
        mTimer.purge();
        mTimer = new Timer();
    }

    private boolean shouldTimerBeRunning() {
        return mItemStore.getDownloadQueue().size() > 0;
    }

    private class DownloadTask extends TimerTask {
        @Override
        public void run() {
            synchronized (mItemStore) {
                for (CatalogItem ci : mItemStore.getDownloadQueue()) {

                    NetworkInfo networkInfo = mConnManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                        ci.progress.set(ci.progress.get() + 1);
                    }

                    if (ci.progress.get() >= 100) {
                        ci.status.set(CatalogItem.AVAILABLE);
                        ci.progress.set(0);
                    }
                }
            }

            ensureValidTimerState();
        }
    }
}

package com.google.codelabs.migratingtojobs;

import android.net.ConnectivityManager;

import com.google.codelabs.migratingtojobs.model.CatalogItem;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Downloader {
    public static final int FAILURE = 1;
    public static final int SUCCESS = 2;

    private final ExecutorService mExecutorService;
    private final ConnectivityManager mConnManager;

    public Downloader(ExecutorService executorService, ConnectivityManager connManager) {
        mExecutorService = executorService;
        mConnManager = connManager;
    }

    public Future<Integer> start(final CatalogItem item) {
        return mExecutorService.submit(new DownloadRunner(item));
    }

    private class DownloadRunner implements Callable<Integer> {
        private final CatalogItem mItem;

        public DownloadRunner(CatalogItem item) {
            mItem = item;
        }

        @Override
        public Integer call() throws Exception {
            if (!Util.isNetworkActive(Downloader.this.mConnManager)) {
                return FAILURE;
            }

            for (int i = 1 + mItem.progress.get(); i <= 100; i++) {
                mItem.progress.set(i);
                Thread.sleep(100);
            }

            return mItem.progress.get() >= 100 ? SUCCESS : FAILURE;
        }
    }
}

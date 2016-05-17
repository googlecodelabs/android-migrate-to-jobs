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

package com.google.codelabs.migratingtojobs.shared;

import android.net.ConnectivityManager;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Downloader is responsible for simulating download requests. No actual HTTP requests are made.
 */
public class Downloader {
    public static final int SUCCESS = 0;
    public static final int ERROR = 1;

    public static final String TAG = "Downloader";

    /**
     * @see CatalogItem#TOTAL_NUM_CHUNKS
     */
    private static final int MAX_MILLIS_PER_TICK = 28;
    public final EventBus.EventListener eventListener = new DownloaderEventListener();
    private final ExecutorService mExecutorService;
    private final ConnectivityManager mConnManager;
    private final Random mRandom;
    private final EventBus mBus;
    private final SimpleArrayMap<CatalogItem, Future<?>> mMap = new SimpleArrayMap<>();

    @Inject
    public Downloader(@Named("worker") ExecutorService executorService,
                      ConnectivityManager connManager, Random random, EventBus eventBus) {
        mExecutorService = executorService;
        mConnManager = connManager;
        mRandom = random;
        mBus = eventBus;
    }

    /**
     * Start downloading the provided CatalogItem.
     */
    private Future<?> start(final CatalogItem item) {
        synchronized (mMap) {
            Future<?> future = mMap.get(item);
            if (future != null) {
                Log.v(TAG, "found pending future for " + item.getBook().getTitle());
                return future;
            }

            future = mExecutorService.submit(new DownloadRunner(item, mBus, mRandom, mConnManager));
            mMap.put(item, future);
            return future;
        }
    }

    /**
     * Stop downloading the provided CatalogItem.
     */
    private void stop(final CatalogItem item) {
        synchronized (mMap) {
            Future<?> future = mMap.remove(item);
            if (future != null) {
                future.cancel(true);
            }
        }
    }

    private static class DownloadRunner implements Runnable {
        private final CatalogItem item;
        private final EventBus bus;
        private final Random random;
        private final ConnectivityManager connectivityManager;

        public DownloadRunner(CatalogItem item, EventBus bus, Random random,
                              ConnectivityManager connectivityManager) {
            this.item = item;
            this.bus = bus;
            this.random = random;
            this.connectivityManager = connectivityManager;
        }

        public int attemptDownload() throws InterruptedException {
            while (!item.isAvailable()) {
                if (!Util.isNetworkActive(connectivityManager)) {
                    // simulate an error if the network is down
                    return ERROR;
                }

                Thread.sleep(random.nextInt(MAX_MILLIS_PER_TICK));
                bus.postItemDownloadIncrementProgress(item);
            }

            return SUCCESS;
        }

        public void run() {
            Log.i(TAG, String.format(Locale.US, "starting to download \"%s\"",
                    item.getBook().getTitle()));

            try {
                if (attemptDownload() == ERROR) {
                    bus.postItemDownloadFailed(item);
                } else {
                    bus.postItemDownloadFinished(item);
                }
            } catch (InterruptedException e) {
                bus.postItemDownloadInterrupted(item);
            }
        }
    }

    private class DownloaderEventListener extends BaseEventListener {
        @Override
        public void onItemDownloadStarted(CatalogItem item) {
            Log.v(TAG, "starting to download " + item.getBook().getTitle());
            start(item);
        }

        @Override
        public void onItemDownloadCancelled(CatalogItem item) {
            stop(item);
        }

        @Override
        public void onItemDownloadFailed(CatalogItem item) {
            synchronized (mMap) {
                mMap.remove(item);
            }
        }

        @Override
        public void onItemDownloadFinished(CatalogItem item) {
            synchronized (mMap) {
                mMap.remove(item);

                if (mMap.size() == 0) {
                    mBus.postAllDownloadsFinished();
                }
            }
        }

        @Override
        public void onRetryDownloads(CatalogItemStore store) {
            Log.v(TAG, "retrying downloads");

            for (CatalogItem item : store.getDownloadQueue()) {
                Log.v(TAG, "posting start for " + item.getBook().getTitle());
                mBus.postItemDownloadStarted(item);
            }
        }
    }
}

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

package com.google.codelabs.migratingtojobs.common;

import android.net.ConnectivityManager;
import android.support.v4.util.SimpleArrayMap;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Downloader is responsible for simulating download requests. No actual HTTP requests are made.
 */
public class Downloader {
    public static final int FAILURE = 1;
    public static final int SUCCESS = 2;

    private final ExecutorService mExecutorService;
    private final ConnectivityManager mConnManager;
    private final SimpleArrayMap<CatalogItem, Future<Integer>> mMap = new SimpleArrayMap<>();

    public Downloader(ExecutorService executorService, ConnectivityManager connManager) {
        mExecutorService = executorService;
        mConnManager = connManager;
    }

    /** Start downloading the provided CatalogItem. */
    public Future<Integer> start(final CatalogItem item) {
        Future<Integer> future = mMap.get(item);
        if (future != null) {
            return future;
        }

        future = mExecutorService.submit(new DownloadRunner(item));
        mMap.put(item, future);
        return future;
    }

    /** Stop downloading the provided CatalogItem. */
    public void stop(final CatalogItem item) {
        Future<Integer> future = mMap.remove(item);
        if (future != null) {
            future.cancel(true);
        }
    }

    private class DownloadRunner implements Callable<Integer> {
        private final CatalogItem mItem;

        public DownloadRunner(CatalogItem item) {
            mItem = item;
        }

        @Override
        public Integer call() throws Exception {
            try {
                for (int i = 1 + mItem.progress.get(); i <= 100; i++) {
                    // simulate a network failure
                    if (!Util.isNetworkActive(Downloader.this.mConnManager)) {
                        return FAILURE;
                    }

                    mItem.progress.set(i);
                    Thread.sleep(100);
                }

                if (mItem.progress.get() >= 100) {
                    return SUCCESS;
                }

                mItem.status.set(CatalogItem.ERROR);
                return FAILURE;
            } finally {
                mMap.remove(mItem);
            }
        }
    }
}

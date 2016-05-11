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

package com.google.codelabs.migratingtojobs.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.google.codelabs.migratingtojobs.common.App;
import com.google.codelabs.migratingtojobs.common.Downloader;
import com.google.codelabs.migratingtojobs.common.Util;
import com.google.codelabs.migratingtojobs.common.CatalogItem;

import java.util.List;
import java.util.concurrent.Future;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    public static final String TAG = "ConnChangeRec";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (TextUtils.isEmpty(action) || !ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            return;
        }

        Log.i(TAG, "checking network connection");

        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!Util.isNetworkActive(connManager)) {
            return; // nothing we can do here
        }

        App app = (App) context.getApplicationContext();
        List<CatalogItem> downloadQueue = app.getCatalogStore().getDownloadQueue();
        if (downloadQueue == null || downloadQueue.isEmpty()) {
            // nothing to do
            return;
        }

        Downloader downloader = app.getDownloader();
        SimpleArrayMap<CatalogItem, Future<Integer>> pendingResults = new SimpleArrayMap<>(downloadQueue.size());
        for (CatalogItem item : downloadQueue) {
            pendingResults.put(item, downloader.start(item));
        }

        for (int i = 0; i < pendingResults.size(); i++) {
            CatalogItem k = pendingResults.keyAt(i);
            Future<Integer> result = pendingResults.get(k);

            int res = Downloader.FAILURE;
            try {
                if (!result.isCancelled()) {
                    res = result.get();
                }
            } catch (Throwable e) {
                Log.e(TAG, "unexpected error while gathering results", e);
            }

            Log.i(TAG, "got result = " + res);
            if (res == Downloader.SUCCESS) {
                k.status.set(CatalogItem.AVAILABLE);
            }
        }

        app.writeCatalogItems();
    }
}

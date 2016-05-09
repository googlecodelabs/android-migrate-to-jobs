package com.google.codelabs.migratingtojobs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.google.codelabs.migratingtojobs.model.CatalogItem;
import com.google.codelabs.migratingtojobs.model.CatalogItemStore;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
                res = result.get();
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

package com.google.codelabs.migratingtojobs.common;

import android.util.Log;

import java.util.Locale;

public class ErrorLoggingDownloadCallback extends BaseEventListener {
    private static final String TAG = Downloader.TAG;

    @Override
    public void onItemDownloadFailed(CatalogItem item) {
        Log.i(TAG, String.format(Locale.US,
                "Encountered error downloading %s", item.getBook().getTitle()));
    }
}

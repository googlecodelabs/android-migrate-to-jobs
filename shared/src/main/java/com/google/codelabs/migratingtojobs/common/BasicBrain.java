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

import android.util.Log;

import java.util.Locale;

/** BasicBrain is responsible for connecting the UI to the business logic. */
public class BasicBrain implements Downloader.OnCompleteCallback {
    protected static final String TAG = "Brain";

    protected final Downloader mDownloader;

    public BasicBrain(Downloader downloader) {
        mDownloader = downloader;
    }

    public void didClickOn(CatalogItem item) {
        item.progress.set(0);

        switch (item.status.get()) {
            case CatalogItem.AVAILABLE:
                // if it was available, the user would like to delete it
                item.status.set(CatalogItem.UNAVAILABLE);
                break;

            case CatalogItem.DOWNLOADING:
                // if it was downloading, the user would like to cancel the download
                item.status.set(CatalogItem.UNAVAILABLE);
                mDownloader.stop(item);
                break;

            case CatalogItem.ERROR:
                // fallthrough
            case CatalogItem.UNAVAILABLE:
                // if it was unavailable (or error'd), the user would like to (re)download it
                item.status.set(CatalogItem.DOWNLOADING);
                mDownloader.start(item, this);
                break;
        }
    }

    @Override
    public void onComplete(CatalogItem item, int status) {
        Log.i(TAG, String.format(Locale.US,
                "Finished downloading %s with status %d", item.book.get().title, status));
    }
}

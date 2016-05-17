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

import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.util.Log;
import android.widget.ImageButton;

import com.google.codelabs.migratingtojobs.shared.nano.CatalogItemProtos;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CatalogItem extends BaseEventListener implements Observable {
    public final static int AVAILABLE = CatalogItemProtos.CatalogItem.AVAILABLE;
    public final static int UNAVAILABLE = CatalogItemProtos.CatalogItem.UNAVAILABLE;
    public final static int DOWNLOADING = CatalogItemProtos.CatalogItem.DOWNLOADING;
    public final static int ERROR = CatalogItemProtos.CatalogItem.ERROR;
    /**
     * The size, in download "ticks", of this item.
     *
     * @see Downloader#MAX_MILLIS_PER_TICK
     */
    public final static int TOTAL_NUM_CHUNKS = 300;
    private static final String TAG = "CatalogItem";
    private final static String[] STATUS_STRINGS =
            {"UNKNOWN", "AVAILABLE", "UNAVAILABLE", "DOWNLOADING", "ERROR"};
    private final PropertyChangeRegistry mPropertyChangeRegistry = new PropertyChangeRegistry();
    private final CatalogItemProtos.CatalogItem mProto;
    private final Book mBook;

    public CatalogItem(Book book) {
        this(book, 0, UNAVAILABLE);
    }

    public CatalogItem(Book book, @IntRange(from = 0, to = TOTAL_NUM_CHUNKS) int progress,
                       @ItemStatus int status) {
        mBook = book;

        mProto = new CatalogItemProtos.CatalogItem();
        mProto.downloadProgress = progress;
        mProto.status = status;
        mProto.book = book.getProto();
    }

    public CatalogItem(CatalogItemProtos.CatalogItem proto) {
        mProto = proto;
        mBook = new Book(mProto.book);
    }

    @BindingAdapter("catalogIconSource")
    public static void setCatalogIconSource(ImageButton button, int status) {
        switch (status) {
            case AVAILABLE:
                button.setImageResource(R.drawable.ic_delete);
                break;

            case UNAVAILABLE:
                button.setImageResource(R.drawable.ic_download);
                break;

            case DOWNLOADING:
                button.setImageResource(R.drawable.ic_cancel);
                break;

            case ERROR:
                button.setImageResource(R.drawable.ic_error);
                break;

            default:
                button.setImageResource(android.R.drawable.ic_dialog_alert);
                break;
        }
    }

    public CatalogItemProtos.CatalogItem getProto() {
        return mProto;
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mPropertyChangeRegistry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mPropertyChangeRegistry.remove(onPropertyChangedCallback);
    }

    @Bindable
    public int getDownloadProgress() {
        return mProto.downloadProgress;
    }

    private void setDownloadProgress(int progress) {
        mProto.downloadProgress = progress;
        mPropertyChangeRegistry.notifyChange(this, BR.downloadProgress);
    }

    @Bindable
    public Book getBook() {
        return mBook;
    }

    @Bindable
    public int getStatus() {
        return mProto.status;
    }

    private synchronized void setStatus(int newStatus) {
        int oldStatus = mProto.status;
        if ((oldStatus == AVAILABLE && newStatus == UNAVAILABLE)
                || (oldStatus == DOWNLOADING && newStatus == ERROR)
                || (oldStatus == DOWNLOADING && newStatus == AVAILABLE)
                || (oldStatus == DOWNLOADING && newStatus == UNAVAILABLE)
                || (oldStatus == ERROR && newStatus == DOWNLOADING)
                || (oldStatus == UNAVAILABLE && newStatus == DOWNLOADING)
                ) {

            Log.v(TAG,
                    "transitioning from state " + STATUS_STRINGS[oldStatus]
                            + " to " + STATUS_STRINGS[newStatus]);
            mProto.status = newStatus;

            mPropertyChangeRegistry.notifyChange(this, BR.status);
        }
    }

    public boolean isDownloading() {
        return mProto.status == DOWNLOADING;
    }

    public boolean isAvailable() {
        return mProto.status == AVAILABLE;
    }

    public boolean isErroring() {
        return mProto.status == ERROR;
    }

    @Override
    public void onItemDownloadIncrementProgress(CatalogItem item) {
        if (item == this) {
            setDownloadProgress(mProto.downloadProgress + 1);
            if (mProto.downloadProgress >= TOTAL_NUM_CHUNKS) {
                setStatus(AVAILABLE);
            }
        }
    }

    @Override
    public void onItemDownloadInterrupted(CatalogItem item) {
        onItemDeleteLocalCopy(item);
    }

    @Override
    public void onItemDownloadCancelled(CatalogItem item) {
        onItemDeleteLocalCopy(item);
    }

    @Override
    public void onItemDeleteLocalCopy(CatalogItem item) {
        if (item == this) {
            setStatus(UNAVAILABLE);
            setDownloadProgress(0);
        }
    }

    @Override
    public void onItemDownloadFinished(CatalogItem item) {
        if (item == this) {
            item.setStatus(AVAILABLE);
        }
    }

    @Override
    public void onItemDownloadStarted(CatalogItem item) {
        if (item == this) {
            item.setStatus(DOWNLOADING);
        }
    }

    @Override
    public void onItemDownloadFailed(CatalogItem item) {
        if (item == this) {
            item.setStatus(ERROR);
        }
    }

    @IntDef({AVAILABLE, UNAVAILABLE, DOWNLOADING, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    @interface ItemStatus {
    }
}

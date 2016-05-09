package com.google.codelabs.migratingtojobs.model;

import android.databinding.Bindable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;

import com.google.codelabs.migratingtojobs.nano.CatalogItemProtos;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CatalogItem {
    @IntDef({AVAILABLE, UNAVAILABLE, DOWNLOADING})
    @Retention(RetentionPolicy.SOURCE)
    @interface ItemStatus {}

    public final static int AVAILABLE = CatalogItemProtos.CatalogItem.AVAILABLE;
    public final static int UNAVAILABLE = CatalogItemProtos.CatalogItem.UNAVAILABLE;
    public final static int DOWNLOADING = CatalogItemProtos.CatalogItem.DOWNLOADING;

    public final ObservableField<Book> book = new ObservableField<>();

    @IntRange(from = 0, to = 100)
    public final ObservableInt progress = new ObservableInt(0) {
        @Override
        public void set(int value) {
            super.set(value);

            updateStatus();
        }
    };

    @ItemStatus
    public final ObservableInt status = new ObservableInt(UNAVAILABLE);

    private void updateStatus() {
        if (status.get() == DOWNLOADING && progress.get() >= 100) {
            status.set(AVAILABLE);
        }
    }

    public CatalogItem(Book book, @IntRange(from = 0, to = 100) int progress, @ItemStatus int status) {
        this.book.set(book);
        this.progress.set(progress);
        this.status.set(status);
    }

    public CatalogItem(Book book) {
        this(book, 0, UNAVAILABLE);
    }

    public boolean isAvailable() {
        return status.get() == AVAILABLE;
    }

    public boolean isDownloading() {
        return status.get() == DOWNLOADING;
    }
}

package com.google.codelabs.migratingtojobs.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;

public class ApplicationDataStore extends BaseEventListener {
    private final ExecutorService mExecutor;

    private final Runnable mWriterRunnable;

    private final AtomicInteger mNumActivities = new AtomicInteger(0);

    @Inject
    public ApplicationDataStore(@Named("worker") ExecutorService executorService,
                                StoreArchiver archiver, CatalogItemStore catalogItemStore) {

        mExecutor = executorService;
        mWriterRunnable = new WriterRunnable(archiver, catalogItemStore);
    }

    @Override
    public void onActivityCreated() {
        mNumActivities.incrementAndGet();
    }

    @Override
    public void onActivityDestroyed() {
        if (mNumActivities.decrementAndGet() == 0) {
            mExecutor.submit(mWriterRunnable);
        }
    }

    private class WriterRunnable implements Runnable {
        private StoreArchiver mArchiver;
        private CatalogItemStore mCatalogItemStore;

        public WriterRunnable(StoreArchiver archiver, CatalogItemStore itemStore) {
            mArchiver = archiver;
            mCatalogItemStore = itemStore;
        }

        @Override
        public void run() {
            mArchiver.write(mCatalogItemStore);
        }
    }
}

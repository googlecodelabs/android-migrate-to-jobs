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

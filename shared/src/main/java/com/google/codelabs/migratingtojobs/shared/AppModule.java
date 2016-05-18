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

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.HandlerThread;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private static final String PREFERENCES_NAME = "prefs";

    private Context mContext;

    public AppModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    public Context provideApplication() {
        return mContext;
    }

    @Singleton
    @Provides
    public SharedPreferences provideSharedPreferences() {
        return mContext.getSharedPreferences(PREFERENCES_NAME, 0);
    }

    @Provides
    @Singleton
    public CatalogItemStore provideCatalogItemStore(StoreArchiver archiver) {
        return archiver.read();
    }

    @Provides
    @Singleton
    public ApplicationDataStore provideApplicationDataStore(
            @Named("worker") ExecutorService executorService, StoreArchiver archiver,
            CatalogItemStore catalogItemStore) {

        return new ApplicationDataStore(executorService, archiver, catalogItemStore);
    }

    @Provides
    @Named("worker")
    @Singleton
    public ExecutorService provideWorkerExecutorService(
            @Named("worker") ThreadFactory threadFactory) {

        return Executors.newFixedThreadPool(3, threadFactory);
    }

    @Provides
    @Named("worker")
    public ThreadFactory provideWorkerThreadFactory() {
        return new PriorityThreadFactory(Thread.MIN_PRIORITY);
    }

    @Provides
    @Singleton
    public ConnectivityManager provideConnectivityManager() {
        return (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    public Random provideRandom() {
        return new Random();
    }

    @Provides
    @Singleton
    public EventBus provideEventBus() {
        HandlerThread ht = new HandlerThread("eventbus");
        ht.setPriority(Thread.NORM_PRIORITY - 1);
        ht.start();
        return new EventBus(ht.getLooper());
    }
}

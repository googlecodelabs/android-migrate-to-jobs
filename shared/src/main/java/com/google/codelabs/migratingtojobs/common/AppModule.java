package com.google.codelabs.migratingtojobs.common;

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

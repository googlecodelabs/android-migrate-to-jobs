package com.google.codelabs.migratingtojobs.original_app;

import android.content.Context;

import com.google.codelabs.migratingtojobs.common.AppModule;
import com.google.codelabs.migratingtojobs.common.ErrorLoggingDownloadCallback;
import com.google.codelabs.migratingtojobs.common.EventBus;
import com.google.codelabs.migratingtojobs.common.SharedInitializer;

import javax.inject.Inject;

public class OriginalGlobalState {
    private static OriginalGlobalState sInstance;

    public static OriginalComponent get(Context app) {
        if (sInstance == null) {
            synchronized (SharedInitializer.class) {
                if (sInstance == null) {
                    sInstance = new OriginalGlobalState(app);

                    sInstance.init();
                }
            }
        }

        return sInstance.get();
    }

    private final OriginalComponent component;

    @Inject
    SharedInitializer sharedInitializer;
    @Inject
    EventBus bus;

    public OriginalGlobalState(Context app) {
        component = DaggerOriginalComponent.builder()
                .appModule(new AppModule(app))
                .build();

        component.inject(this);
    }

    private void init() {
        sharedInitializer.init();

        bus.register(new ErrorLoggingDownloadCallback());
    }

    public OriginalComponent get() {
        return component;
    }
}

package com.google.codelabs.migratingtojobs.original_app;

import android.content.Context;

import com.google.codelabs.migratingtojobs.shared.AppModule;
import com.google.codelabs.migratingtojobs.shared.ErrorLoggingDownloadCallback;
import com.google.codelabs.migratingtojobs.shared.EventBus;
import com.google.codelabs.migratingtojobs.shared.SharedInitializer;

import javax.inject.Inject;

public class OriginalGlobalState {
    private static OriginalGlobalState sInstance;
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

    private void init() {
        sharedInitializer.init();

        bus.register(new ErrorLoggingDownloadCallback());
    }

    public OriginalComponent get() {
        return component;
    }
}

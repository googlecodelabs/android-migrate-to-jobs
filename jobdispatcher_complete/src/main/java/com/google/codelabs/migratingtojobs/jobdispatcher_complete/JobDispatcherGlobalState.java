package com.google.codelabs.migratingtojobs.jobdispatcher_complete;

import android.app.Application;

import com.google.codelabs.migratingtojobs.shared.AppModule;
import com.google.codelabs.migratingtojobs.shared.EventBus;
import com.google.codelabs.migratingtojobs.shared.SharedInitializer;

import javax.inject.Inject;

public class JobDispatcherGlobalState {
    private static JobDispatcherGlobalState sInstance;

    public static JobDispatcherComponent get(Application app) {
        if (sInstance == null) {
            synchronized (JobDispatcherGlobalState.class) {
                if (sInstance == null) {
                    sInstance = new JobDispatcherGlobalState(app);

                    sInstance.init();
                }
            }
        }

        return sInstance.get();
    }

    private final JobDispatcherComponent mComponent;

    @Inject
    SharedInitializer mSharedInitializer;

    @Inject
    EventBus mBus;

    @Inject
    JobDispatchingErrorListener mJobDispatchingErrorListener;

    public JobDispatcherGlobalState(Application app) {
        mComponent = DaggerJobDispatcherComponent.builder()
                .appModule(new AppModule(app))
                .build();

        mComponent.inject(this);
    }

    private void init() {
        mSharedInitializer.init();

        mBus.register(mJobDispatchingErrorListener);
    }

    public JobDispatcherComponent get() {
        return mComponent;
    }
}

package com.google.codelabs.migratingtojobs.jobdownloads;

import android.app.Application;

import com.google.codelabs.migratingtojobs.common.AppModule;
import com.google.codelabs.migratingtojobs.common.EventBus;
import com.google.codelabs.migratingtojobs.common.SharedInitializer;

import javax.inject.Inject;

public class JobSchedulerGlobalState {
    private static JobSchedulerGlobalState sInstance;
    private final JobSchedulerComponent mComponent;
    @Inject
    SharedInitializer mSharedInitializer;
    @Inject
    EventBus mBus;
    @Inject
    JobSchedulingErrorListener mJobSchedulingErrorListener;

    public JobSchedulerGlobalState(Application app) {
        mComponent = DaggerJobSchedulerComponent.builder()
                .appModule(new AppModule(app))
                .build();

        mComponent.inject(this);
    }

    public static JobSchedulerComponent get(Application app) {
        if (sInstance == null) {
            synchronized (SharedInitializer.class) {
                if (sInstance == null) {
                    sInstance = new JobSchedulerGlobalState(app);

                    sInstance.init();
                }
            }
        }

        return sInstance.get();
    }

    private void init() {
        mSharedInitializer.init();

        mBus.register(mJobSchedulingErrorListener);
    }

    public JobSchedulerComponent get() {
        return mComponent;
    }
}

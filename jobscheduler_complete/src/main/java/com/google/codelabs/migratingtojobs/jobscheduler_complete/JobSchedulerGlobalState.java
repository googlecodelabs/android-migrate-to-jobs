package com.google.codelabs.migratingtojobs.jobscheduler_complete;

import android.app.Application;

import com.google.codelabs.migratingtojobs.shared.AppModule;
import com.google.codelabs.migratingtojobs.shared.EventBus;
import com.google.codelabs.migratingtojobs.shared.SharedInitializer;

import javax.inject.Inject;

public class JobSchedulerGlobalState {
    private static JobSchedulerGlobalState sInstance;

    public static JobSchedulerComponent get(Application app) {
        if (sInstance == null) {
            synchronized (JobSchedulerGlobalState.class) {
                if (sInstance == null) {
                    sInstance = new JobSchedulerGlobalState(app);

                    sInstance.init();
                }
            }
        }

        return sInstance.get();
    }

    private final JobSchedulerComponent component;
    @Inject
    SharedInitializer mSharedInitializer;
    @Inject
    EventBus bus;
    @Inject
    JobSchedulingErrorListener jobSchedulingErrorListener;

    public JobSchedulerGlobalState(Application app) {
        component = DaggerJobSchedulerComponent.builder()
                .appModule(new AppModule(app))
                .build();

        component.inject(this);
    }

    private void init() {
        mSharedInitializer.init();

        bus.register(jobSchedulingErrorListener);
    }

    public JobSchedulerComponent get() {
        return component;
    }
}

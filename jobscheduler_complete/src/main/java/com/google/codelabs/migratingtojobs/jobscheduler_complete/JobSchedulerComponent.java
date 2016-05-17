package com.google.codelabs.migratingtojobs.jobscheduler_complete;

import com.google.codelabs.migratingtojobs.shared.AppModule;
import com.google.codelabs.migratingtojobs.shared.RootComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface JobSchedulerComponent extends RootComponent {
    void inject(JobSchedulerGlobalState jobSchedulerGlobalState);

    void inject(JobSchedulingCatalogListActivity activity);

    void inject(DownloaderJobService jobService);
}

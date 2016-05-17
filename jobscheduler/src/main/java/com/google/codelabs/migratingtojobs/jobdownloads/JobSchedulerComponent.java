package com.google.codelabs.migratingtojobs.jobdownloads;

import com.google.codelabs.migratingtojobs.common.AppModule;
import com.google.codelabs.migratingtojobs.common.RootComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface JobSchedulerComponent extends RootComponent {
    void inject(JobSchedulerGlobalState jobSchedulerGlobalState);

    void inject(JobSchedulingCatalogListActivity activity);

    void inject(DownloaderJobService jobService);
}

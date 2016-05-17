package com.google.codelabs.migratingtojobs.jobdispatcher_complete;

import com.google.codelabs.migratingtojobs.shared.AppModule;
import com.google.codelabs.migratingtojobs.shared.RootComponent;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class, JobDispatcherModule.class})
public interface JobDispatcherComponent extends RootComponent {
    void inject(JobDispatcherGlobalState jobDispatcherGlobalState);
    void inject(JobDispatchingCatalogListActivity activity);
    void inject(DownloaderJobService downloaderJobService);
}

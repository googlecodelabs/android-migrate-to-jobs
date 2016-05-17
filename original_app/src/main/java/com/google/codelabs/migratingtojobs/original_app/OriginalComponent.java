package com.google.codelabs.migratingtojobs.original_app;

import com.google.codelabs.migratingtojobs.common.AppModule;
import com.google.codelabs.migratingtojobs.common.RootComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface OriginalComponent extends RootComponent {
    void inject(OriginalGlobalState originalGlobalState);

    void inject(ErrorLoggingCatalogListActivity activity);

    void inject(ConnectivityChangeReceiver receiver);
}

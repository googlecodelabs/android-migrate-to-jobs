package com.google.codelabs.migratingtojobs.original_app;

import com.google.codelabs.migratingtojobs.shared.AppModule;
import com.google.codelabs.migratingtojobs.shared.RootComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface OriginalComponent extends RootComponent {
    void inject(OriginalGlobalState originalGlobalState);

    void inject(ErrorLoggingCatalogListActivity activity);

    void inject(ConnectivityChangeReceiver receiver);
}

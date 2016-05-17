package com.google.codelabs.migratingtojobs.shared;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface RootComponent {
    void inject(SharedInitializer baseGlobalState);

    void inject(CatalogListActivity activity);
}

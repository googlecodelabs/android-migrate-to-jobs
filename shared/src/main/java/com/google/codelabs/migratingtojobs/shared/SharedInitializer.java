package com.google.codelabs.migratingtojobs.shared;

import javax.inject.Inject;

public class SharedInitializer {
    protected final EventBus bus;
    protected final CatalogItemStore itemStore;
    protected final Downloader downloader;
    protected final ApplicationDataStore appDataStore;

    @Inject
    SharedInitializer(EventBus bus, CatalogItemStore itemStore, Downloader downloader,
                      ApplicationDataStore appDataStore) {
        this.bus = bus;
        this.itemStore = itemStore;
        this.downloader = downloader;
        this.appDataStore = appDataStore;
    }

    public void init() {
        bus.register(appDataStore);
        bus.register(downloader.eventListener);

        for (int i = 0; i < itemStore.size(); i++) {
            bus.register(itemStore.get(i));
        }
    }
}

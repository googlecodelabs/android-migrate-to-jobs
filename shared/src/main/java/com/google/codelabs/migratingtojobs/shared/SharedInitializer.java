// Copyright 2016 Google, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

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

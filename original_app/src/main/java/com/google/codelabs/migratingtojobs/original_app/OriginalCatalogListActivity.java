package com.google.codelabs.migratingtojobs.original_app;

import com.google.codelabs.migratingtojobs.shared.CatalogListActivity;

public class OriginalCatalogListActivity extends CatalogListActivity {
    @Override
    protected void inject() {
        OriginalGlobalState.get(getApplication()).inject(this);
    }
}

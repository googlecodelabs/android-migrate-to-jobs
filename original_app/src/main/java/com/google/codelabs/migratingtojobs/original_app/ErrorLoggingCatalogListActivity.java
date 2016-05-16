package com.google.codelabs.migratingtojobs.original_app;

import com.google.codelabs.migratingtojobs.common.CatalogListActivity;

import javax.inject.Inject;

public class ErrorLoggingCatalogListActivity extends CatalogListActivity {
    @Override
    protected void inject() {
        OriginalGlobalState.get(getApplication()).inject(this);
    }
}

package com.google.codelabs.migratingtojobs.jobdownloads;

import com.google.codelabs.migratingtojobs.common.CatalogListActivity;

public class JobSchedulingCatalogListActivity extends CatalogListActivity {
    @Override
    protected void inject() {
        // sets all our package-specific global state up and provides superclass dependencies
        JobSchedulerGlobalState.get(getApplication()).inject(this);
    }
}

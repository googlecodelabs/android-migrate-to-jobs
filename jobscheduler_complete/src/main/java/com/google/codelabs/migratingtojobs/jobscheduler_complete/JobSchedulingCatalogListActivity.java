package com.google.codelabs.migratingtojobs.jobscheduler_complete;

import com.google.codelabs.migratingtojobs.shared.CatalogListActivity;

public class JobSchedulingCatalogListActivity extends CatalogListActivity {
    @Override
    protected void inject() {
        // sets all our package-specific global state up and provides superclass dependencies
        JobSchedulerGlobalState.get(getApplication()).inject(this);
    }
}

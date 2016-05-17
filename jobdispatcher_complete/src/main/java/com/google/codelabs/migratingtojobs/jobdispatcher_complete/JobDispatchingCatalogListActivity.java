package com.google.codelabs.migratingtojobs.jobdispatcher_complete;

import com.google.codelabs.migratingtojobs.shared.CatalogListActivity;

public class JobDispatchingCatalogListActivity extends CatalogListActivity {
  @Override
  protected void inject() {
      JobDispatcherGlobalState.get(getApplication()).inject(this);
  }
}

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

package com.google.codelabs.migratingtojobs.jobscheduler_complete;

import com.google.codelabs.migratingtojobs.shared.CatalogListActivity;

public class JobSchedulingCatalogListActivity extends CatalogListActivity {
    @Override
    protected void inject() {
        // sets all our package-specific global state up and provides superclass dependencies
        JobSchedulerGlobalState.get(getApplication()).inject(this);
    }
}

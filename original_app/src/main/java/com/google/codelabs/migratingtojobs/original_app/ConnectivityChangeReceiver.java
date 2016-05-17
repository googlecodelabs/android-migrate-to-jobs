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

package com.google.codelabs.migratingtojobs.original_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.google.codelabs.migratingtojobs.shared.CatalogItemStore;
import com.google.codelabs.migratingtojobs.shared.EventBus;

import javax.inject.Inject;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    @Inject
    EventBus bus;
    @Inject
    ConnectivityManager connManager;
    @Inject
    CatalogItemStore itemStore;
    private boolean mIsInitialized;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (TextUtils.isEmpty(action) || !ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            return;
        }

        injectOnce(context);
        bus.postRetryDownloads(itemStore);
    }

    private void injectOnce(Context context) {
        if (!mIsInitialized) {
            synchronized (this) {
                if (!mIsInitialized) {
                    OriginalGlobalState.get(context.getApplicationContext()).inject(this);
                    mIsInitialized = true;
                }
            }
        }
    }
}

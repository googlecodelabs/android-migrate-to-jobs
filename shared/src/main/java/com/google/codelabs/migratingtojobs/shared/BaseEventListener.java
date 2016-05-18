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

import android.os.Message;

public class BaseEventListener implements EventBus.EventListener {
    @Override
    public void handle(Message msg) {
    }

    @Override
    public void onInit(CatalogItemStore itemStore) {
    }

    @Override
    public void onActivityCreated() {
    }

    @Override
    public void onActivityDestroyed() {
    }

    @Override
    public void onItemDownloadFailed(CatalogItem item) {
    }

    @Override
    public void onItemDownloadStarted(CatalogItem item) {
    }

    @Override
    public void onItemDownloadFinished(CatalogItem item) {
    }

    @Override
    public void onItemDownloadCancelled(CatalogItem item) {
    }

    @Override
    public void onItemDownloadInterrupted(CatalogItem item) {
    }

    @Override
    public void onItemDownloadIncrementProgress(CatalogItem item) {
    }

    @Override
    public void onItemDeleteLocalCopy(CatalogItem item) {
    }

    @Override
    public void onAllDownloadsFinished() {
    }

    @Override
    public void onRetryDownloads(CatalogItemStore itemStore) {
    }
}

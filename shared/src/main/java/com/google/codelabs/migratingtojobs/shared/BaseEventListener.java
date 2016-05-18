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

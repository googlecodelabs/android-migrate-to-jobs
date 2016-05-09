package com.google.codelabs.migratingtojobs.common;

/** Brain is responsible for connecting the UI to the business logic. */
public final class Brain {
    private final Downloader mDownloader;

    public Brain(Downloader downloader) {
        mDownloader = downloader;
    }

    public void didClickOn(CatalogItem item) {
        item.progress.set(0);

        switch (item.status.get()) {
            case CatalogItem.AVAILABLE:
                // if it was available, the user would like to delete it
                item.status.set(CatalogItem.UNAVAILABLE);
                break;

            case CatalogItem.DOWNLOADING:
                // if it was downloading, the user would like to cancel the download
                item.status.set(CatalogItem.UNAVAILABLE);
                mDownloader.stop(item);
                break;

            case CatalogItem.ERROR:
                // fallthrough
            case CatalogItem.UNAVAILABLE:
                // if it was unavailable (or error'd), the user would like to (re)download it
                item.status.set(CatalogItem.DOWNLOADING);
                mDownloader.start(item);
                break;
        }
    }
}

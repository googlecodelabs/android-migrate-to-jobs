package com.google.codelabs.migratingtojobs.common;

public class Events {
    public static class InitEvent {
        public final CatalogItemStore itemStore;

        public InitEvent(CatalogItemStore itemStore) {
            this.itemStore = itemStore;
        }
    }

    public static class ActivityCreatedEvent {}
    public static class ActivityDestroyedEvent {}

    public static class DownloadErrorEvent extends CatalogItemEvent {
        public DownloadErrorEvent(CatalogItem item) {
            super(item);
        }
    }

    public static class DownloadStartEvent extends CatalogItemEvent {
        public DownloadStartEvent(CatalogItem item) {
            super(item);
        }
    }

    public static class DownloadFinishedEvent extends CatalogItemEvent {
        public DownloadFinishedEvent(CatalogItem item) {
            super(item);
        }
    }

    public static class DownloadCancelledEvent extends CatalogItemEvent {
        public DownloadCancelledEvent(CatalogItem item) {
            super(item);
        }
    }

    static class DownloadInterruptedEvent extends CatalogItemEvent {
        public DownloadInterruptedEvent(CatalogItem item) {
            super(item);
        }
    }

    static class IncrementDownloadProgressEvent extends CatalogItemEvent {
        public IncrementDownloadProgressEvent(CatalogItem item) {
            super(item);
        }
    }

    public static class DeleteLocalCopyEvent extends CatalogItemEvent{
        public DeleteLocalCopyEvent(CatalogItem item) {
            super(item);
        }
    }

    abstract static class CatalogItemEvent {
        public final CatalogItem item;

        public CatalogItemEvent(CatalogItem item) {
            this.item = item;
        }
    }
}

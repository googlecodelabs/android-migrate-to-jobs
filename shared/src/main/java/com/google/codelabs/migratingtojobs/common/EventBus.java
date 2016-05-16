package com.google.codelabs.migratingtojobs.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.LinkedList;
import java.util.List;

public class EventBus {
    public interface EventListener {
        void handle(int what);

        void onInit(CatalogItemStore itemStore);

        void onActivityCreated();

        void onActivityDestroyed();

        void onItemDownloadFailed(CatalogItem item);

        void onItemDownloadStarted(CatalogItem item);

        void onItemDownloadFinished(CatalogItem item);

        void onItemDownloadCancelled(CatalogItem item);

        void onItemDownloadInterrupted(CatalogItem item);

        void onItemDownloadIncrementProgress(CatalogItem item);

        void onItemDeleteLocalCopy(CatalogItem item);

        void onAllDownloadsFinished();

        void onRetryDownloads(CatalogItemStore itemStore);
    }

    private static final int INIT = 1;
    private static final int ACTIVITY_CREATED = 1 << 1;
    private static final int ACTIVITY_DESTROYED = 1 << 2;
    private static final int ITEM_DOWNLOAD_FAILED = 1 << 3;
    private static final int ITEM_DOWNLOAD_STARTED = 1 << 4;
    private static final int ITEM_DOWNLOAD_FINISHED = 1 << 5;
    private static final int ITEM_DOWNLOAD_CANCELLED = 1 << 6;
    private static final int ITEM_DOWNLOAD_INTERRUPTED = 1 << 7;
    private static final int ITEM_DOWNLOAD_INCREMENT_PROGRESS = 1 << 8;
    private static final int ITEM_DELETE_LOCAL_COPY = 1 << 9;
    private static final int REGISTER = 1 << 10;
    private static final int UNREGISTER = 1 << 11;
    private static final int ALL_DOWNLOADS_FINISHED = 1 << 12;
    private static final int RETRY_DOWNLOADS = 1 << 13;

    public static final int FIRST_UNUSED = 14;

    private final EventBusHandler handler;

    public EventBus(Looper looper) {
        handler = getEventBusHandler(looper);
    }

    @NonNull
    protected EventBusHandler getEventBusHandler(Looper looper) {
        return new EventBusHandler(looper);
    }

    public void send(int what) {
        handler.sendEmptyMessage(what);
    }

    public void send(int what, Object arg) {
        handler.obtainMessage(what, arg).sendToTarget();
    }

    public void register(EventListener eventListener) {
        send(REGISTER, eventListener);
    }

    public void unregister(EventListener eventListener) {
        send(UNREGISTER, eventListener);
    }

    public void postInit(CatalogItemStore store) {
        send(INIT, store);
    }

    public void postActivityCreated() {
        send(ACTIVITY_CREATED);
    }

    public void postActivityDestroyed() {
        send(ACTIVITY_DESTROYED);
    }

    public void postItemDownloadFailed(CatalogItem item) {
        send(ITEM_DOWNLOAD_FAILED, item);
    }

    public void postItemDownloadStarted(CatalogItem item) {
        send(ITEM_DOWNLOAD_STARTED, item);
    }

    public void postItemDownloadFinished(CatalogItem item) {
        send(ITEM_DOWNLOAD_FINISHED, item);
    }

    public void postItemDownloadCancelled(CatalogItem item) {
        send(ITEM_DOWNLOAD_CANCELLED, item);
    }

    public void postItemDownloadInterrupted(CatalogItem item) {
        send(ITEM_DOWNLOAD_INTERRUPTED, item);
    }

    public void postItemDownloadIncrementProgress(CatalogItem item) {
        send(ITEM_DOWNLOAD_INCREMENT_PROGRESS, item);
    }

    public void postRetryDownloads(CatalogItemStore itemStore) {
        send(RETRY_DOWNLOADS, itemStore);
    }

    public void postAllDownloadsFinished() {
        send(ALL_DOWNLOADS_FINISHED);
    }

    public void postItemDeleteLocalCopy(CatalogItem item) {
        send(ITEM_DELETE_LOCAL_COPY, item);
    }

    public static class MultiplexingEventListener implements EventListener {
        protected final List<EventListener> listeners = new LinkedList<>();

        void register(EventListener listener) {
            synchronized (listeners) {
                listeners.add(listener);
            }
        }

        void unregister(EventListener listener) {
            synchronized (listeners) {
                listeners.remove(listener);
            }
        }

        @Override
        public void handle(int what) {
            synchronized (listeners) {
                for (EventListener listener : listeners) {
                    listener.handle(what);
                }
            }
        }

        @Override
        public void onInit(CatalogItemStore itemStore) {
            synchronized (listeners) {
                for (EventListener listener : listeners) {
                    listener.onInit(itemStore);
                }
            }
        }

        @Override
        public void onActivityCreated() {
            synchronized (listeners) {
                for (EventListener listener : listeners) {
                    listener.onActivityCreated();
                }
            }
        }

        @Override
        public void onActivityDestroyed() {
            synchronized (listeners) {
                for (EventListener listener : listeners) {
                    listener.onActivityDestroyed();
                }
            }
        }

        @Override
        public void onItemDownloadFailed(CatalogItem item) {
            synchronized (listeners) {
                for (EventListener listener : listeners) {
                    listener.onItemDownloadFailed(item);
                }
            }
        }

        @Override
        public void onItemDownloadStarted(CatalogItem item) {
            synchronized (listeners) {
                for (EventListener listener : listeners) {
                    listener.onItemDownloadStarted(item);
                }
            }
        }

        @Override
        public void onItemDownloadFinished(CatalogItem item) {
            synchronized (listeners) {
                for (EventListener listener : listeners) {
                    listener.onItemDownloadFinished(item);
                }
            }
        }

        @Override
        public void onItemDownloadCancelled(CatalogItem item) {
            synchronized (listeners) {
                for (EventListener listener : listeners) {
                    listener.onItemDownloadCancelled(item);
                }
            }
        }

        @Override
        public void onItemDownloadInterrupted(CatalogItem item) {
            synchronized (listeners) {
                for (EventListener listener : listeners) {
                    listener.onItemDownloadInterrupted(item);
                }
            }
        }

        @Override
        public void onItemDownloadIncrementProgress(CatalogItem item) {
            synchronized (listeners) {
                for (EventListener listener : listeners) {
                    listener.onItemDownloadIncrementProgress(item);
                }
            }
        }

        @Override
        public void onItemDeleteLocalCopy(CatalogItem item) {
            synchronized (listeners) {
                for (EventListener listener : listeners) {
                    listener.onItemDeleteLocalCopy(item);
                }
            }
        }

        @Override
        public void onAllDownloadsFinished() {
            synchronized (listeners) {
                for (EventListener listener : listeners) {
                    listener.onAllDownloadsFinished();
                }
            }
        }

        @Override
        public void onRetryDownloads(CatalogItemStore itemStore) {
            synchronized (listeners) {
                for (EventListener listener : listeners) {
                    listener.onRetryDownloads(itemStore);
                }
            }
        }
    }

    protected static class EventBusHandler extends Handler {
        protected final MultiplexingEventListener listener;

        public EventBusHandler(Looper looper) {
            super(looper);

            listener = getMultiplexingEventListener();
        }

        @NonNull
        protected MultiplexingEventListener getMultiplexingEventListener() {
            return new MultiplexingEventListener();
        }

        private void logHandleMessage(String what) {
            Log.v("EventBus", "received " + what);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT:
                    logHandleMessage("INIT");
                    listener.onInit((CatalogItemStore) msg.obj);
                    break;

                case ACTIVITY_CREATED:
                    logHandleMessage("ACTIVITY_CREATED");
                    listener.onActivityCreated();
                    break;

                case ACTIVITY_DESTROYED:
                    logHandleMessage("ACTIVITY_DESTROYED");
                    listener.onActivityDestroyed();
                    break;

                case ITEM_DOWNLOAD_FAILED:
                    logHandleMessage("ITEM_DOWNLOAD_FAILED");
                    listener.onItemDownloadFailed((CatalogItem) msg.obj);
                    break;

                case ITEM_DOWNLOAD_STARTED:
                    logHandleMessage("ITEM_DOWNLOAD_STARTED");
                    listener.onItemDownloadStarted((CatalogItem) msg.obj);
                    break;

                case ITEM_DOWNLOAD_FINISHED:
                    logHandleMessage("ITEM_DOWNLOAD_FINISHED");
                    listener.onItemDownloadFinished((CatalogItem) msg.obj);
                    break;

                case ITEM_DOWNLOAD_CANCELLED:
                    logHandleMessage("ITEM_DOWNLOAD_CANCELLED");
                    listener.onItemDownloadCancelled((CatalogItem) msg.obj);
                    break;

                case ITEM_DOWNLOAD_INTERRUPTED:
                    logHandleMessage("ITEM_DOWNLOAD_INTERRUPTED");
                    listener.onItemDownloadInterrupted((CatalogItem) msg.obj);
                    break;

                case ITEM_DOWNLOAD_INCREMENT_PROGRESS:
                    // Don't log, too spammy
                    listener.onItemDownloadIncrementProgress((CatalogItem) msg.obj);
                    break;

                case ITEM_DELETE_LOCAL_COPY:
                    logHandleMessage("ITEM_DELETE_LOCAL_COPY");
                    listener.onItemDeleteLocalCopy((CatalogItem) msg.obj);
                    break;

                case REGISTER:
                    logHandleMessage("REGISTER");
                    listener.register((EventListener) msg.obj);
                    break;

                case ALL_DOWNLOADS_FINISHED:
                    logHandleMessage("ALL_DOWNLOADS_FINISHED");
                    listener.onAllDownloadsFinished();
                    break;

                case UNREGISTER:
                    logHandleMessage("UNREGISTER");
                    listener.unregister((EventListener) msg.obj);
                    break;

                case RETRY_DOWNLOADS:
                    logHandleMessage("RETRY_DOWNLOADS");
                    listener.onRetryDownloads((CatalogItemStore) msg.obj);
                    break;

                default:
                    listener.handle(msg.what);
                    break;
            }
        }
    }
}

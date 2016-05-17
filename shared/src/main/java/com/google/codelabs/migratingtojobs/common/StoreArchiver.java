package com.google.codelabs.migratingtojobs.common;

import android.content.SharedPreferences;
import android.support.annotation.WorkerThread;
import android.util.Base64;
import android.util.Log;

import com.google.codelabs.migratingtojobs.common.nano.CatalogItemProtos;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;

import javax.inject.Inject;

public final class StoreArchiver {
    private static final String STORE_KEY = "key";
    private static final String TAG = "AppModule";

    private final static CatalogItem[] DEFAULT_BOOKS = {
            new CatalogItem(
                    new Book("Les Trois Mousquetaires", "Alexandre Dumas"),
                    100,
                    CatalogItem.AVAILABLE
            ),
            new CatalogItem(
                    new Book("Treasure Island", "Robert Louis Stevenson"),
                    100,
                    CatalogItem.AVAILABLE
            ),
            new CatalogItem(
                    new Book("Don Quixote", "Miguel de Cervantes Saavedra"),
                    0,
                    CatalogItem.UNAVAILABLE
            ),
            new CatalogItem(
                    new Book("The Dunwich Horror", "H. P. Lovecraft"),
                    0,
                    CatalogItem.UNAVAILABLE
            ),
            new CatalogItem(
                    new Book("Ulysses", "James Joyce"),
                    0,
                    CatalogItem.UNAVAILABLE
            )};

    private final SharedPreferences mSharedPreferences;

    @Inject
    public StoreArchiver(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    @WorkerThread
    public CatalogItemStore read() {
        String storeValue = mSharedPreferences.getString(STORE_KEY, "");
        if ("".equals(storeValue)) {
            Log.e(TAG, "No saved value in SharedPrefs, using default books");
            return new CatalogItemStore(DEFAULT_BOOKS);
        }

        try {
            return new CatalogItemStore(
                    CatalogItemProtos.CatalogItemStore.parseFrom(
                            Base64.decode(storeValue, Base64.NO_WRAP)));
        } catch (InvalidProtocolBufferNanoException e) {
            Log.e(TAG, "Unable to parse serialized items", e);
        }

        return new CatalogItemStore(DEFAULT_BOOKS);
    }

    @WorkerThread
    public void write(CatalogItemStore store) {
        mSharedPreferences
                .edit()
                .putString(
                        STORE_KEY,
                        Base64.encodeToString(store.toProtoBytes(), Base64.NO_WRAP))
                .apply();
    }
}

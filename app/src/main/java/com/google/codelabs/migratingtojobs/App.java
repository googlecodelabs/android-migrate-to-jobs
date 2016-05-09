package com.google.codelabs.migratingtojobs;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Base64;
import android.util.Log;

import com.google.codelabs.migratingtojobs.logic.Brain;
import com.google.codelabs.migratingtojobs.model.Book;
import com.google.codelabs.migratingtojobs.model.CatalogItem;
import com.google.codelabs.migratingtojobs.model.CatalogItemStore;
import com.google.codelabs.migratingtojobs.nano.CatalogItemProtos;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class App extends Application {
    private static final String PREFERENCES_NAME = "prefs";
    private static final String STORE_KEY = "key";
    public static final String TAG = "App";
    private Brain mBrain;

    private Downloader mDownloader;
    private ConnectivityManager mConnManager;
    private final List<CatalogItem> defaultBooks = Arrays.asList(
            new CatalogItem(
                    new Book("Abarat", "Clive Barker"),
                    100,
                    CatalogItem.AVAILABLE
            ),
            new CatalogItem(
                    new Book("Harry Potter", "J.K. Rowling"),
                    23,
                    CatalogItem.DOWNLOADING
            ),
            new CatalogItem(
                    new Book("The Gunslinger", "Stephen King"),
                    0,
                    CatalogItem.UNAVAILABLE
            ));

    private CatalogItemStore mCatalogItems;

    @Override
    public void onCreate() {
        super.onCreate();

        mCatalogItems = readCatalogItems(getSharedPreferences(PREFERENCES_NAME, 0));
        mConnManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        mDownloader = new Downloader(Executors.newCachedThreadPool(), mConnManager);
        mBrain = new Brain(mCatalogItems, mConnManager);
    }

    @Override
    public void onTerminate() {
        Log.i(TAG, "onTerminate called");
        writeCatalogItems();

        super.onTerminate();
    }


    public void writeCatalogItems() {
        Log.i(TAG, "writing catalog items");
        getSharedPreferences(PREFERENCES_NAME, 0)
                .edit()
                .putString(
                        STORE_KEY,
                        Base64.encodeToString(mCatalogItems.toProtoBytes(), Base64.NO_WRAP))
                .apply();
    }

    private CatalogItemStore readCatalogItems(SharedPreferences preferences) {
        String storeValue = preferences.getString(STORE_KEY, "");
        if ("".equals(storeValue)) {
            Log.e("App", "No saved value in SharedPrefs, using default books");
            return new CatalogItemStore(defaultBooks);
        }

        try {
            return new CatalogItemStore(
                    CatalogItemProtos.CatalogItemStore.parseFrom(
                            Base64.decode(storeValue, Base64.NO_WRAP)));
        } catch (InvalidProtocolBufferNanoException e) {
            Log.e("App", "Unable to parse serialized items", e);
        }

        return new CatalogItemStore(defaultBooks);
    }

    public Brain getBrain() {
        return mBrain;
    }

    public CatalogItemStore getCatalogStore() {
        return mCatalogItems;
    }

    public Downloader getDownloader() {
        return mDownloader;
    }
}

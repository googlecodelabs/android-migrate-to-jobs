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

import android.content.SharedPreferences;
import android.support.annotation.WorkerThread;
import android.util.Base64;
import android.util.Log;

import com.google.codelabs.migratingtojobs.shared.nano.CatalogItemProtos;
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
            CatalogItemProtos.CatalogItemStore store = CatalogItemProtos.CatalogItemStore.parseFrom(
                    Base64.decode(storeValue, Base64.NO_WRAP));

            new CatalogItemStore((store));
            return new CatalogItemStore(store);
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

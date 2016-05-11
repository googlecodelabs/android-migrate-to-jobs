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

package com.google.codelabs.migratingtojobs.common;

import android.support.annotation.NonNull;

import com.google.codelabs.migratingtojobs.nano.CatalogItemProtos;
import com.google.protobuf.nano.MessageNano;

import java.util.LinkedList;
import java.util.List;

public class CatalogItemStore {
    private interface Predicate<T> {
        boolean apply(T item);
    }

    private final List<CatalogItem> mItems;

    private Predicate<CatalogItem> mPredicateIsDownloading = new Predicate<CatalogItem>() {
        @Override
        public boolean apply(CatalogItem item) {
            return item.isDownloading();
        }
    };

    public CatalogItemStore(@NonNull List<CatalogItem> items) {
        mItems = items;
    }

    public CatalogItemStore(@NonNull CatalogItemProtos.CatalogItemStore protoStore) {
        mItems = new LinkedList<>();
        for (CatalogItemProtos.CatalogItem item : protoStore.items) {
            CatalogItemProtos.Book protoBook = item.book;

            //noinspection WrongConstant
            mItems.add(
                    new CatalogItem(
                            new Book(protoBook.title, protoBook.author),
                            item.downloadProgress,
                            item.status));
        }
    }
    public CatalogItem get(int position) {
        return mItems.get(position);
    }

    public int size() {
        return mItems.size();
    }

    @NonNull
    public List<CatalogItem> getDownloadQueue() {
        return filter(mPredicateIsDownloading);
    }

    public byte[] toProtoBytes() {
        CatalogItemProtos.CatalogItemStore store = new CatalogItemProtos.CatalogItemStore();
        store.items = new CatalogItemProtos.CatalogItem[mItems.size()];

        for (int i = 0; i < store.items.length; i++) {
            CatalogItem item = mItems.get(i);
            Book book = item.book.get();

            CatalogItemProtos.CatalogItem protoItem = new CatalogItemProtos.CatalogItem();
            CatalogItemProtos.Book protoBook = new CatalogItemProtos.Book();

            protoBook.title = book.title;
            protoBook.author = book.author;

            protoItem.book = protoBook;
            protoItem.downloadProgress = item.progress.get();
            protoItem.status = item.status.get();

            store.items[i] = protoItem;
        }

        return MessageNano.toByteArray(store);
    }

    private List<CatalogItem> filter(Predicate<CatalogItem> predicate) {
        List<CatalogItem> newList = new LinkedList<>();
        for (CatalogItem item : mItems) {
            if (predicate.apply(item)) {
                newList.add(item);
            }
        }

        return newList;
    }
}

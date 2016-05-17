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

import android.databinding.Observable;
import android.support.annotation.NonNull;

import com.google.codelabs.migratingtojobs.shared.nano.CatalogItemProtos;
import com.google.protobuf.nano.MessageNano;

import java.util.LinkedList;
import java.util.List;

public class CatalogItemStore {
    private final List<ChangeListener> mChangeListeners = new LinkedList<>();
    private final Observable.OnPropertyChangedCallback mPropertyChangedCallback =
            new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable observable, int i) {
                    notifyItemDidChange(observable);
                }
            };
    private final CatalogItemProtos.CatalogItemStore mProto;
    private final CatalogItem[] mItems;
    private Predicate<CatalogItem> mPredicateIsDownloading = new Predicate<CatalogItem>() {
        @Override
        public boolean apply(CatalogItem item) {
            return item.isErroring() || item.isDownloading();
        }
    };

    public CatalogItemStore(@NonNull CatalogItem[] items) {
        mProto = new CatalogItemProtos.CatalogItemStore();
        mItems = items;

        mProto.items = new CatalogItemProtos.CatalogItem[mItems.length];
        for (int i = 0; i < mItems.length; i++) {
            mProto.items[i] = mItems[i].getProto();
            mItems[i].addOnPropertyChangedCallback(mPropertyChangedCallback);
        }
    }

    public CatalogItemStore(@NonNull CatalogItemProtos.CatalogItemStore protoStore) {
        mProto = protoStore;

        mItems = new CatalogItem[mProto.items.length];
        for (int i = 0; i < mProto.items.length; i++) {
            mItems[i] = new CatalogItem(mProto.items[i]);
            mItems[i].addOnPropertyChangedCallback(mPropertyChangedCallback);
        }
    }

    public void addChangeListener(ChangeListener changeListener) {
        mChangeListeners.add(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        mChangeListeners.remove(changeListener);
    }

    private void notifyItemDidChange(Observable observable) {
        for (ChangeListener listener : mChangeListeners) {
            listener.onStoreDidChange();
        }
    }

    public CatalogItem get(int position) {
        return mItems[position];
    }

    public int size() {
        return mItems.length;
    }

    @NonNull
    public List<CatalogItem> getDownloadQueue() {
        return filter(mPredicateIsDownloading);
    }

    public byte[] toProtoBytes() {
        return MessageNano.toByteArray(mProto);
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

    interface ChangeListener {
        void onStoreDidChange();
    }

    private interface Predicate<T> {
        boolean apply(T item);
    }
}

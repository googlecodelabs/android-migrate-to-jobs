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

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

public class CatalogRecyclerAdaptor extends RecyclerView.Adapter<CatalogRecyclerAdaptor.Holder> {
    private final CatalogItemStore mStore;

    @Inject
    public CatalogRecyclerAdaptor(@NonNull CatalogItemStore items) {
        mStore = items;
    }

    public CatalogItemStore getStore() {
        return mStore;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.content_catalog_item, parent, false);

        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        CatalogItem item = mStore.get(position);
        holder.getBinding().setVariable(BR.item, item);

        // trigger queued bindings early (as opposed to next animation frame)
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mStore.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final ViewDataBinding mBinding;

        public Holder(View v) {
            super(v);

            mBinding = DataBindingUtil.bind(v);
        }

        public ViewDataBinding getBinding() {
            return mBinding;
        }
    }
}

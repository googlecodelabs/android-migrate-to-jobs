package com.google.codelabs.migratingtojobs.common;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.codelabs.migratingtojobs.BR;
import com.google.codelabs.migratingtojobs.R;

public class CatalogRecyclerAdaptor extends RecyclerView.Adapter<CatalogRecyclerAdaptor.Holder> {
    private CatalogItemStore mItems;

    public CatalogRecyclerAdaptor(@NonNull CatalogItemStore items) {
        mItems = items;
    }

    public CatalogItemStore getStore() {
        return mItems;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_catalog_item, parent, false);

        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        CatalogItem item = mItems.get(position);
        holder.getBinding().setVariable(BR.item, item);

        // trigger queued bindings early (as opposed to next animation frame)
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
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

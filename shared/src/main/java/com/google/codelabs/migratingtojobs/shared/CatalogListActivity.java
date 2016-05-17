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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import javax.inject.Inject;

public abstract class CatalogListActivity extends AppCompatActivity {
    @Inject
    CatalogRecyclerAdaptor mAdaptor;

    @Inject
    EventBus mBus;

    /* @BindView(R.id.catalog_list) */ RecyclerView mCatalogList;

    protected void inject() {
        DaggerRootComponent.builder()
                .appModule(new AppModule(getApplication()))
                .build()
                .inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_catalog_list);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        inject();

        mBus.postActivityCreated();

        mCatalogList = (RecyclerView) findViewById(R.id.catalog_list);
        mCatalogList.setLayoutManager(new LinearLayoutManager(this));
        mCatalogList.setAdapter(mAdaptor);
        mCatalogList.addOnItemTouchListener(new OnCatalogItemTouchListener());
    }

    @Override
    protected void onDestroy() {
        mBus.postActivityDestroyed();

        super.onDestroy();
    }

    private class OnCatalogItemTouchListener implements RecyclerView.OnItemTouchListener {
        private final GestureDetector mGestureDetector;

        public OnCatalogItemTouchListener() {
            mGestureDetector = new GestureDetector(getBaseContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent me) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View target = rv.findChildViewUnder(e.getX(), e.getY());
            if (target != null && mGestureDetector.onTouchEvent(e)) {
                CatalogItem item = mAdaptor.getStore().get(rv.getChildAdapterPosition(target));

                if (item.isAvailable()) {
                    mBus.postItemDeleteLocalCopy(item);
                } else if (item.isDownloading()) {
                    mBus.postItemDownloadCancelled(item);
                } else {
                    mBus.postItemDownloadStarted(item);
                }

                return true;
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}

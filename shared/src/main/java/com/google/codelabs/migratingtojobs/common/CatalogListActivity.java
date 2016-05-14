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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class CatalogListActivity extends AppCompatActivity {

    private RecyclerView mCatalogList;

    private CatalogRecyclerAdaptor mAdaptor;
    private BasicBrain mBrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCatalogList = (RecyclerView) findViewById(R.id.catalog_list);
        mCatalogList.setLayoutManager(new LinearLayoutManager(this));

        BasicApp app = ((BasicApp) getApplication());
        mAdaptor = new CatalogRecyclerAdaptor(app.getCatalogStore());
        mBrain = app.getBrain();

        mCatalogList.setAdapter(mAdaptor);
        mCatalogList.addOnItemTouchListener(new OnCatalogItemTouchListener());
    }

    @Override
    protected void onPause() {
        ((BasicApp) getApplication()).writeCatalogItems();
        super.onPause();
    }

    private class OnCatalogItemTouchListener implements RecyclerView.OnItemTouchListener {
        private final GestureDetector mGestueDetector;

        public OnCatalogItemTouchListener() {
            mGestueDetector = new GestureDetector(getBaseContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent me) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View target = rv.findChildViewUnder(e.getX(), e.getY());
            if (target != null && mGestueDetector.onTouchEvent(e)) {
                mBrain.didClickOn(mAdaptor.getStore().get(rv.getChildAdapterPosition(target)));

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

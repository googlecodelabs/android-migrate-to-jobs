package com.google.codelabs.migratingtojobs.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.google.codelabs.migratingtojobs.R;

public class CatalogList extends AppCompatActivity {

    private RecyclerView mCatalogList;

    private CatalogRecyclerAdaptor mAdaptor;
    private Brain mBrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCatalogList = (RecyclerView) findViewById(R.id.catalog_list);
        mCatalogList.setLayoutManager(new LinearLayoutManager(this));

        App app = ((App) getApplication());
        mAdaptor = new CatalogRecyclerAdaptor(app.getCatalogStore());
        mBrain = app.getBrain();

        mCatalogList.setAdapter(mAdaptor);
        mCatalogList.addOnItemTouchListener(new OnCatalogItemTouchListener());
    }

    @Override
    protected void onPause() {
        ((App) getApplication()).writeCatalogItems();
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

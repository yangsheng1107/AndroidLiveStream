package com.example.yangsheng.video;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by yangsheng on 2016/7/10.
 */
public class CircularListAdapter extends BaseAdapter {
    static final boolean DEBUG = false;
    static final String TAG = CircularListAdapter.class.getSimpleName();

    private BaseAdapter mListAdapter;
    private int mListAdapterCount;

    public CircularListAdapter(BaseAdapter listAdapter)
    {
        if(listAdapter == null) {
            throw new IllegalArgumentException("listAdapter cannot be null.");
        }

        this.mListAdapter = listAdapter;
        this.mListAdapterCount = listAdapter.getCount();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int i) {
        return mListAdapter.getItem(i % mListAdapterCount);
    }

    @Override
    public long getItemId(int i) {
       return mListAdapter.getItemId(i % mListAdapterCount);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        return mListAdapter.getView(position % mListAdapterCount, view, viewGroup);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mListAdapter.areAllItemsEnabled();
    }

    @Override
    public int getItemViewType(int position) {
        return mListAdapter.getItemViewType(position % mListAdapterCount);
    }

    @Override
    public int getViewTypeCount() {
        return mListAdapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mListAdapter.isEmpty();
    }

    @Override
    public boolean isEnabled(int position) {
        return mListAdapter.isEnabled(position % mListAdapterCount);
    }
    @Override
    public void notifyDataSetChanged() {
        mListAdapter.notifyDataSetChanged();
        mListAdapterCount = mListAdapter.getCount();

        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        mListAdapter.notifyDataSetInvalidated();
        super.notifyDataSetInvalidated();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return mListAdapter.getDropDownView(position % mListAdapterCount,
                convertView, parent);
    }

    @Override
    public boolean hasStableIds() {
        return mListAdapter.hasStableIds();
    }
}

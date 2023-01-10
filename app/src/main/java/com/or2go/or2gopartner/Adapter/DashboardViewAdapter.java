package com.or2go.or2gopartner.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.or2go.or2gopartner.R;

import java.util.List;

public class DashboardViewAdapter extends BaseAdapter {
    Context mContext;
    List<String> mCategoryList;
    List<Integer> mOrderImage;
    List<Integer> mOrderCountList;
    LayoutInflater inflter;
    private RequestQueue mRequestQueue;
    ImageLoader mImageLoader;
    int cacheSize = 1 * 1024 * 1024; // 4MiB

    public DashboardViewAdapter(Context context, List<Integer> mOrderImage, List<String> mCategoryList, List<Integer> mOrderCountList) {
        this.mContext = context;
        this.mCategoryList = mCategoryList;
        this.mOrderImage = mOrderImage;
        this.mOrderCountList = mOrderCountList;
        this.inflter = (LayoutInflater.from(context));

        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(cacheSize);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    @Override
    public int getCount() {
        return mCategoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.cardview_dashboard, null);
        TextView ordcat = (TextView) convertView.findViewById(R.id.dashlabel);
        TextView ordcnt = (TextView) convertView.findViewById(R.id.dashinfo1);
        ImageView catimg = (ImageView) convertView.findViewById(R.id.martcatimg);
        ordcat.setText(mCategoryList.get(position));
        ordcnt.setText(mOrderCountList.get(position).toString());
        catimg.setImageResource(mOrderImage.get(position));
        return convertView;
    }
}

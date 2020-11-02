package com.wasfat.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.wasfat.R;
import com.wasfat.ui.pojo.BannerResponseItem;

import java.util.ArrayList;

public class BannerAdapter extends PagerAdapter {
    LayoutInflater inflater;
    Context context;
    int position = 3;
    ArrayList<BannerResponseItem> bannerResponseItems;

    public BannerAdapter(Context context, ArrayList<BannerResponseItem> bannerResponseItems) {
        this.context = context;
        this.bannerResponseItems = bannerResponseItems;
    }

    @Override
    public int getCount() {
        return bannerResponseItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.banner_item, container, false);
        ImageView img = (ImageView) view.findViewById(R.id.ima1);
        Glide.with(context).load(bannerResponseItems.get(position).getBannerURL() + "/" + bannerResponseItems.get(position).getImageName()).into(img);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}


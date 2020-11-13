package com.sk3a.news.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.v7.widget.CardView;
import android.support.v4.view.PagerAdapter;

import com.bumptech.glide.Glide;
//import com.sk3a.news.DetailsActivity;
import com.sk3a.news.Model.News;
import com.sk3a.news.R;

import java.util.ArrayList;
import java.util.Random;

public class FeaturedAdapter extends PagerAdapter {

    private Context mContext;

    private ArrayList<News> mItemList;
    ArrayList<String> colors = new ArrayList<>();
    private LayoutInflater inflater;

    public FeaturedAdapter(Context mContext, ArrayList<News> mItemList) {
        this.mContext = mContext;
        this.mItemList = mItemList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }


    @Override
    public Object instantiateItem(final ViewGroup view, final int position) {

        View rootView = inflater.inflate(R.layout.item_featured, view, false);

        final TextView title = (TextView) rootView.findViewById(R.id.title_text);
        final TextView category = (TextView) rootView.findViewById(R.id.category_text);
        final ImageView image = (ImageView) rootView.findViewById(R.id.post_img);
        final CardView cardView = (CardView) rootView.findViewById(R.id.card_view_top);

        final News news = mItemList.get(position);

        // setting data over views
        String imgUrl = news.getUrl();
        Glide.with(mContext)
                    .load(imgUrl)
                    .into(image);

        title.setText(Html.fromHtml(news.getTitle()));
        category.setText(Html.fromHtml(news.getCategory()));
        colors.add("#673AB7");
        colors.add("#FFC107");
        colors.add("#009688");
        colors.add("#E91E63");
        colors.add("#3F51B5");
        colors.add("#4CAF50");
        colors.add("#E43700");

        Random r = new Random();
        int randomIdx = r.nextInt(colors.size());

        category.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colors.get(randomIdx))));

        view.addView(rootView);

        /*cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra("mid",news.getId());
                intent.putExtra("mtitle",news.getTitle());
                intent.putExtra("mdesc",news.getDesc());
                intent.putExtra("mimage",news.getUrl());
                intent.putExtra("mdate",news.getDate());
                intent.putExtra("mdetails",news.getIsFeatured());
                intent.putExtra("mcategory",news.getCategory());
                mContext.startActivity(intent);
            }
        });*/

        return rootView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

}

package com.sk3a.news.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sk3a.news.DetailsActivity;
import com.sk3a.news.Fragment.DetailsActivity_Fragment;
import com.sk3a.news.Model.News;
import com.sk3a.news.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RelatedNewsAdapter extends RecyclerView.Adapter<com.sk3a.news.adapters.RelatedNewsAdapter.WallpaperViewHolder> {

    private Context mCtx;
    private List<News> wallpaperList;
    private int STORAGE_PERMISSION_CODE = 1;
    ArrayList<String> colors = new ArrayList<>();




    public RelatedNewsAdapter(Context mCtx, List<News> wallpaperList) {
        this.mCtx = mCtx;
        this.wallpaperList = wallpaperList;
    }

    @Override
    public WallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_related, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WallpaperViewHolder holder, int position) {
        final News news = wallpaperList.get(position);

        //title
        holder.title.setText(news.getTitle());
        //date
        holder.date.setText(news.getDate());
        //category
        holder.category.setText(news.getCategory());

        colors.add("#673AB7");
        colors.add("#FFC107");
        colors.add("#009688");
        colors.add("#E91E63");
        colors.add("#3F51B5");
        colors.add("#4CAF50");
        colors.add("#E43700");
        Random r = new Random();
        int randomIdx = r.nextInt(colors.size());

        holder.category.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colors.get(randomIdx))));
        //news image
        Glide.with(mCtx)
                .load(news.getUrl())
                .into(holder.imageView);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mCtx, DetailsActivity.class);
//                intent.putExtra("mid",news.getId());
//                intent.putExtra("mtitle",news.getTitle());
//                intent.putExtra("mdesc",news.getDesc());
//                intent.putExtra("mimage",news.getUrl());
//                intent.putExtra("mdate",news.getDate());
//                intent.putExtra("mdetails",news.getIsFeatured());
//                intent.putExtra("mcategory",news.getCategory());
//                mCtx.startActivity(intent);
                Fragment homepage = new DetailsActivity_Fragment();
                FragmentTransaction fragmentManager =((FragmentActivity)mCtx).getSupportFragmentManager()
                        .beginTransaction();

                Bundle args = new Bundle();
                args.putInt("mid", news.getId());
                args.putString("mtitle", news.getTitle());
                args.putString("mdesc", news.getDesc());
                args.putString("mimage", news.getUrl());
                args.putString("mdate", news.getDate());
                args.putString("mdetails", news.getIsFeatured());
                args.putString("mcategory", news.getCategory());
                //args.putString("news_id", news_id);
                homepage.setArguments(args);


                fragmentManager.add(R.id.frame_layout, homepage);
                fragmentManager.addToBackStack(null);
                fragmentManager.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }


    class WallpaperViewHolder extends RecyclerView.ViewHolder{


        TextView title, date, category;
        ImageView imageView , fav_btn;
        RelativeLayout relativeLayout;

        public WallpaperViewHolder(View itemView) {
            super(itemView);


            title = itemView.findViewById(R.id.title_text);
            date = itemView.findViewById(R.id.date);
            category = itemView.findViewById(R.id.category_name);
            imageView = itemView.findViewById(R.id.post_img);
            relativeLayout = itemView.findViewById(R.id.lyt_container);
            fav_btn = itemView.findViewById(R.id.btn_fav);

        }
    }
}

package com.sk3a.news.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v7.widget.RecyclerView;
import com.bumptech.glide.Glide;
//import com.sk3a.news.DetailsActivity;
import com.sk3a.news.MainActivity;
//import com.sk3a.news.Model.FavoriteList;
import com.sk3a.news.Model.News;
import com.sk3a.news.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HomeNewsAdapter extends RecyclerView.Adapter<HomeNewsAdapter.WallpaperViewHolder> {

    private Context mCtx;
    private List<News> newsArrayList;
    private int STORAGE_PERMISSION_CODE = 1;
    ArrayList<String> colors = new ArrayList<>();




    public HomeNewsAdapter(Context mCtx, List<News> newsArrayList) {
        this.mCtx = mCtx;
        this.newsArrayList = newsArrayList;
    }

    @Override
    public WallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_news, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WallpaperViewHolder holder, int position) {
        final News news = newsArrayList.get(position);


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

        /*if (MainActivity.favoriteDatabase.favoriteDao().isFavorite(news.getId())==1)
            holder.fav_btn.setImageResource(R.drawable.ic_favorite_red);
        else
            holder.fav_btn.setImageResource(R.drawable.ic_favorite_black);

        holder.fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavoriteList favoriteList=new FavoriteList();

                int id=news.getId();
                String name= news.getTitle();
                String date = news.getDate();
                String category= news.getCategory();
                String desc= news.getDesc();
                String image= news.getUrl();

                favoriteList.setId(id);
                favoriteList.setName(name);
                favoriteList.setDate(date);
                favoriteList.setCategory(category);
                favoriteList.setDesc(desc);
                favoriteList.setImage(image);

                if (MainActivity.favoriteDatabase.favoriteDao().isFavorite(id)!=1){
                    holder.fav_btn.setImageResource(R.drawable.ic_favorite_red);
                    MainActivity.favoriteDatabase.favoriteDao().addData(favoriteList);


                }else {
                    holder.fav_btn.setImageResource(R.drawable.ic_favorite_black);
                    MainActivity.favoriteDatabase.favoriteDao().delete(favoriteList);


                }
            }
        });*/

        /*holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, DetailsActivity.class);
                intent.putExtra("mid",news.getId());
                intent.putExtra("mtitle",news.getTitle());
                intent.putExtra("mdesc",news.getDesc());
                intent.putExtra("mimage",news.getUrl());
                intent.putExtra("mdate",news.getDate());
                intent.putExtra("mdetails",news.getIsFeatured());
                intent.putExtra("mcategory",news.getCategory());
                mCtx.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
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

package com.sk3a.news.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.sk3a.news.DetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sk3a.news.DetailsActivity;
import com.sk3a.news.Fragment.DetailsActivity_Fragment;
import com.sk3a.news.Fragment.MainActivity_Fragment;
import com.sk3a.news.Fragment.User_MainActivity_Fragment;
import com.sk3a.news.MainActivity;
//import com.sk3a.news.Model.FavoriteList;
import com.sk3a.news.Main_Activity_Fragment;
import com.sk3a.news.Model.News;
import com.sk3a.news.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HomeNewsAdapter extends RecyclerView.Adapter<HomeNewsAdapter.WallpaperViewHolder> {

    private Context mCtx;
    private String news_id;
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
    public void onBindViewHolder(final WallpaperViewHolder holder, final int position) {
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


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mCtx, DetailsActivity_Fragment.class);
                intent.putExtra("mid",news.getId());
                intent.putExtra("mtitle",news.getTitle());
                intent.putExtra("mdesc",news.getDesc());
                intent.putExtra("mimage",news.getUrl());
                intent.putExtra("mdate",news.getDate());
                intent.putExtra("mdetails",news.getIsFeatured());
                intent.putExtra("mcategory",news.getCategory());
                intent.putExtra("news_id",news_id);


//                MainActivity_Fragment f = new MainActivity_Fragment();
//                Bundle args = new Bundle();
//                args.putInt("mid", news.getId());
//                args.putString("mtitle", news.getTitle());
//                args.putString("mdesc", news.getDesc());
//                args.putString("mimage", news.getUrl());
//                args.putString("mdate", news.getDate());
//                args.putString("mdetails", news.getIsFeatured());
//                args.putString("mcategory", news.getCategory());
//                args.putString("news_id", news_id);
//
//
//
//                f.setArguments(args);
//                Toast.makeText(mCtx, news.getTitle(), Toast.LENGTH_LONG).show();

                //Fragment b = new DetailsActivity_Fragment();

                //((AppCompatActivity)mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,b,"details_fragment").addToBackStack(null).commit();

                //mCtx.startActivity(intent);

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
                args.putString("news_id", news_id);
                homepage.setArguments(args);


                fragmentManager.replace(R.id.frame_layout, homepage,"details");
                fragmentManager.addToBackStack(null);
                fragmentManager.commit();

            }
        });
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

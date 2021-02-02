package com.sk3a.news.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v7.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sk3a.news.Model.Comments;
import com.sk3a.news.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CommentNewsAdapter extends RecyclerView.Adapter<CommentNewsAdapter.WallpaperViewHolder> {

    private Context mCtx;
    private String news_id;
    private List<Comments> newsArrayList;
    private int STORAGE_PERMISSION_CODE = 1;



    public CommentNewsAdapter(Context mCtx, List<Comments> newsArrayList) {
        this.mCtx = mCtx;
        this.newsArrayList = newsArrayList;
    }

    @Override
    public WallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_comment, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WallpaperViewHolder holder, final int position) {
        final Comments comments = newsArrayList.get(position);


        //user_name
        holder.user_name.setText(comments.getUser_name());
        //user_date
        holder.user_date.setText(comments.getUser_date());
        //user_comment
        holder.user_comment.setText(comments.getUser_comment());
        //comment image

        if(comments.getUser_avatar() == null){
        } else{
            Glide.with(mCtx)
                    .load(comments.getUser_avatar())
                    .into(holder.user_avatar);
        }
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }


    class WallpaperViewHolder extends RecyclerView.ViewHolder{


        TextView user_date, user_comment,user_name;
        ImageView user_avatar;
        RelativeLayout relativeLayout;

        public WallpaperViewHolder(View itemView) {
            super(itemView);

            user_comment = itemView.findViewById(R.id.user_comment);
            user_date = itemView.findViewById(R.id.user_date);
            user_name = itemView.findViewById(R.id.user_name);
            user_avatar = itemView.findViewById(R.id.user_image);
        }
    }
}

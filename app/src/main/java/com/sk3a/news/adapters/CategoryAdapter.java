package com.sk3a.news.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.sk3a.news.Fragment.Details_CategoryActivity_Fragment;
import com.sk3a.news.Model.Category;
//import com.sk3a.news.NewsActivity;
import com.sk3a.news.R;

import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context mCtx;
    private List<Category> categoryList;
    int selected_position = 0;


    public CategoryAdapter(Context mCtx, List<Category> categoryList) {
        this.mCtx = mCtx;
        this.categoryList = categoryList;

    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category c = categoryList.get(position);
        holder.textView.setText(c.name);
        Glide.with(mCtx)
                .load(c.thumb)
                .into(holder.imageView);

        //  highlighting the background
        holder.itemView.setBackgroundColor(selected_position == position ? Color.LTGRAY : Color.TRANSPARENT);


    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        ImageView imageView;


        public CategoryViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_view_cat_name);
            imageView = itemView.findViewById(R.id.image_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int p = getAdapterPosition();
            Category c = categoryList.get(p);

            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

            // Updating old as well as new positions
            notifyItemChanged(selected_position);
            selected_position = getAdapterPosition();
            notifyItemChanged(selected_position);


            //Intent intent = new Intent(mCtx, Details_CategoryActivity_Fragment.class);
            //intent.putExtra("category", c.name);

            //mCtx.startActivity(intent);

            Fragment details = new Details_CategoryActivity_Fragment();
            FragmentTransaction fragmentManager =((FragmentActivity)mCtx).getSupportFragmentManager()
                    .beginTransaction();

            Bundle args = new Bundle();
            args.putString("category", c.name);
            details.setArguments(args);


            fragmentManager.add(R.id.frame_layout_category, details);
            fragmentManager.addToBackStack(null);
            fragmentManager.commit();
        }
    }
}

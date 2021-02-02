package com.sk3a.news.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sk3a.news.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;


import com.sk3a.news.Config;
import com.sk3a.news.Model.News;
import com.sk3a.news.R;
import com.sk3a.news.adapters.CategoryDetailsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Details_CategoryActivity_Fragment extends Fragment {
    List<News> wallpaperList;
    List<News> favList;
    RecyclerView recyclerView;
    CategoryDetailsAdapter adapter;
    ProgressBar progressBar;
    DatabaseReference dbWallpapers, dbFavs;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String category;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.activity_categorydetails,container,false);

        category = getArguments().getString("category");

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(category);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

        wallpaperList = new ArrayList<>();
        favList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);


        if (Config.homePageLayout){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
            //gridLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(gridLayoutManager);
        }else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(layoutManager);
        }

        adapter = new CategoryDetailsAdapter(getActivity(), wallpaperList);

        recyclerView.setAdapter(adapter);

        progressBar = rootView.findViewById(R.id.progressbar);
        dbWallpapers = FirebaseDatabase.getInstance().getReference("news");

        adapter.notifyDataSetChanged();

        fetchNews(category);

        return rootView;
    }

    private void fetchNews(final String category) {
        progressBar.setVisibility(View.VISIBLE);
        dbWallpapers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        News news = wallpaperSnapshot.getValue(News.class);

                        favList.add(news);
                    }
                    for (int i = 0; i < favList.size(); i++) {
                        if (favList.get(i).getCategory().equals(category)) {
                            wallpaperList.add(favList.get(i));
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            getActivity().finish(); // close this activity and return to preview activity (if there is any)

        }

        return super.onOptionsItemSelected(item);
    }
}

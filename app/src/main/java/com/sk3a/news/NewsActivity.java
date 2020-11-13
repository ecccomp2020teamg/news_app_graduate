/*
package com.sk3a.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.widget.SwipeRefreshLayout;

import com.sk3a.news.Model.News;
import com.sk3a.news.adapters.WallpapersAdapter;
*/
/*import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;*//*

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity{

    List<News> wallpaperList;
    List<News> favList;
    RecyclerView recyclerView;
    WallpapersAdapter adapter;
    ProgressBar progressBar;
    DatabaseReference dbWallpapers, dbFavs;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String category;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_news);
        setContentView(R.layout.content_main);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(category);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //loadBannerAds();
        //loadInterstitialAds();

        wallpaperList = new ArrayList<>();
        favList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);


        if (Config.homePageLayout){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
            //gridLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(gridLayoutManager);
        }else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(layoutManager);
        }

        adapter = new WallpapersAdapter(this, wallpaperList);

        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.progressbar);
        dbWallpapers = FirebaseDatabase.getInstance().getReference("news");

        adapter.notifyDataSetChanged();

        fetchNews(category);
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

    */
/*private void loadBannerAds() {
        if (Config.showBannerAds){
            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    private void loadInterstitialAds() {
        if (Config.showInterstitialAds){
            final InterstitialAd interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId(getString(R.string.interstitial_ads));
            AdRequest request = new AdRequest.Builder().build();
            interstitialAd.loadAd(request);
            interstitialAd.setAdListener(new AdListener(){
                public void onAdLoaded(){
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                }
            });
        }
    }*//*


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            finish(); // close this activity and return to preview activity (if there is any)

        }

        return super.onOptionsItemSelected(item);
    }

}
*/

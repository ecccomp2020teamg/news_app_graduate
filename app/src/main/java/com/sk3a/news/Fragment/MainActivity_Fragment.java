package com.sk3a.news.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;

//add more here
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

//import com.sk3a.news.Model.Category;
import com.sk3a.news.BottomNavigationBehavior;
import com.sk3a.news.DarkModePrefManager;
import com.sk3a.news.Fragment.User_Login_Fragment;
import com.sk3a.news.Login.User_Login;
import com.sk3a.news.MainActivity;
import com.sk3a.news.Model.News;
//import com.sk3a.news.adapters.CategoryAdapter;
import com.sk3a.news.R;
import com.sk3a.news.adapters.FeaturedAdapter;
import com.sk3a.news.adapters.HomeNewsAdapter;
import android.support.design.widget.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AlertDialog;
import android.app.AlertDialog;
//import androidx.core.view.GravityCompat;
//import androidx.fragment.app.FragmentManager;
import android.support.v4.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
import android.support.v4.app.FragmentTransaction;
//import androidx.viewpager.widget.ViewPager;
import android.support.v4.view.ViewPager;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.LinearLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


public class MainActivity_Fragment extends Fragment {

    //for home display
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    DatabaseReference mDatabaseReference, mCategoryReference;
    RelativeLayout mLytFeatured, lyt_featured;
    private Activity mActivity;
    Timer timer;
    ProgressBar progressBar;
    LinearLayout content_layout;

    //public static FavoriteDatabase favoriteDatabase;
    //AdView mAdView;
    //private InterstitialAd interstitialAd;

    //news list
    private List<News> newsArrayList;
    private List<News> newsList;
    private HomeNewsAdapter wallpapersAdapter;

    // Featured
    private ArrayList<News> mFeaturedList;
    private ViewPager mFeaturedPager;
    private FeaturedAdapter mFeaturedPagerAdapter = null;

    private Handler handler;
    private int delay = 5000; //milliseconds
    private int page = 0;
    Runnable runnable = new Runnable() {
        public void run() {
            if (mFeaturedPagerAdapter.getCount() == page) {
                page = 0;
            } else {
                page++;
            }
            mFeaturedPager.setCurrentItem(page, true);
            handler.postDelayed(this, delay);
        }
    };
    private static final int id = 0;
    private static final String title = null;
    private static final String desc = null;
    private static final String url = null;
    private static final String date = null;
    private static final String isfeatured = null;
    private static final String  category = null;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.app_bar_main,container,false);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("個人情報");

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        mActivity = getActivity();


        //DrawerLayout drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);


        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                //getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.addDrawerListener(toggle);
        //toggle.syncState();

        NavigationView navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(getActivity());

        //bottomNavigationView = rootView.findViewById(R.id.navigation);
        //bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//
        //CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        //layoutParams.setBehavior(new BottomNavigationBehavior());

        //bottomNavigationView.setSelectedItemId(R.id.navigationHome);

        //add more
        newsArrayList = new ArrayList<>();
        newsList = new ArrayList<>();
        mFeaturedList = new ArrayList<>();

        lyt_featured = rootView.findViewById(R.id.lyt_featured);
        progressBar = rootView.findViewById(R.id.progressbar);
        content_layout = rootView.findViewById(R.id.contentlayout);
        drawerLayout = rootView.findViewById(R.id.drawer_layout);
        //navigationView = rootView.findViewById(R.id.nav_view);
        //navigationView.setItemIconTintList(null);
        //navigationView.setNavigationItemSelectedListener(this);

        //actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        //drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        //actionBarDrawerToggle.syncState();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("news");

        progressBar.setVisibility(View.VISIBLE);
        loadPostsFromFirebase();
        fetchNews();

        //for category
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view1);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        wallpapersAdapter = new HomeNewsAdapter(getActivity(), newsList);
        recyclerView.setAdapter(wallpapersAdapter);

        handler = new Handler();
        mFeaturedPager = rootView.findViewById(R.id.pager_featured_posts);
        mLytFeatured = (RelativeLayout) rootView.findViewById(R.id.lyt_featured);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mFeaturedPager, true);


        mFeaturedPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;
    }
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
////        if (id == R.id.nav_camera) {
////            // Handle the camera action
////        } else if (id == R.id.nav_gallery) {
////
////        } else if (id == R.id.nav_slideshow) {
////
////        } else if (id == R.id.nav_manage) {
////
////        } else if (id == R.id.nav_share) {
////
////        } else if (id == R.id.nav_dark_mode) {
//            //code for setting dark mode
//            //true for dark mode, false for day mode, currently toggling on each click
//            //DarkModePrefManager darkModePrefManager = new DarkModePrefManager(getActivity());
//            //darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
//            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            //getActivity().recreate();
//
//        //}
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
    //}

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void loadPostsFromFirebase() {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot contentSnapShot : dataSnapshot.getChildren()) {
                    News news = contentSnapShot.getValue(News.class);
                    newsArrayList.add(news);
                }
                for (int i = 0; i < newsArrayList.size(); i++) {
                    if (newsArrayList.get(i).getIsFeatured().equals("Yes")) {
                        mFeaturedList.add(newsArrayList.get(i));
                    }
                }
                mFeaturedPagerAdapter = new FeaturedAdapter(mActivity, (ArrayList<News>) mFeaturedList);
                mFeaturedPager.setAdapter(mFeaturedPagerAdapter);

//                TimerTask timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        mFeaturedPager.post(new Runnable(){
//
//                            @Override
//                            public void run() {
//                                mFeaturedPager.setCurrentItem((mFeaturedPager.getCurrentItem()+1));
//                            }
//                        });
//                    }
//                };
//                timer = new Timer();
//                timer.schedule(timerTask, 3000, 3000);


                if (mFeaturedList.size() > 0) {
                    mLytFeatured.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@android.support.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }

    private void fetchNews() {
        //progressBar.setVisibility(View.VISIBLE);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                content_layout.setVisibility(View.VISIBLE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        News news = wallpaperSnapshot.getValue(News.class);

                        newsList.add(news);

                    }
                    wallpapersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBodyText = "https://play.google.com/store/apps/details?id="+getActivity().getPackageName();
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);
                startActivity(Intent.createChooser(intent,"share via"));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_favorite_red)
                .setTitle(getString(R.string.app_name))
                .setMessage("アプリを閉じたいですか？")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}

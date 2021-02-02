package com.sk3a.news;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sk3a.news.Fragment.CategoryActivity_Fragment;
import com.sk3a.news.Fragment.FavouriteActivity_Fragment;
import com.sk3a.news.Fragment.MainActivity_Fragment;
import com.sk3a.news.Fragment.SearchActivity_Fragment;
import com.sk3a.news.Fragment.User_Login_Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.MenuItem;

//add more here
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

//import com.sk3a.news.Model.Category;
import com.sk3a.news.Fragment.User_Login_Fragment;
import com.sk3a.news.Login.User_Login;
import com.sk3a.news.Model.News;
//import com.sk3a.news.adapters.CategoryAdapter;
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


public class Main_Activity_Fragment extends AppCompatActivity {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        BottomNavigationView bottomNav = findViewById(R.id.navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.navigationHome);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //add more
        mActivity = Main_Activity_Fragment.this;

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragement = null;

                    switch (menuItem.getItemId()) {
                        case R.id.navigationMenu:
                            selectedFragement = new FavouriteActivity_Fragment();
                            break;
                        case R.id.navigationSearch:
                            selectedFragement = new SearchActivity_Fragment();
                            break;
                        case R.id.navigationHome:
                            selectedFragement = new MainActivity_Fragment();
                            break;
                        case R.id.navigationMyCourses:
                            selectedFragement = new CategoryActivity_Fragment();
                            break;
                        case R.id.navigationMyProfile:
                            selectedFragement = new User_Login_Fragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                            selectedFragement).addToBackStack(null).commit();

                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBodyText = "https://play.google.com/store/apps/details?id="+getPackageName();
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);
                startActivity(Intent.createChooser(intent,"share via"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

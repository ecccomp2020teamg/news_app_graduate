package com.sk3a.news.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;


import com.sk3a.news.Model.Category;
import com.sk3a.news.Model.News;
import com.sk3a.news.R;
import com.sk3a.news.adapters.CategoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity_Fragment extends Fragment {

    private List<Category> categoryList;
    private ProgressBar progressBar;
    private DatabaseReference dbCategories;

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    String category;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.activity_category,container,false);

        progressBar = rootView.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),5));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity() , RecyclerView.HORIZONTAL, false));
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(getActivity(), categoryList);
        recyclerView.setAdapter(adapter);

        dbCategories = FirebaseDatabase.getInstance().getReference("categories");
        //DatabaseReference dbWallpapers = FirebaseDatabase.getInstance().getReference("news");

        dbCategories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.getKey();
                        String desc = ds.child("desc").getValue(String.class);
                        //String quotes = ds.child("quotes").getValue(String.class);
                        String thumb = ds.child("thumbnail").getValue(String.class);

                        Category c = new Category(name, desc, thumb);
                        categoryList.add(c);

                        //fetchNews(c.name);

                    }
                    adapter.notifyDataSetChanged();

                    //for show default details of category
                    Fragment details = new Details_CategoryActivity_Fragment();
                    FragmentTransaction fragmentManager =(getActivity()).getSupportFragmentManager()
                            .beginTransaction();

                    Bundle args = new Bundle();
                    args.putString("category", categoryList.get(0).name);
                    details.setArguments(args);


                    fragmentManager.replace(R.id.frame_layout_category, details);
                    fragmentManager.addToBackStack(null);
                    fragmentManager.commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    /*private void fetchNews(final String category) {
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
        });*/
    //}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            getActivity().finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}

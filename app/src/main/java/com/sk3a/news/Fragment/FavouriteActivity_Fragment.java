package com.sk3a.news.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sk3a.news.R;

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
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.sk3a.news.Config;
import com.sk3a.news.Model.News;
import com.sk3a.news.R;
import com.sk3a.news.adapters.CategoryDetailsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sk3a.news.adapters.FavouriteAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity_Fragment extends Fragment {

    List<News> wallpaperList;
    List<String> favouriteList = new ArrayList<String>();
    List<News> favList;
    RecyclerView recyclerView;
    FavouriteAdapter adapter;
    ProgressBar progressBar;
    DatabaseReference dbWallpapers, dbFavs;
    DatabaseReference favourite;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String category;
    private String userId;
    private int count = 0;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;

    //import from Hung
    public News newsItem;
    public String b = "0";
    TextView text_favourite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.activity_favourite,container,false);


        text_favourite = rootView.findViewById(R.id.text_favourite);
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

        adapter = new FavouriteAdapter(getActivity(), wallpaperList);

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        progressBar = rootView.findViewById(R.id.progressbar);
        dbWallpapers = FirebaseDatabase.getInstance().getReference("news");

        adapter.notifyDataSetChanged();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            fetchFavourite();
            fetchNews();

        } else {
            text_favourite.setText("お気に入りニュースがありません！");
        }

        // import from Hung, swipe left and right to remove favourite news
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0 ,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT ) {    //if swipe left

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //alert for confirm to delete
                    builder.setMessage("本当に削除したいのですか?");    //set message

                    builder.setPositiveButton("削除", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth fAuth = FirebaseAuth.getInstance();;
                            FirebaseUser fuser = fAuth.getCurrentUser();

                            newsItem = adapter.getNewsAt(position);
                            adapter.notifyItemRemoved(position);
                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("user");
                            //myRef.child(fuser.getUid()).child("Like").child(newsItem.getKey()).removeValue();
                            myRef.child(fuser.getUid()).child("Like").child(String.valueOf(newsItem.getId())).removeValue();

                            Log.i("delete", "削除完了");

                            //reload favourite page
                            getFragmentManager().beginTransaction().replace(R.id.frame_layout,new FavouriteActivity_Fragment()).commit();
                        }
                    }).setNegativeButton(
                            "キャンセル",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //reload favourite page
                                    getFragmentManager().beginTransaction().replace(R.id.frame_layout,new FavouriteActivity_Fragment()).commit();
                                }
                            })
                            .show();
                }
            }
        }).attachToRecyclerView(recyclerView);

        return rootView;
    }

    private void fetchFavourite(){
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressBar.setVisibility(View.VISIBLE);
        favourite = FirebaseDatabase.getInstance().getReference("user");

        favourite.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {
                        if (wallpaperSnapshot.getKey().equals(userId)){
                            for (DataSnapshot newssnapshot : wallpaperSnapshot.child("Like").getChildren()) {
                                //Toast.makeText(getActivity(),newssnapshot.getKey(),Toast.LENGTH_LONG).show();
                                favouriteList.add(newssnapshot.getKey());
                            }
                        }
                    }
                    //if removed all of favourite list, show the message
                }else {
                    text_favourite.setText("お気に入りニュースがありません！");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchNews() {
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
                    for (int i = 0; i < favouriteList.size(); i++) {
                        for (int j = 0; j < favList.size();j++ ) {
                            if (favList.get(j).getId() == Integer.valueOf(favouriteList.get(i))) {
                                wallpaperList.add(favList.get(j));
                            }
                        }
                    }
                    //Toast.makeText(getActivity(),wallpaperList.toString(),Toast.LENGTH_LONG).show();
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
    }
}

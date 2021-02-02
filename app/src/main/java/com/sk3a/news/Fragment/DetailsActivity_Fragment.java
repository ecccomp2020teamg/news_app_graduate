package com.sk3a.news.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sk3a.news.DetailsActivity;
import com.sk3a.news.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.sk3a.news.Login.User_Login;
import com.sk3a.news.Login.User_Register;
import com.sk3a.news.Model.Comments;
import com.sk3a.news.Model.News;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sk3a.news.adapters.CommentNewsAdapter;
import com.sk3a.news.adapters.HomeNewsAdapter;
import com.sk3a.news.adapters.RelatedNewsAdapter;

//from Cuong
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class DetailsActivity_Fragment extends Fragment {

    RelativeLayout relativeLayout;
    TextView title ,date, categoryname;
    ImageView imageView, fav_btn;
    WebView webView;
    String mytitle = "";
    String mydecs , myimg, mydate, mydetails, mycategoty;
    int myid;

    //related news
    List<News> wallpaperList;
    List<News> favList;

    //comment list
    private List<Comments> newsArrayList;
    private List<Comments> newsList;
    private CommentNewsAdapter wallpapersAdapter;

    ListView listView;
    RecyclerView recyclerView;
    RecyclerView recyclerView_comment;

    RelatedNewsAdapter adapter;
    ProgressBar progressBar;
    DatabaseReference dbWallpapers, dbFavs;

    private DatabaseReference newsDetails;

    DatabaseReference mDatabaseReference;
    DatabaseReference Comment_DatabaseReference;

    //for comment fuction
    private AlertDialog.Builder dialogBuilder;
    private  AlertDialog dialog;
    private EditText user_name_id,user_email_id,user_comments_id;
    private String user_name, user_email,user_comments,user_avatar;
    private ImageView user_image,user_image_id;
    private Button button_comments,button_cancle;
    private Button news_comment;
    private long count;

    private String comment = "";
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter_listview;

    //import from Cuong
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;
    private String userId;
    private StorageReference storageReference;
    FirebaseAuth.AuthStateListener mAuthListener;

    // images
    ImageView profileImage;
    String login_confirm = "0";

    //favorite button
    private Boolean likedFlag = true;
    private ImageButton likeButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.activity_details,container,false);

        mytitle = getArguments().getString("mtitle");
        myid = getArguments().getInt("mid", 1);
        mydecs = getArguments().getString("mdesc");
        myimg = getArguments().getString("mimage");
        mydate = getArguments().getString("mdate");
        mydetails = getArguments().getString("mdetails");
        mycategoty = getArguments().getString("mcategory");



        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(mycategoty);


        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        setHasOptionsMenu(true);


        wallpaperList = new ArrayList<>();
        favList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity() , RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RelatedNewsAdapter(getActivity(), wallpaperList);

        recyclerView.setAdapter(adapter);

        progressBar = rootView.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        dbWallpapers = FirebaseDatabase.getInstance().getReference("news");

        title = rootView.findViewById(R.id.news_title);
        date = rootView.findViewById(R.id.news_date);
        //categoryname = rootView.findViewById(R.id.news_category);
        webView = rootView.findViewById(R.id.news_desc);
        imageView = rootView.findViewById(R.id.news_image);
        fav_btn = rootView.findViewById(R.id.fav_btn);
        relativeLayout = rootView.findViewById(R.id.main_layout);
        progressBar = rootView.findViewById(R.id.progressbar);


        title.setText(mytitle);
        webView.setBackgroundColor(Color.parseColor("#ECF4FB"));
        webView.setFocusableInTouchMode(false);
        webView.setFocusable(false);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String text = "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/myfonts/font.ttf\")}body,* {font-family: MyFont; color:#666666; font-size: 18px;line-height:1.2}img{max-width:100%;height:auto; border-radius: 3px;}</style>";
        webView.loadDataWithBaseURL("", text + "<div>" + mydecs + "</div>", "text/html", "utf-8", null);

        //categoryname.setText(mycategoty);
        date.setText(mydate);

        Glide.with(this)
                .load(myimg)
                .into(imageView);

        //for the comments fuction
        user = fAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        news_comment = rootView.findViewById(R.id.news_comment);
        news_comment.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                if (user != null) {
                    createNewCommentDialog();
                }
                else {
                    login_createNewCommentDialog();
                }
            }
        });

        //for favorite button

        likeButton = rootView.findViewById(R.id.fav_btn);
        //final String myid = intent.getStringExtra("myid");
        if(user != null){
            checkLikedNews();
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( user != null){
                    //checkLikedNews();
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("user");
                    if ( likedFlag == true){
                        myRef.child(user.getUid()).child("Like").child(String.valueOf(myid)).setValue(" ");
                        likeButton.setImageResource(R.drawable.ic_favorite_red);
                        likedFlag = false;
                    }else {
                        myRef.child(user.getUid()).child("Like").child(String.valueOf(myid)).removeValue();
                        likeButton.setImageResource(R.drawable.ic_favorite_border);
                        likedFlag = true;
                    }
                }else{
                    new AlertDialog.Builder(getActivity())
                            .setTitle("5PC ニュース")
                            .setMessage("この機能を使うにはログインしてください！")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // OK button pressed
                                    //startActivity(new Intent(getActivity(),User_Login.class));
                                    getFragmentManager().beginTransaction().replace(R.id.frame_layout, new User_Login_Fragment()).commit();
                                    //Toast.makeText(getApplicationContext(), "コメントする前にログインしてください", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("キャンセル", null)
                            .show();
                }

            }

        });


        //for the related news here
        dbWallpapers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        News news = wallpaperSnapshot.getValue(News.class);

                        favList.add(news);
                    }
                    for (int i = 0; i < favList.size(); i++) {
                        if (favList.get(i).getCategory().equals(mycategoty)) {
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

        //show comment in recycle view
        newsList = new ArrayList<>();

        recyclerView_comment = rootView.findViewById(R.id.read_comment);
        recyclerView_comment.setHasFixedSize(true);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        layoutManager1.setReverseLayout(true);
        layoutManager1.setStackFromEnd(true);
        recyclerView_comment.setLayoutManager(layoutManager1);

        wallpapersAdapter = new CommentNewsAdapter(getActivity(), newsList);
        recyclerView_comment.setAdapter(wallpapersAdapter);

        fetchComments();


        return rootView;
    }

    //import from Hung >> check if like button was pressed or not
    public void checkLikedNews(){
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("user").getRef();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {
                        if (wallpaperSnapshot.getKey().equals(userId)){
                            for (DataSnapshot newssnapshot : wallpaperSnapshot.child("Like").getChildren()) {
                                //Toast.makeText(getActivity(), newssnapshot.getKey(), Toast.LENGTH_LONG).show();
                                if (newssnapshot.getKey().equals(String.valueOf(myid))){
                                    likeButton.setImageResource(R.drawable.ic_favorite_red);
                                    likedFlag = false;
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                //getActivity().finish(); // close this activity and return to preview activity (if there is any)
                //adapter.notifyDataSetChanged();
                getFragmentManager().popBackStack();

                return true;

            case R.id.action_settings:

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBodyText = mytitle;
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);
                startActivity(Intent.createChooser(intent,"share via"));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //create new diaglog to input comment

    public void createNewCommentDialog() {

        LayoutInflater inflater
                = LayoutInflater.from(getActivity());
        View commentView = inflater.inflate(R.layout.activity_comments, null);
        //get id from user input
        user_name_id = commentView.findViewById(R.id.user_name);
        user_email_id = commentView.findViewById(R.id.user_mail);
        user_comments_id = commentView.findViewById(R.id.user_comment);
        user_image_id = commentView.findViewById(R.id.user_image_add);

        //firebase connection here
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("comment");

        user = fAuth.getInstance().getCurrentUser();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Toast.makeText(getApplicationContext(), userId, Toast.LENGTH_LONG).show();

        // get data from google account then export name and email to alert dialog
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (signInAccount != null) {
            // get user name
            user_name_id.setText(signInAccount.getDisplayName());
            //get email
            user_email_id.setText(signInAccount.getEmail());
            //get email
            //Uri photo = signInAccount.getPhotoUrl();
            //Glide.with(this).load(String.valueOf(photo)).into(user_image_id);
        }

        // get data from facebook
        if(user!=null){
            // get profile picture
            //Glide.with(this).load(user.getPhotoUrl()).into(user_image_id);
            // get email
            user_email_id.setText(user.getEmail());
            // get name
            user_name_id.setText(user.getDisplayName());

        }



        //get profile picture from storage in firebase
        final DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener( new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Toast.makeText(getActivity(), "Listen faild"+ e, Toast.LENGTH_LONG).show();
                    return;
                }
                if(documentSnapshot != null && documentSnapshot.exists()){

                    user_name_id.setText(documentSnapshot.getString("UserName"));
                    user_email_id.setText(documentSnapshot.getString("Email"));

                }else {
                    Toast.makeText(getActivity(), "エラーが発生したよ", Toast.LENGTH_LONG).show();
                }

            }
        });
        StorageReference profileRef = storageReference.child("users/" + fAuth.getInstance().getCurrentUser().getUid() + "/profile.jpg");
        if (profileRef == null){
        }else{
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(user_image_id);
                }
            });}

        new AlertDialog.Builder(getActivity())
                .setTitle("こんにちは!")
                //.setIcon(R.drawable.avatar)
                //.setIcon(R.drawable.avatar)
                .setView(commentView)
                .setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get text from id
                                user_comments = user_comments_id.getText().toString();
                                user_name = user_name_id.getText().toString();
                                user_email = user_email_id.getText().toString();
                                StorageReference profileRef = storageReference.child("users/" + fAuth.getInstance().getCurrentUser().getUid() + "/profile.jpg");
                                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                {
                                    @Override
                                    public void onSuccess(Uri downloadUrl)
                                    {
                                        user_avatar = downloadUrl.toString();
                                    }
                                });
                                if (profileRef == null){
                                    user_avatar = "";
                                }else {
                                    user_avatar = profileRef.toString();
                                }
                                final String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                                // データベースの参照を取得する
                                final DatabaseReference ref = mDatabaseReference.child(String.valueOf(myid)).getRef();

                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        count = dataSnapshot.getChildrenCount() + 1;

                                        ref.child(String.valueOf(count)).child("user_date").setValue(currentDate);
                                        ref.child(String.valueOf(count)).child("user_email").setValue(user_email);
                                        ref.child(String.valueOf(count)).child("user_name").setValue(user_name);
                                        ref.child(String.valueOf(count)).child("user_comment").setValue(user_comments);
                                        ref.child(String.valueOf(count)).child("user_avatar").setValue(user_avatar);
                                        count++;

                                        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.frame_layout);
                                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                        fragmentTransaction.detach(currentFragment);
                                        fragmentTransaction.attach(currentFragment);
                                        fragmentTransaction.commit();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }

                                });

                                Context context = getActivity();

                                Toast.makeText(context, "コメント追加に成功しました", Toast.LENGTH_LONG).show();

                                dialog.dismiss();


                            }
                        })
                .setNegativeButton(
                        "キャンセル",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    public void login_createNewCommentDialog() {

        new AlertDialog.Builder(getActivity())
                .setTitle("5PC　ニュース")
                .setMessage("この機能を使うにはログインしてください！")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // OK button pressed
                        //startActivity(new Intent(getActivity(),User_Login.class));
                        getFragmentManager().beginTransaction().replace(R.id.frame_layout, new User_Login_Fragment()).commit();
                        //Toast.makeText(getApplicationContext(), "コメントする前にログインしてください", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("キャンセル", null)
                .show();
    }

    private void fetchComments() {

        //show the comment here

        Comment_DatabaseReference = FirebaseDatabase.getInstance().getReference("comment").child(String.valueOf(myid)).getRef();

        Comment_DatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        Comments comments = wallpaperSnapshot.getValue(Comments.class);

                        newsList.add(comments);

                    }
                    wallpapersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

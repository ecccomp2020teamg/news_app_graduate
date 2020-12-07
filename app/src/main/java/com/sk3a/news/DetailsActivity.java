package com.sk3a.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.ChildEventListener;
import com.sk3a.news.Model.Comments;
import com.sk3a.news.Model.News;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sk3a.news.adapters.RelatedNewsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;
    TextView title ,date, categoryname;
    ImageView imageView, fav_btn;
    WebView webView;
    String mytitle, mydecs , myimg, mydate, mydetails, mycategoty;
    int myid;

    //related news
    List<News> wallpaperList;
    List<News> favList;

    //comment
    List<Comments> commentsList;

    ListView listView;
    RecyclerView recyclerView;
    RelatedNewsAdapter adapter;
    ProgressBar progressBar;
    DatabaseReference dbWallpapers, dbFavs;

    private DatabaseReference newsDetails;

    DatabaseReference mDatabaseReference;

    //for comment fuction
    private AlertDialog.Builder dialogBuilder;
    private  AlertDialog dialog;
    private EditText user_name_id,user_email_id,user_comments_id;
    private String user_name, user_email,user_comments;
    private ImageView user_image;
    private Button button_comments,button_cancle;
    private Button news_comment;
    private String getkey;

    private long count;
    private int user_id;

    private String comment = "";
    private int count_comment = 1;

    private ArrayList<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mytitle = getIntent().getStringExtra("mtitle");
        myid = getIntent().getIntExtra("mid", 1);
        mydecs = getIntent().getStringExtra("mdesc");
        myimg = getIntent().getStringExtra("mimage");
        mydate = getIntent().getStringExtra("mdate");
        mydetails = getIntent().getStringExtra("mdetails");
        mycategoty = getIntent().getStringExtra("mcategory");


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(mycategoty);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        wallpaperList = new ArrayList<>();
        favList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this , RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RelatedNewsAdapter(this, wallpaperList);

        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.progressbar);
        dbWallpapers = FirebaseDatabase.getInstance().getReference("news");

        title = findViewById(R.id.news_title);
        date = findViewById(R.id.news_date);
        //categoryname = findViewById(R.id.news_category);
        webView = findViewById(R.id.news_desc);
        imageView = findViewById(R.id.news_image);
        fav_btn = findViewById(R.id.fav_btn);
        relativeLayout = findViewById(R.id.main_layout);
        progressBar = findViewById(R.id.progressbar);


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
        news_comment = findViewById(R.id.news_comment);
        news_comment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                createNewCommentDialog();
            }
        });

        //for the related news here
        dbWallpapers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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



        DatabaseReference reference_comment = FirebaseDatabase.getInstance().getReference().child("comments");

        DatabaseReference ref_user = reference_comment.child(String.valueOf(myid)).child("0").getRef();


        //DatabaseReference ref_comment = ref_user.child(String.valueOf(user_id)).getRef();

        //to show the comment in Firebase here

        listView = (ListView) findViewById(R.id.read_comment);

        ref_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    comment = child.getValue(String.class);
                    list.add(comment);

                    Toast.makeText(getApplicationContext(),
                            comment.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
            }
        });
        adapter_listview = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2,list);
        listView.setAdapter(adapter_listview);
        adapter_listview.notifyDataSetChanged();
    }

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
            case android.R.id.home:
                finish(); // close this activity and return to preview activity (if there is any)
                //adapter.notifyDataSetChanged();

                return true;

            case R.id.action_settings:

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBodyText = mytitle+"\nRead Full News :- \nhttps://play.google.com/store/apps/details?id="+getPackageName();
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);
                startActivity(Intent.createChooser(intent,"share via"));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void createNewCommentDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View commentView = getLayoutInflater().inflate(R.layout.activity_comments,null);

        //get id from user input
        user_image = (ImageView)commentView.findViewById(R.id.user_image);
        user_name_id = (EditText)commentView.findViewById(R.id.user_name);
        user_email_id = (EditText)commentView.findViewById(R.id.user_mail);
        user_comments_id = (EditText)commentView.findViewById(R.id.user_comment);

        //button click ?
        button_comments = (Button)commentView.findViewById(R.id.button_content);
        button_cancle = (Button)commentView.findViewById(R.id.button_cancle);


        user_image.setImageResource(R.drawable.avatar);
        dialogBuilder.setView(commentView);
        dialog = dialogBuilder.create();
        dialog.show();

        //firebase connection here
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("comments");


        // データベースの参照を取得する
        final DatabaseReference ref = mDatabaseReference.child(String.valueOf(myid)).child(String.valueOf(user_id)).getRef();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                count = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button_comments.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                //get text from id
                user_comments = user_comments_id.getText().toString();
                user_name = user_name_id.getText().toString();
                user_email = user_email_id.getText().toString();

                count = count + 1;

                ref.child("comment"+count).setValue(user_comments);

                Context context = getApplicationContext();

                Toast.makeText(context , "コメント追加に成功しました", Toast.LENGTH_LONG).show();

                dialog.cancel();
            }
        });

        button_cancle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.cancel();
            }
        });
    }


}

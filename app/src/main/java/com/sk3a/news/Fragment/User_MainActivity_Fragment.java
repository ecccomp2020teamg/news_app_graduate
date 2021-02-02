package com.sk3a.news.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sk3a.news.Login.User_Edit;
import com.sk3a.news.Login.User_Login;
import com.sk3a.news.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class User_MainActivity_Fragment extends Fragment {

    private static final int GALLERY_INTENT_CODE = 1023;
    // TEn nguoi dung,gmail,SDT
    TextView fullName, email, phone;
    //
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userId;
    StorageReference storageReference;
    // Nut dang xuat
    Button logout;
    // NUt thay doi password and images
    Button resetPassLocal, changeProfileImage;
    // Bien images
    ImageView profileImage;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.activity_info,container,false);

        phone = rootView.findViewById(R.id.profilePhone);//SDT
        fullName = rootView.findViewById(R.id.profileName);//user_name
        email = rootView.findViewById(R.id.profileEmail);//gmail adreess
        resetPassLocal = rootView.findViewById(R.id.resetPasswordLocal);//reset password
        logout = rootView.findViewById(R.id.button_logout);// Logout button
        profileImage = rootView.findViewById(R.id.profileImage);//images view
        changeProfileImage = rootView.findViewById(R.id.changeProfile);// thay doi images

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("個人情報");

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        setHasOptionsMenu(true);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Lay du lieu tu tai khoan google xuat ra man hinh thong tin ca nhan nguoi dung
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (signInAccount != null) {
            // lay ten tai khoan
            fullName.setText(signInAccount.getDisplayName());
            //lay dia chi gmail
            email.setText(signInAccount.getEmail());
            //lay hinh anh
            Uri photo = signInAccount.getPhotoUrl();
            Glide.with(getActivity()).load(String.valueOf(photo)).into(profileImage);
            phone.setHint("XXX-XXXX-XXXX");
        }

        // XU li nut logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                /*Intent intent = new Intent(getActivity(), User_Login.class);
                startActivity(intent);
                getActivity().finish();*/
                getFragmentManager().beginTransaction().replace(R.id.frame_layout, new User_Login_Fragment()).commit();
            }
        });

        // Tao thu muc luu tru anh nguoi dung duoc luu va quan lis ow muc Storaga o FireBase
        StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        // Hien thi thong tin nguoi dung facebook
        if(user!=null){
            // Lay hinh anh dai dien
            Glide.with(getActivity()).load(user.getPhotoUrl()).into(profileImage);
            // Lay dia chi gmail
            email.setText(user.getEmail());
            // lay ten nguoi dung Fb
            fullName.setText(user.getDisplayName());
            phone.setHint("未確認");

        }

        //Tao bang users tren firebase o muc cloud firestore
        final DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener( new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e != null){
                    System.err.println("Listen faild"+ e);
                    return;
                }
                if(documentSnapshot != null && documentSnapshot.exists()){

                    phone.setText(documentSnapshot.getString("Phone"));
                    fullName.setText(documentSnapshot.getString("UserName"));
                    email.setText(documentSnapshot.getString("Email"));

                }else {
                    System.out.print("Current data: null");
                }

            }
        });

        //Xu li du lieu cho phep nguoi dung thay doi mat khau
        resetPassLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.frame_layout, new User_ChangePassword_Fragment()).commit();
            }
        });
        // Thay doi profile cua nguoi dung
        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                /*Intent i = new Intent(v.getContext(), User_Edit.class);
                i.putExtra("UserName", fullName.getText().toString());
                i.putExtra("Email", email.getText().toString());
                i.putExtra("Phone", phone.getText().toString());

                startActivity(i);*/
                Fragment edit = new User_Edit_Fragment();
                FragmentTransaction fragmentManager =((FragmentActivity)getActivity()).getSupportFragmentManager()
                        .beginTransaction();

                Bundle args = new Bundle();
                args.putString("UserName", fullName.getText().toString());
                args.putString("Email", email.getText().toString());
                args.putString("Phone", phone.getText().toString());
                edit.setArguments(args);


                fragmentManager.replace(R.id.frame_layout, edit);
                fragmentManager.addToBackStack(null);
                fragmentManager.commit();

            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed(); // close this activity and return to preview activity (if there is any)
                //adapter.notifyDataSetChanged();
                //getFragmentManager().popBackStack();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

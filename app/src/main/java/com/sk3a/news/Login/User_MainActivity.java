package com.sk3a.news.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.sk3a.news.R;


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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import android.content.Intent;

public class User_MainActivity extends AppCompatActivity {

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info);
        phone = findViewById(R.id.profilePhone);//SDT
        fullName = findViewById(R.id.profileName);//user_name
        email = findViewById(R.id.profileEmail);//gmail adreess
        resetPassLocal = findViewById(R.id.resetPasswordLocal);//reset password
        logout = findViewById(R.id.button_logout);// Logout button
        profileImage = findViewById(R.id.profileImage);//images view
        changeProfileImage = findViewById(R.id.changeProfile);// thay doi images


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Lay du lieu tu tai khoan google xuat ra man hinh thong tin ca nhan nguoi dung
        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            // lay ten tai khoan
            fullName.setText(signInAccount.getDisplayName());
            //lay dia chi gmail
            email.setText(signInAccount.getEmail());
            //lay hinh anh
            Uri photo = signInAccount.getPhotoUrl();
            Glide.with(this).load(String.valueOf(photo)).into(profileImage);
            phone.setHint("XXX-XXXX-XXXX");
        }

        // XU li nut logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(com.sk3a.news.Login.User_MainActivity.this, User_Login.class);
                startActivity(intent);
                finish();
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
            Glide.with(this).load(user.getPhotoUrl()).into(profileImage);
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
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
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

                final EditText resetPassword = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("パスワードを変更 ?");
                passwordResetDialog.setMessage("6文字以上を入力してください！");
                passwordResetDialog.setView(resetPassword);
                passwordResetDialog.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        final String newPassword = resetPassword.getText().toString();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void aVoid) {
                                DocumentReference docRef = fStore.collection("users").document(user.getUid());
                                Map<String, Object> edited = new HashMap<>();
                                edited.put("Password",newPassword);
//                                edited.put("Password",resetPassLocal.getText().toString());
                                docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(com.sk3a.news.Login.User_MainActivity.this, "情報が保存された！", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                Toast.makeText(com.sk3a.news.Login.User_MainActivity.this, "パスワードを変更しました！", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(com.sk3a.news.Login.User_MainActivity.this, "エラーが発生しました！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close
                    }
                });

                passwordResetDialog.create().show();
            }
        });
        // Thay doi profile cua nguoi dung
        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                Intent i = new Intent(v.getContext(), User_Edit.class);
                i.putExtra("UserName", fullName.getText().toString());
                i.putExtra("Email", email.getText().toString());
                i.putExtra("Phone", phone.getText().toString());

                startActivity(i);
//

            }
        });

    }

}

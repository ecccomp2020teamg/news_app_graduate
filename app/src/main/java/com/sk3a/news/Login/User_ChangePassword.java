package com.sk3a.news.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sk3a.news.R;

import java.util.HashMap;
import java.util.Map;

public class User_ChangePassword extends AppCompatActivity {


    private EditText oldPass,newPass,confirmPass;
    private Button changePass;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__change_password);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        oldPass = findViewById(R.id.oldppassword);
        newPass = findViewById(R.id.newpassword);
        confirmPass =findViewById(R.id.confirnpassword);
        changePass = findViewById(R.id.resetPass);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txtOldPass = oldPass.getText().toString();
                String txtNewPass = newPass.getText().toString();
                String txtCOnfPass = confirmPass.getText().toString();

                if(txtOldPass.isEmpty() || txtNewPass.isEmpty() || txtCOnfPass.isEmpty()){
                    Toast.makeText(User_ChangePassword.this,"全ての入力が必要です。",Toast.LENGTH_SHORT).show();
                }else if(txtNewPass.length() < 6){
                    Toast.makeText(User_ChangePassword.this,"パスワードの長さは6文字以上です",Toast.LENGTH_SHORT).show();
                }else if(!txtCOnfPass.equals(txtNewPass)){
                    Toast.makeText(User_ChangePassword.this,"パスワードが間違いです",Toast.LENGTH_SHORT).show();
                }else{
                    changePassword(txtOldPass,txtNewPass);
                }
            }
        });

    }

    private void changePassword(String txtOldPass, final String txtNewPass) {
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),txtOldPass);
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    firebaseUser.updatePassword(txtNewPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                DocumentReference docRef = fStore.collection("users").document(user.getUid());
                                Map<String,Object> edited = new HashMap<>();
                                edited.put("Password",txtNewPass);
                                docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(User_ChangePassword.this, "", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                firebaseAuth.signOut();
                                Intent intent = new Intent(User_ChangePassword.this, User_Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }else {
                                Toast.makeText(User_ChangePassword.this,"パスワードが一致ではありません。",Toast.LENGTH_SHORT).show();
                                confirmPass.setText("");
                                newPass.setText("");
                            }
                        }
                    });
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(User_ChangePassword.this,"現在のパスワードが間違いです",Toast.LENGTH_SHORT).show();
                    oldPass.setText("");
                    confirmPass.setText("");
                    newPass.setText("");
                }
            }
        });
    }

}
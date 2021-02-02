package com.sk3a.news.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
import com.sk3a.news.Login.User_ChangePassword;
import com.sk3a.news.Login.User_Login;
import com.sk3a.news.R;

import java.util.HashMap;
import java.util.Map;

import com.sk3a.news.R;

public class User_ChangePassword_Fragment extends Fragment {

    private EditText oldPass,newPass,confirmPass;
    private Button changePass;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.activity_user__change_password,container,false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        oldPass = rootView.findViewById(R.id.oldppassword);
        newPass = rootView.findViewById(R.id.newpassword);
        confirmPass =rootView.findViewById(R.id.confirnpassword);
        changePass = rootView.findViewById(R.id.resetPass);
        progressBar = rootView.findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("パスワード変更");

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        setHasOptionsMenu(true);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txtOldPass = oldPass.getText().toString();
                String txtNewPass = newPass.getText().toString();
                String txtCOnfPass = confirmPass.getText().toString();

                if(txtOldPass.isEmpty() || txtNewPass.isEmpty() || txtCOnfPass.isEmpty()){
                    Toast.makeText(getActivity(),"全ての入力が必要です。",Toast.LENGTH_SHORT).show();
                }else if(txtNewPass.length() < 6){
                    Toast.makeText(getActivity(),"パスワードの長さは6文字以上です",Toast.LENGTH_SHORT).show();
                }else if(!txtCOnfPass.equals(txtNewPass)){
                    Toast.makeText(getActivity(),"パスワードが間違いです",Toast.LENGTH_SHORT).show();
                }else{
                    changePassword(txtOldPass,txtNewPass);
                }
            }
        });



        return rootView;
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
                                        //getActivity().finish();
                                    }
                                });
                                firebaseAuth.signOut();
                                getFragmentManager().beginTransaction().replace(R.id.frame_layout,new User_Login_Fragment()).commit();

                            }else {
                                Toast.makeText(getActivity(),"パスワードが一致ではありません。",Toast.LENGTH_SHORT).show();
                                confirmPass.setText("");
                                newPass.setText("");
                            }
                        }
                    });
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),"現在のパスワードが間違いです",Toast.LENGTH_SHORT).show();
                    oldPass.setText("");
                    confirmPass.setText("");
                    newPass.setText("");
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                //getActivity().onBackPressed(); // close this activity and return to preview activity (if there is any)
                //adapter.notifyDataSetChanged();
                getFragmentManager().popBackStack();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

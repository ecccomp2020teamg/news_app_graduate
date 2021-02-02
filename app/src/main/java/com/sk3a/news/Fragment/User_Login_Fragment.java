package com.sk3a.news.Fragment;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sk3a.news.DetailsActivity;
import com.sk3a.news.Login.User_Register;
import com.sk3a.news.R;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import com.sk3a.news.R;

public class User_Login_Fragment extends Fragment {

    String login_confirm = "0";

    TextView fb_btn;
    private static final int RC_SIGN_IN = 123;

    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mCreateBtn,forgotTextLink;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    TextView google_btn;
    GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.activity_login,container,false);

        mEmail = rootView.findViewById(R.id.Email);
        mPassword = rootView.findViewById(R.id.password);
        progressBar = rootView.findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = rootView.findViewById(R.id.loginBtn);
        mCreateBtn = rootView.findViewById(R.id.createText);
        forgotTextLink = rootView.findViewById(R.id.forgotPassword);

        //User_Login with facebook account
        // Initialize Facebook User_Login button
        fb_btn = (TextView) rootView.findViewById(R.id.facebook_login);
        mCallbackManager = CallbackManager.Factory.create();

        fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Truy xuat trang nhap facebook va lay du lieu tu Fb
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("TAG", "facebook:onSuccess:" + loginResult);
                        //handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("TAG", "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("TAG", "facebook:onError", error);
                        // ...
                    }
                });
            }
        });


        //User_Login with google account
        google_btn= rootView.findViewById(R.id.google_login);
        google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRequest();
                signIn();
            }
        });

        // XU ly du lieu khi nguoi dung click vao login button
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("登録したメールアドレスを利用してください。");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("パスワードを入力してください");
                    return;
                }
                // mat khau pai co it nhat 6 ky tu
                if(password.length() < 6){
                    mPassword.setError("パスワードは6文字以上が必要です。");
                    mPassword.setText("");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                // authenticate the user

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "ログインしました！", Toast.LENGTH_SHORT).show();

                            Fragment selectedFragement = new User_MainActivity_Fragment();

                            getFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragement).commit();

                        }else {
                            Toast.makeText(getActivity(), "メールアドレスまたパスワードが間違いました! ", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            mPassword.setText("");
                        }

                    }
                });

            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*startActivity(new Intent(getActivity(), User_Register.class));*/
                //Fragment selectedFragement = new User_Register().class;
                getFragmentManager().beginTransaction().replace(R.id.frame_layout, new User_Register_Fragment()).addToBackStack(null).commit();
            }
        });

        //CHo phep nguoi dung tim lai mat khau qua gmail
        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.frame_layout, new User_ForgotPassword_Fragment()).commit();
            }
        });


        return rootView;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getActivity(),e.toString(), Toast.LENGTH_LONG).show();
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(getActivity(), user.getEmail(), Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }

                    private void updateUI(FirebaseUser user) {
                        //Intent intent = new Intent(getActivity(), com.sk3a.news.Fragment.User_MainActivity_Fragment.class);
                        //startActivity(intent);

                        Fragment selectedFragement = new User_MainActivity_Fragment();

                        getFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragement).commit();
                    }
                });

    }

    private void createRequest(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = fAuth.getCurrentUser();
                            //startActivity(new Intent(getActivity(), com.sk3a.news.Fragment.User_MainActivity_Fragment.class));
                            //getActivity().finish();

                            Fragment a = new User_MainActivity_Fragment();

                            getFragmentManager().beginTransaction().replace(R.id.frame_layout, a).commit();
                            getActivity().finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
    public void onStart() {

        super.onStart();
        FirebaseUser user = fAuth.getCurrentUser();
        if(user!=null){
            //startActivity(new Intent(getActivity(), com.sk3a.news.Fragment.User_MainActivity_Fragment.class));
            //getActivity().finish();
            Fragment b = new User_MainActivity_Fragment();

            getFragmentManager().beginTransaction().replace(R.id.frame_layout, b).commit();
            //getActivity().finish();
        }

    }

}

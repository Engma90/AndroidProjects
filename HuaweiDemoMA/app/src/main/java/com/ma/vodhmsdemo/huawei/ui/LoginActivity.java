package com.ma.vodhmsdemo.huawei.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.huawei.hms.auth.api.signin.HuaweiIdSignIn;
import com.huawei.hms.auth.api.signin.HuaweiIdSignInClient;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.ma.vodhmsdemo.huawei.R;
import com.ma.vodhmsdemo.huawei.repos.SharedPref;
import com.ma.vodhmsdemo.huawei.repos.UserRepo;
import com.ma.vodhmsdemo.huawei.utils.Constant;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    ImageView btnLoginToken;
    ProgressBar progressBar;
    UserViewModel userViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPref.init(getApplicationContext());
        progressBar = findViewById(R.id.progressBar);
        btnLoginToken = findViewById(R.id.btnLoginToken);
        btnLoginToken.setOnClickListener(this);


        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getLiveData().observe(this, userModel -> {
            if(!userModel.getName().isEmpty()) {
                Intent videoIntent = new Intent(LoginActivity.this, VideoActivity.class);
                videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(videoIntent);
                UserRepo.getInstance().setIsLoggedInBefore(true);
                finish();
            }
            else{
                progressBar.setVisibility(View.GONE);
                btnLoginToken.setVisibility(View.VISIBLE);
            }
        });

        if(UserRepo.getInstance().isLoggedInBefore()){
            startLoginProcess();
        }
        else {
            progressBar.setVisibility(View.GONE);
            btnLoginToken.setVisibility(View.VISIBLE);
        }
    }

    private void startLoginProcess(){
        Log.i(TAG, "starting Login Process");
        HuaweiIdSignInOptions mSignInOptions = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN).requestIdToken("").build();
        HuaweiIdSignInClient mSignInClient = HuaweiIdSignIn.getClient(LoginActivity.this, mSignInOptions);
        startActivityForResult(mSignInClient.getSignInIntent(), Constant.REQUEST_SIGN_IN_LOGIN);
    }

    @Override
    public void onClick(View view) {
        if (view == btnLoginToken) {
            startLoginProcess();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_SIGN_IN_LOGIN) {
                userViewModel.loadLiveData(data);
        }
    }
}

package com.ma.vodhmsdemo.huawei.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.auth.api.signin.HuaweiIdSignIn;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.ma.vodhmsdemo.huawei.models.UserModel;
import com.ma.vodhmsdemo.huawei.repos.UserRepo;

public class UserViewModel extends ViewModel {
    private static final String TAG = "UserViewModel";

    private static MutableLiveData<UserModel> liveData;
    LiveData<UserModel> getLiveData(){
        if(liveData == null) {
            liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    void loadLiveData(Intent data){
        UserModel userModel = null;
        Task<SignInHuaweiId> signInHuaweiIdTask = HuaweiIdSignIn.getSignedInAccountFromIntent(data);
        if (signInHuaweiIdTask.isSuccessful()) {
            SignInHuaweiId huaweiAccount = signInHuaweiIdTask.getResult();

            userModel = new UserModel(huaweiAccount.getId(),
                    huaweiAccount.getDisplayName(),
                    huaweiAccount.getPhotoUrl().toString(),
                    null);
            liveData.setValue(userModel);

        } else {
            Log.e(TAG, "sign in failed : " +((ApiException)signInHuaweiIdTask
                    .getException()).getStatusCode());
        }
        if(userModel != null)
            new AvatarAsyncTask().execute(userModel);
    }

    private static class AvatarAsyncTask extends AsyncTask<UserModel, Void, Bitmap> {
        UserModel model;
        protected Bitmap doInBackground(UserModel... params) {
            this.model = params[0];
            return UserRepo.getInstance().getUserImage(params[0].getPhotoUrl());
        }

        protected void onPostExecute(Bitmap result) {
            this.model.setPhoto(result);
            liveData.setValue(this.model);
        }

    }
}

package com.ma.vodhmsdemo.huawei.data;

import android.graphics.Bitmap;
import com.ma.vodhmsdemo.huawei.data.local.SharedPref;

import com.ma.vodhmsdemo.huawei.data.local.Storage;
import com.ma.vodhmsdemo.huawei.data.remote.Cloud;

public final class UserRepository {
    private static UserRepository instance = null;
    private UserRepository(){

    }
    public static UserRepository getInstance(){
        if (instance == null)
        {
            instance = new UserRepository();
        }
        return instance;
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    public Bitmap getUserImage(String photoUrl){
        Bitmap bitmap = Cloud.getRemoteUserImage(photoUrl);
        if(bitmap == null)
            bitmap = Storage.getCachedImage();
        return bitmap;
    }


    public boolean isLoggedInBefore(){
        return SharedPref.read(SharedPref.IS_LOGGED_IN_BEFORE, false);
    }

    public void setIsLoggedInBefore(boolean val){
        SharedPref.write(SharedPref.IS_LOGGED_IN_BEFORE, val);
    }
}

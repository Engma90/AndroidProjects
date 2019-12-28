package com.ma.vodhmsdemo.huawei.repos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class UserRepo {
    private static UserRepo instance = null;
    private UserRepo(){

    }
    public static UserRepo getInstance(){
        if (instance == null)
        {
            instance = new UserRepo();
        }
        return instance;
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    public Bitmap getUserImage(String photoUrl){
        try {
            URL url = new URL(photoUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch(IOException e) {
            return getCachedImage(); // try to get the cached one
        }
    }
    private Bitmap getCachedImage(){
        //Todo: implement caching logic.
        try {

        }catch (Exception e){
            return null;
        }
        return null;
    }

    public boolean isLoggedInBefore(){

        return SharedPref.read(SharedPref.IS_LOGGED_IN_BEFORE, false);
    }

    public void setIsLoggedInBefore(boolean val){
        SharedPref.write(SharedPref.IS_LOGGED_IN_BEFORE, val);
    }
}

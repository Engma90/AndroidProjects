package com.ma.vodhmsdemo.huawei.data.remote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Cloud {
    public static Bitmap getRemoteUserImage(String photoUrl){
        try {
            URL url = new URL(photoUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch(IOException e) {
            return null;
        }
    }
}

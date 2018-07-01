package com.allvoes.lunachat;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class Luna extends Application {




    @Override
    public void onCreate() {
        super.onCreate();



        FirebaseDatabase.getInstance().setPersistenceEnabled(true);



        Picasso.Builder B = new Picasso.Builder(this);
        B.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso b = B.build();
        b.setIndicatorsEnabled(true);
        b.setLoggingEnabled(true);
        Picasso.setSingletonInstance(b);

    }





}

package com.allvoes.lunachat;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.database.ServerValue;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    ArrayList<File> mlist;
    private GridLayoutManager mgridView;
    private RecyclerView recyclerView;
    private GalleryAdapterz galleryAdapterz;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerView = (RecyclerView)findViewById(R.id.gallery_recycle);
        mlist = imageRender(Environment.getExternalStorageDirectory());
        galleryAdapterz = new GalleryAdapterz(this,mlist);
        mgridView = new GridLayoutManager(this,3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mgridView);
        recyclerView.setAdapter(galleryAdapterz);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent returnIntent = new Intent();
                File c =mlist.get(position);
                String result = c.toURI().toString();
                returnIntent.putExtra("result",result);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();

            }

            @Override
            public void onLongClick(View view, int position) {
                Intent i = new Intent(getApplicationContext(),FullPictureActivity.class);
                i.putExtra("list",mlist);
                i.putExtra("position",position);
                startActivity(i);
            }
        }));
    }


    private ArrayList<File> imageRender(File externalStorageDirectory) {

        ArrayList<File> b = new ArrayList<>();

        File[] files = externalStorageDirectory.listFiles();
        for (int i = 0; i<files.length; i++){
            if(files[i].isDirectory()){
                b.addAll(imageRender(files[i]));

            }else {
                if(files[i].getName().endsWith(".jpg")){
                    b.add(files[i]);
                }
            }
        }
        return b;
    }


}

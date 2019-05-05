package com.allvoes.lunachat;

import android.net.Uri;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.allvoes.lunachat.Image.customviewGroup;

import java.io.File;
import java.util.ArrayList;

public class FullPictureActivity extends AppCompatActivity {

    ImageView imageView;
    ViewPager viewPager;
    ArrayList<File> mList;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_picture);

        viewPager = (ViewPager) findViewById(R.id.viewphoto);
        position = getIntent().getIntExtra("position", 1);
        mList = (ArrayList<File>) getIntent().getSerializableExtra("list");
        FullPictureAdapter adapter = new FullPictureAdapter(this, mList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position, true);

    }
}

package com.allvoes.lunachat;

import android.content.Context;
import android.graphics.Matrix;
import android.net.Uri;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;



import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

public class FullPictureAdapter extends PagerAdapter {
    Context context;
    ArrayList<File> mList;
    LayoutInflater layoutInflater;
    ImageView imageView ;
    View mView;
    Matrix matrix  = new Matrix();
    private float mScaleFactor = 1.0f;
    ScaleGestureDetector SGD;




    public FullPictureAdapter(Context context, ArrayList<File> mList) {
        this.context = context;
        this.mList = mList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        File c = mList.get(position);
        mView = layoutInflater.inflate(R.layout.full_img,null);
        imageView = (ImageView) mView.findViewById(R.id.photo_full);
        imageView.setImageURI(Uri.parse(c.toString()));

//        imageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return SGD.onTouchEvent(event);
//            }
//        });
//        Animation zoomAnim = AnimationUtils.loadAnimation(context,R.anim.zoom);
//        imageView.startAnimation(zoomAnim);
//        SGD  = new ScaleGestureDetector(context,new scaleListener());
        ViewPager viewPager = (ViewPager)container;
        viewPager.addView(mView,-1);
        return mView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager)container;
        View mView = (View)object;
        viewPager.removeView(mView);
    }

}

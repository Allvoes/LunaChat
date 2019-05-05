package com.allvoes.lunachat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class GalleryAdapterz extends  RecyclerView.Adapter<GalleryAdapterz.GalleryViewHodel> {

    private ArrayList<File> mlist;
    LayoutInflater mLayoutInflater;
    Context context;


    public GalleryAdapterz(Context c, ArrayList<File> mlist) {
        this.context = c;
        this.mlist = mlist;
        this.mLayoutInflater = LayoutInflater.from(c);

    }

    @NonNull
    @Override
    public GalleryViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new GalleryViewHodel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  GalleryViewHodel holder,int position) {
        File c = mlist.get(position);
        holder.setimg(c.toURI());

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class GalleryViewHodel extends RecyclerView.ViewHolder {
        View mView;

        public GalleryViewHodel(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void  setimg(final URI uri){
            ImageView m = (ImageView) mView.findViewById(R.id.imageGallery);
            m.setImageURI(Uri.parse(uri.toString()));

        }

    }
}

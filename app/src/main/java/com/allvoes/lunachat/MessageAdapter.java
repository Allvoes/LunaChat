package com.allvoes.lunachat;

import android.app.Notification;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<messages> mMessagesList;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;



    public MessageAdapter(List<messages> mMessagesList) {
        this.mMessagesList = mMessagesList;
    }



    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        messages c = mMessagesList.get(position);
        String from_user = c.getFrom();
        String message_type = c.getType();
        if(from_user.equals(current_user_id) && message_type.equals("text")){
            return 1;
        }else if(from_user.equals(current_user_id) && message_type.equals("image")){
            return 3;
        }else if(!from_user.equals(current_user_id) && message_type.equals("text")){
            return 2;
        }else {
            return 4;
        }


    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder ;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case 1:
                View view1 = inflater.inflate(R.layout.messages_single_layout2,parent,false);
                viewHolder = new MessageViewHolder(view1);
                break;
            case 2:
                View view2 = inflater.inflate(R.layout.messages_single_layout,parent,false);
                viewHolder = new MessageViewHolder(view2);
                break;

            case 3:
                View view3 = inflater.inflate(R.layout.messages_img_layout2, parent, false);
                viewHolder = new MessageViewHolder(view3);
                break;

            case 4:
                View view4 = inflater.inflate(R.layout.messages_img_layout, parent, false);
                viewHolder = new MessageViewHolder(view4);
                break;
            default:
                    View view = inflater.inflate(R.layout.messages_single_layout2,parent,false);
                    viewHolder = new MessageViewHolder(view);
                    break;
        }
        return (MessageViewHolder) viewHolder;
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder {
        View mView;


        public MessageViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setMessage(String message) {
            final TextView messagetext = (TextView) mView.findViewById(R.id.mess_text);
            final TextView time = (TextView) mView.findViewById(R.id.time_text_layout);
            final TextView seen = (TextView) mView.findViewById(R.id.textView5);
            messagetext.setText(message);
//            messagetext.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    getItemCount();
//                    time.setVisibility(View.VISIBLE);
//                    seen.setVisibility(View.VISIBLE);
//                }
//            });
        }

        public void  setImg (final String message){
            final ImageView m = (ImageView) mView.findViewById(R.id.image_reci);
            final TextView time = (TextView) mView.findViewById(R.id.time_text_layout);
            final TextView seen = (TextView) mView.findViewById(R.id.textView5);
            Picasso.get().load(message).networkPolicy(NetworkPolicy.OFFLINE).into(m, new Callback() {
                @Override
                public void onSuccess() {
                    Picasso.get().load(message).placeholder(R.drawable.default_avatar).into(m);
//                    m.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            getItemCount();
//                            time.setVisibility(View.VISIBLE);
//                            seen.setVisibility(View.VISIBLE);
//                        }
//                    });
                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(message).placeholder(R.drawable.default_avatar).into(m);
                }
            });
        }
//        public void isSeen (String seen){
//            TextView textseen = (TextView) mView.findViewById(R.id.textView5);
//            textseen.setText(seen);
//
//            if(seen == false){
//                textseen.setText("Delivered");
//            }else if(seen == true){
//                textseen.setText("seen");
//            }
//        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {

        messages c = mMessagesList.get(position);

        String message_type = c.getType();
//        boolean seen = c.isSeen();


        if(message_type.equals("text")){
            holder.setMessage(c.getMessage());
        }else{
            holder.setImg(c.getMessage());
        }

//        if(c.isSeen()){
//            holder.isSeen("seen");
//        }else {
//            holder.isSeen("Delivered");
//        }

    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }


}




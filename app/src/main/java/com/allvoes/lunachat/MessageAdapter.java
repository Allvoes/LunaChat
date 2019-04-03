package com.allvoes.lunachat;

import android.app.Notification;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
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
        if(from_user.equals(current_user_id)){
            return 1;
        }else{
            return 2;
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
            default:
                    View view = inflater.inflate(R.layout.messages_single_layout2,parent,false);
                    viewHolder = new MessageViewHolder(view);
                    break;


        }

        return (MessageViewHolder) viewHolder;
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TextView messagetext;
//        public CircleImageView profileimg;

        public MessageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setMessage(String message) {
            messagetext = (TextView) mView.findViewById(R.id.mess_text);
            messagetext.setText(message);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        messages c = mMessagesList.get(position);
        holder.setMessage(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }


}




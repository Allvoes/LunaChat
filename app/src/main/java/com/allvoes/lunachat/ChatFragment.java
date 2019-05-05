package com.allvoes.lunachat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {

    private RecyclerView mConvList;
    private DatabaseReference mDatabase,mMessageDatabase,mFriendDatabase,mUserDatabase,mConvesationDatabase;
    private FirebaseAuth mAuth;
    private String m_current_user;
    private View mView;

    public ChatFragment() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_chat, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mConvList = (RecyclerView) mView.findViewById(R.id.Conv_list);

        m_current_user =  mAuth.getCurrentUser().getUid();
        mConvesationDatabase = mDatabase.child("chat").child(m_current_user);
        mFriendDatabase = mDatabase.child("Friend").child(m_current_user);
        mUserDatabase = mDatabase.child("User");
        mMessageDatabase = mDatabase.child("message").child(m_current_user);
        mDatabase.keepSynced(true);

        mConvList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mConvList.setLayoutManager(linearLayoutManager);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery = mConvesationDatabase.orderByChild("lastseen");
        FirebaseRecyclerOptions<Conv> options =
                new FirebaseRecyclerOptions.Builder<Conv>()
                        .setQuery(conversationQuery, Conv.class)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Conv,ConvViewHodel>(options) {
            @NonNull
            @Override
            public ConvViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent, false);
                return new ConvViewHodel(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ConvViewHodel holder, int position, @NonNull final Conv model) {
                final String list_friend = getRef(position).getKey();
                Query lastmessageQuery = mMessageDatabase.child(list_friend).limitToLast(1);
                lastmessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String data = dataSnapshot.child("message").getValue().toString();
                        holder.setMessage(data,model.isSeen());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mUserDatabase.child(list_friend).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();
                        final String thumb_img = dataSnapshot.child("Thumb_image").getValue().toString();
                        if(dataSnapshot.hasChild("online")){

                            String online =(String) dataSnapshot.child("online").getValue().toString();
                            holder.Setuseronline(online);
                        }
                        holder.setName(name);
                        holder.setImage(thumb_img);
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getActivity(),ChatActivity.class);
                                i.putExtra( "user_id",list_friend);
                                i.putExtra("user_name",name);
                                i.putExtra("user_img",thumb_img);
                                startActivity(i);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        };
        mConvList.setAdapter(adapter);
        adapter.startListening();
    }

    public interface OnFragmentInteractionListener {
    }


    private static class ConvViewHodel extends RecyclerView.ViewHolder {
        View mView;
        public ConvViewHodel(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name) {
            TextView usernameview = (TextView) mView.findViewById(R.id.user_single_name);
            usernameview.setText(name);
        }
        public void setMessage(String message,boolean isSeen){
            TextView userstatusview = (TextView) mView.findViewById(R.id.user_single_status);
            userstatusview.setText(message);
            if(!isSeen){
                userstatusview.setTypeface(userstatusview.getTypeface(), Typeface.BOLD);
                userstatusview.setTextSize(34);
            }else {
                userstatusview.setTypeface(userstatusview.getTypeface(), Typeface.NORMAL);
            }
        }
        public void setImage(final String image) {

            final CircleImageView m = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(m, new Callback() {
                @Override
                public void onSuccess() {
                    Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(m);
                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(m);
                }
            });
        }
        public void Setuseronline(String online){
            CircleImageView online_status = (CircleImageView) mView.findViewById(R.id.online);
            if(online.equals("true")){
                online_status.setVisibility(View.VISIBLE);
            }else {
                online_status.setVisibility(View.INVISIBLE);
            }
        }
    }
}

package com.allvoes.lunachat;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    private RecyclerView mFriendList;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String m_current_user;
    private View mView;



    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_friend, container, false);

        mFriendList = (RecyclerView) mView.findViewById(R.id.friend_list);
        mAuth = FirebaseAuth.getInstance();
        m_current_user =  mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Friend").child(m_current_user);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        mUserDatabase.keepSynced(true);
        mDatabase.keepSynced(true);
        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        return  mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friend> options =
                new FirebaseRecyclerOptions.Builder<Friend>()
                        .setQuery(mDatabase, Friend.class)
                        .build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friend,FriendViewHolder>(options) {

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent, false);
                return new FriendViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull final FriendViewHolder holder, final int position, @NonNull Friend model) {

                final String list_friend = getRef(position).getKey();
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
                                CharSequence options[] = new CharSequence[]{"Send Messgasing","Open Profile","Gallery"};
                                AlertDialog.Builder  builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which == 0){
                                            Intent i = new Intent(getActivity(),ChatActivity.class);
                                            i.putExtra( "user_id",list_friend);
                                            i.putExtra("user_name",name);
                                            i.putExtra("user_img",thumb_img);
                                            startActivity(i);
                                        }
                                        if(which == 1){
                                            Intent i = new Intent(getActivity(),ProfileActivity.class);
                                            i.putExtra( "user_id",list_friend);
                                            startActivity(i);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.setDate(model.getDate());
            }
        };
        mFriendList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FriendViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView usernameview = (TextView) mView.findViewById(R.id.user_single_name);
            usernameview.setText(name);
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

        public void  setDate(String date) {
            TextView usernameview = (TextView) mView.findViewById(R.id.user_single_status);
            usernameview.setText(date);
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

package com.allvoes.lunachat;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {


    private Toolbar mtoobar;
    private RecyclerView mUserList;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrnetUser;
    private LinearLayoutManager mlinearlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mtoobar = (Toolbar) findViewById(R.id.user_appbar);
        setSupportActionBar(mtoobar);
        getSupportActionBar().setTitle("List of User");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mCurrnetUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mCurrnetUser.getUid());

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        mUserList = (RecyclerView)findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.child("online").setValue("true");

        FirebaseRecyclerOptions<listUser> options =
                new FirebaseRecyclerOptions.Builder<listUser>()
                        .setQuery(mUserDatabase, listUser.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<listUser, UsersViewHolder>(options) {

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent, false);
                return new UsersViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder holder, int position, @NonNull final listUser model) {


                holder.setName(model.getName());
                holder.setImage(model.getThumb_image());
                holder.setStatus(model.getStatus());

                final String id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(UsersActivity.this,ProfileActivity.class);
                        i.putExtra( "user_id",id);
                        startActivity(i);
                    }
                });
            }

        };

        mUserList.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabase.child("online").setValue(ServerValue.TIMESTAMP);
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
            TextView usernameview = (TextView) mView.findViewById(R.id.user_single_name);
            usernameview.setText(name);
        }
        public void setImage(final String image) {
            final CircleImageView m = (CircleImageView)mView.findViewById(R.id.user_single_image);
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
        public void setStatus(String status) {
            TextView usernameview = (TextView) mView.findViewById(R.id.user_single_status);
            usernameview.setText(status);
        }
        public void setuseronline(String online){
            CircleImageView online_status = (CircleImageView) mView.findViewById(R.id.online);
            if(online.equals("true")){
                online_status.setVisibility(View.VISIBLE);
            }else {
                online_status.setVisibility(View.INVISIBLE);
            }
        }
    }
}

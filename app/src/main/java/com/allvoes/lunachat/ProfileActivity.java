package com.allvoes.lunachat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    private ImageView mImages;
    private TextView mDisplayname,mStatus,mTotalfriend;
    private Button mSendbtn,mdelinebtn;

    private String mcurrent_state;

    private DatabaseReference mFriendrequest,mUserDatabase,mFriends,mCloudMess;
    private ProgressDialog mdialog;
    private FirebaseUser mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_uid = getIntent().getStringExtra("user_id");

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(user_uid);
        mFriendrequest = FirebaseDatabase.getInstance().getReference().child("friend_ref");
        mFriends = FirebaseDatabase.getInstance().getReference().child("Friend");
        mCloudMess = FirebaseDatabase.getInstance().getReference().child("notifications");


        mUid = FirebaseAuth.getInstance().getCurrentUser();



        mImages = (ImageView)findViewById(R.id.Profile_image);
        mDisplayname = (TextView)findViewById(R.id.Profile_displayname);
        mStatus =(TextView)findViewById(R.id.Profile_status);
        mTotalfriend  =(TextView)findViewById(R.id.Profile_totalfriend);
        mSendbtn=(Button)findViewById(R.id.Profile_sendbtn);
        mdelinebtn=(Button)findViewById(R.id.profile_sendbtn2);


        mcurrent_state = "no_friend";

        mdelinebtn.setVisibility(View.INVISIBLE);


        mdialog = new ProgressDialog(this);
        mdialog.setTitle("Loading user Data");
        mdialog.setMessage("Please wait white we load data !!!");
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();








        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String display_status = dataSnapshot.child("status").getValue().toString();
                String display_image = dataSnapshot.child("Thumb_image").getValue().toString();

                mDisplayname.setText(display_name);
                mStatus.setText(display_status);
                Picasso.get().load(display_image).placeholder(R.drawable.default_avatar).into(mImages);

                //----------------------------btn State-------------------------------


                mdialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });




        mFriendrequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFriendrequest.child(mUid.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_uid)){
                            String rep_data = dataSnapshot.child(user_uid).child("request_type").getValue().toString();
                            if(rep_data.equals("received")){
                                mcurrent_state="req_recevied";
                                mSendbtn.setText("Accept Friend Request");
                                mdelinebtn.setVisibility(View.VISIBLE);
                            }else if(rep_data.equals("sent")){
                                mcurrent_state="req_send";
                                mSendbtn.setText("Cancel Send Request");
                            }

                        }else if (!dataSnapshot.exists()){
                            mFriends.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    mFriends.child(mUid.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(user_uid)){
                                                mcurrent_state="friend";
                                                mSendbtn.setText("Unfriend");
                                                mdelinebtn.setVisibility(View.INVISIBLE);
                                            }else if(!dataSnapshot.exists()){
                                                mcurrent_state="no_friend";
                                                mSendbtn.setText("Send Friend Request");
                                                mdelinebtn.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });









        mSendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //----------------------------------Send Friend Request-----------------------------------
                if(mcurrent_state.equals("no_friend")){

                    mFriendrequest.child(mUid.getUid()).child(user_uid).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull final Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendrequest.child(user_uid).child(mUid.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String ,String> notifications = new HashMap<>();
                                        notifications.put("From :", mUid.getUid());
                                        notifications.put("Type :","Friend Request");


                                        mCloudMess.child(user_uid).push().setValue(notifications).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mdelinebtn.setVisibility(View.INVISIBLE);
                                                mcurrent_state="req_send";
                                                mSendbtn.setText("Cancel Send Request");
                                                Toast.makeText(ProfileActivity.this,"Request Send Success!",Toast.LENGTH_LONG).show();
                                            }

                                        });



                                    }
                                });
                            }else {
                                Toast.makeText(ProfileActivity.this,"Request send fail!!!",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    }
                //--------------------------------Recevied Friend Request------------------
                if(mcurrent_state.equals("req_send")){

                    mFriendrequest.child(mUid.getUid()).child(user_uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendrequest.child(user_uid).child(mUid.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    

                                    mdelinebtn.setVisibility(View.INVISIBLE);
                                    mcurrent_state="no_friend";
                                    mSendbtn.setText("Send Friend Request");



                                }
                            });
                        }
                    });
                }
                //----------------------------Accept Request-------------------------------
                if(mcurrent_state.equals("req_recevied")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mFriends.child(mUid.getUid()).child(user_uid).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriends.child(user_uid).child(mUid.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendrequest.child(mUid.getUid()).child(user_uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendrequest.child(user_uid).child(mUid.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mdelinebtn.setVisibility(View.INVISIBLE);
                                                    mcurrent_state="friend";
                                                    mSendbtn.setText("Unfriend");



                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                    });
                }
                //-------------------------------Unfriend--------------------------------------------
                if(mcurrent_state.equals("friend")){
                    mFriends.child(mUid.getUid()).child(user_uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriends.child(user_uid).child(mUid.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mcurrent_state="no_friend";
                                    mSendbtn.setText("send friend request");

                                }
                            });
                        }
                    });
                }
            }

        });

        mdelinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendrequest.child(mUid.getUid()).child(user_uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendrequest.child(user_uid).child(mUid.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                mdelinebtn.setVisibility(View.INVISIBLE);
                                mcurrent_state="no_friend";
                                mSendbtn.setText("Send Friend Request");




                            }
                        });
                    }
                });
            }
        });
    }


}

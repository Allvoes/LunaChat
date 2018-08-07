package com.allvoes.lunachat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {


    private ImageView mImages;
    private TextView mDisplayname,mStatus,mTotalfriend;
    private Button mSendbtn,mdelinebtn;

    private String mcurrent_state;

    private DatabaseReference mFriendrequest,mUserDatabase,mFriends,mCloudMess,mRootRef;
    private FirebaseAuth mAuth;
    private ProgressDialog mdialog;
    private FirebaseUser mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_uid;
        String data = getIntent().getStringExtra("user_id");
        if(data == null){
            user_uid= getIntent().getStringExtra("from_user_id");
        }else{
            user_uid = getIntent().getStringExtra("user_id");
        }

        mImages = (ImageView)findViewById(R.id.Profile_image);
        mDisplayname = (TextView)findViewById(R.id.Profile_displayname);
        mStatus =(TextView)findViewById(R.id.Profile_status);
        mTotalfriend  =(TextView)findViewById(R.id.Profile_totalfriend);
        mSendbtn=(Button)findViewById(R.id.Profile_sendbtn);
        mdelinebtn=(Button)findViewById(R.id.profile_sendbtn2);
        mcurrent_state = "no_friend";
        mdelinebtn.setVisibility(View.INVISIBLE);





        mUid = FirebaseAuth.getInstance().getCurrentUser();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(user_uid);
        mFriendrequest = FirebaseDatabase.getInstance().getReference().child("friend_ref");
        mFriends = FirebaseDatabase.getInstance().getReference().child("Friend");
        mCloudMess = FirebaseDatabase.getInstance().getReference().child("notifications");


        mdialog = new ProgressDialog(this);
        mdialog.setTitle("Loading user Data");
        mdialog.setMessage("Please wait white we load data !!!");
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();







        //----------------------------GetData--------------------------------------------------
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String display_status = dataSnapshot.child("status").getValue().toString();
                String display_image = dataSnapshot.child("Thumb_image").getValue().toString();

                mDisplayname.setText(display_name);
                mStatus.setText(display_status);

                Picasso.get().load(display_image).placeholder(R.drawable.default_avatar).into(mImages);

                mdialog.dismiss();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
        //----------------------------Default button state ------------------------------------
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
        //----------------------------On-Click button state------------------------------------
        mSendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //----------------------------------Send Friend Request-----------------------------------
                if(mcurrent_state.equals("no_friend")){

                    DatabaseReference newNotifi = mRootRef.child("notifications").child(user_uid).push();
                    String newNotifiID = newNotifi.getKey();

                    HashMap<String ,String> notificationData = new HashMap<>();
                    notificationData.put("From", mUid.getUid());
                    notificationData.put("Type","Friend Request");

                    Map sendMap = new HashMap();
                    sendMap.put("friend_ref/"+ mUid.getUid() +"/"+ user_uid +"/request_type","sent");
                    sendMap.put("friend_ref/"+ user_uid +"/"+ mUid.getUid() +"/request_type","received");
                    sendMap.put("notifications/"+ user_uid +"/"+ newNotifiID,notificationData);

                    mRootRef.updateChildren(sendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError!=null){

                                Toast.makeText(ProfileActivity.this,"error! restart app and try again!",Toast.LENGTH_LONG).show();

                            }else {

                                mdelinebtn.setVisibility(View.INVISIBLE);
                                mcurrent_state="req_send";
                                mSendbtn.setText("Cancel Send Request");
                                Toast.makeText(ProfileActivity.this,"Request Send Success!",Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
                //--------------------------------Recevied Friend Request------------------
                if(mcurrent_state.equals("req_send")){

                    Map DeclineMap = new HashMap();
                    DeclineMap.put("friend_ref/"+ mUid.getUid() +"/"+ user_uid +"/request_type",null);
                    DeclineMap.put("friend_ref/"+ user_uid +"/"+ mUid.getUid() +"/request_type",null);

                    mRootRef.updateChildren(DeclineMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError!=null){
                                Toast.makeText(ProfileActivity.this,"error! restart app and try again!",Toast.LENGTH_LONG).show();
                            }else {
                                mdelinebtn.setVisibility(View.INVISIBLE);
                                mcurrent_state="no_friend";
                                mSendbtn.setText("Send Friend Request");
                            }
                        }
                    });
                    //*************************************************************************
                }
                //----------------------------Accept Request-------------------------------
                if(mcurrent_state.equals("req_recevied")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    Map AcceptMap = new HashMap();
                    AcceptMap.put("Friend/"+ mUid.getUid()+"/"+user_uid+"/"+"Date",currentDate);
                    AcceptMap.put("Friend/"+ user_uid+"/"+mUid.getUid()+"/"+"Date",currentDate);
                    AcceptMap.put("friend_ref/"+ mUid.getUid() +"/"+ user_uid +"/request_type",null);
                    AcceptMap.put("friend_ref/"+ user_uid +"/"+ mUid.getUid() +"/request_type",null);

                    mRootRef.updateChildren(AcceptMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError!=null){
                                Toast.makeText(ProfileActivity.this,"error! restart app and try again!",Toast.LENGTH_LONG).show();
                            }else {
                                mdelinebtn.setVisibility(View.INVISIBLE);
                                mcurrent_state="friend";
                                mSendbtn.setText("Unfriend");
                            }
                        }
                    });
                }
                //-------------------------------Unfriend--------------------------------------------
                if(mcurrent_state.equals("friend")){

                    Map Unfriend = new HashMap();
                    Unfriend.put("Friend/"+ mUid.getUid()+"/"+user_uid,null);
                    Unfriend.put("Friend/"+ user_uid+"/"+mUid.getUid(),null);

                    mRootRef.updateChildren(Unfriend, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError!=null){
                                Toast.makeText(ProfileActivity.this,"error! restart app and try again!",Toast.LENGTH_LONG).show();
                            }else {
                                mcurrent_state="no_friend";
                                mSendbtn.setText("send friend request");
                            }
                        }
                    });
                }
            }
        });
        mdelinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map deline = new HashMap();
                deline.put("friend_ref/"+ mUid.getUid() +"/"+ user_uid +"/request_type",null);
                deline.put("friend_ref/"+ user_uid +"/"+ mUid.getUid() +"/request_type",null);

                mRootRef.updateChildren(deline, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if(databaseError!=null){
                            Toast.makeText(ProfileActivity.this,"error! restart app and try again!",Toast.LENGTH_LONG).show();
                        }else {
                            mdelinebtn.setVisibility(View.INVISIBLE);
                            mcurrent_state="no_friend";
                            mSendbtn.setText("Send Friend Request");
                        }
                    }
                });
            }
        });
    }
}

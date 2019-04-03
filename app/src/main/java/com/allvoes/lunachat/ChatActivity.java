package com.allvoes.lunachat;

import android.content.Context;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String chatID,chatName;
    private Toolbar mChatToolBar;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUserID;
    private TextView mTitleView,mLastSeen;
    private CircleImageView mProfileimg;
    private SwipeRefreshLayout mswipeRefreshLayout;
    private ImageView mAddBtn,mSendBtn;
    private TextView mMessage;
    private RecyclerView mList;
    private List<messages> mMessages = new ArrayList<>();
    private LinearLayoutManager mlinearlayout;
    private MessageAdapter mAdapter;
    private static final int total_item_to_load = 10;
    private int mCurrentPage = 1;
    private int itempost = 0;
    private String mLastKey ="";
    private String mPrerKey = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatID = getIntent().getStringExtra("user_id");
        chatName = getIntent().getStringExtra("user_name");

        mDatabase =FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();


        mChatToolBar = (Toolbar)findViewById(R.id.Chat_app_bar);
        setSupportActionBar(mChatToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(view);

        mswipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);
        mTitleView = (TextView) findViewById(R.id.custom_bar_name);
        mLastSeen = (TextView) findViewById(R.id.custom_bar_seen);
        mProfileimg = (CircleImageView)findViewById(R.id.custom_bar_img);
        mAddBtn = (ImageView)findViewById(R.id.chat_add_btn);
        mSendBtn= (ImageView)findViewById(R.id.chat_send_btn);
        mMessage=(TextView)findViewById(R.id.chat_message_view);
        mAdapter = new MessageAdapter(mMessages);
        mList = (RecyclerView)findViewById(R.id.messages_list);
//
        mlinearlayout = new LinearLayoutManager(this);

        mList.setHasFixedSize(true);
        mList.setLayoutManager(mlinearlayout);
        mList.setAdapter(mAdapter);
       loadmessage();
//
//
        mTitleView.setText(chatName);
        mDatabase.child("User").child(chatID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                String img = dataSnapshot.child("Thumb_image").getValue().toString();
                if(online.equals("true")){
                    mLastSeen.setText("online");
                }else{
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.parseLong(online);
                    String lastseentime = getTimeAgo.getTimeAgo(lastTime,getApplicationContext());
                    mLastSeen.setText(lastseentime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("chat").child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(!dataSnapshot.hasChild(chatID)){
                     Map chatAppMap = new HashMap();
                     chatAppMap.put("Seen",false);
                     chatAppMap.put("lastseen", ServerValue.TIMESTAMP);

                     Map chatUserMap = new HashMap();
                     chatUserMap.put("chat/" + mCurrentUserID+"/"+chatID,chatAppMap);
                     chatUserMap.put("chat/"+chatID+"/"+mCurrentUserID,chatAppMap);
                     mMessage.setText("");

                     mDatabase.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                         @Override
                         public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                             if (databaseError != null){
                                 Log.d("CHAT_LOG",databaseError.getMessage().toString());
                             }
                         }
                     });
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//
//
//
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;
                itempost = 0;
                loadmoremessage();


            }
        });




    }

    private void loadmoremessage() {
        DatabaseReference messageref = mDatabase.child("message").child(mCurrentUserID).child(chatID);
        Query messagequery = messageref.orderByKey().endAt(mLastKey).limitToLast(10);
        messagequery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                messages c = dataSnapshot.getValue(messages.class);
                String messagekey =dataSnapshot.getKey();
               if(mPrerKey.equals(messagekey)){
                   mMessages.add(itempost++,c);

               }else {
                   mPrerKey = mLastKey;
               }
                if(itempost==1){

                    mLastKey = messagekey;
                    mPrerKey = messagekey;
                }

                mAdapter.notifyDataSetChanged();
                mswipeRefreshLayout.setRefreshing(false);
                mlinearlayout.scrollToPositionWithOffset(10,0);
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
    }

    private void loadmessage() {

        DatabaseReference messageref = mDatabase.child("message").child(mCurrentUserID).child(chatID);
        Query messagequery = messageref.limitToLast(mCurrentPage*total_item_to_load);

        messagequery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                messages c = dataSnapshot.getValue(messages.class);
                itempost++;
                if(itempost==1){
                    String messagekey =dataSnapshot.getKey();

                    mLastKey = messagekey;
                    mPrerKey = messagekey;
                }
                mMessages.add(c);
                mAdapter.notifyDataSetChanged();

                mList.scrollToPosition(mMessages.size() -1);
                mswipeRefreshLayout.setRefreshing(false);

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
    }

    private void sendMessage() {
        String message = mMessage.getText().toString();
        if(!TextUtils.isEmpty(message)){
            String Current_user_ref ="message/"+mCurrentUserID+"/"+chatID;
            String Chat_user_ref="message/"+chatID+"/"+mCurrentUserID;
            DatabaseReference user_message_plus = mDatabase.child("messages").child(mCurrentUserID).child(chatID).push();
            String push_ID = user_message_plus.getKey();
            Map messageMap = new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",mCurrentUserID);

            Map messageUserPush = new HashMap();
            messageUserPush.put(Current_user_ref+"/"+push_ID,messageMap);
            messageUserPush.put(Chat_user_ref+"/"+push_ID,messageMap);

            mDatabase.updateChildren(messageUserPush, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null){
                        Log.d("CHAT_LOG",databaseError.getMessage().toString());
                    }
                }
            });

        }
    }
}

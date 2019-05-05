package com.allvoes.lunachat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.provider.ContactsContract;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String chatID,chatName;
    private Toolbar mChatToolBar;
    private DatabaseReference mDatabase, mUserDatabase,mSend,mReceived;
    private FirebaseAuth mAuth;
    private String  mCurrentUserID;
    private TextView mTitleView,mLastSeen;
    private StorageReference mImage;
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
    private  static final int GALARY_PICK = 1;
//    ChildEventListener mSeenlistener,mReceivedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        chatID = getIntent().getStringExtra("user_id");
        chatName = getIntent().getStringExtra("user_name");

        mDatabase =FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();
        mUserDatabase = mDatabase.child("User").child(mCurrentUserID);
//        mSend = mDatabase.child("message").child(mCurrentUserID).child(chatID);
        mImage = FirebaseStorage.getInstance().getReference();

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
        mlinearlayout = new LinearLayoutManager(this);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(mlinearlayout);

        mList.setAdapter(mAdapter);
        loadmessage();




        mTitleView.setText(chatName);
        mDatabase.child("User").child(chatID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                final String img = dataSnapshot.child("Thumb_image").getValue().toString();
                if(online.equals("true")){
                    mLastSeen.setText("online");
                    final CircleImageView m = (CircleImageView)findViewById(R.id.custom_bar_img);
                    Picasso.get().load(img).networkPolicy(NetworkPolicy.OFFLINE).into(m, new Callback() {
                        @Override
                        public void onSuccess() {
                            Picasso.get().load(img).placeholder(R.drawable.default_avatar).into(m);
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(img).placeholder(R.drawable.default_avatar).into(m);
                        }
                });
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

        mMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_SEND){
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });


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
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i.createChooser(i, "select image"), GALARY_PICK);
                Toast.makeText(ChatActivity.this,"select IMAGE!",Toast.LENGTH_LONG).show();
            }
        });

//        isseen();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mUserDatabase.child("online").setValue("true");

    }

    @Override
    protected void onPause() {
        super.onPause();
        mUserDatabase.child("online").setValue(ServerValue.TIMESTAMP);

    }

    @Override
    protected void onStop() {
        super.onStop();
//        mSend.removeEventListener(mSeenlistener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALARY_PICK && resultCode == RESULT_OK){
            Toast.makeText(ChatActivity.this,"IF success",Toast.LENGTH_LONG).show();
            Uri imgUri = data.getData();
            final String send_img = "message/" + mCurrentUserID + "/" + chatID;
            final String received_img = "message/" + chatID + "/" + mCurrentUserID;

            DatabaseReference user_push =  mDatabase.child("message").child(mCurrentUserID).child(chatID).push();

            final String push_id = user_push.getKey();


            final StorageReference filapath =  mImage.child("message_img").child(push_id+ ".jpg");



            filapath.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){

                        filapath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String down_url = uri.toString();

                                Map imgMap = new HashMap();
                                imgMap.put("message",down_url);
                                imgMap.put("seen",false);
                                imgMap.put("type","image");
                                imgMap.put("time", ServerValue.TIMESTAMP);
                                imgMap.put("from",mCurrentUserID);


                                Map userMap = new HashMap();
                                userMap.put(send_img + "/" +push_id, imgMap);
                                userMap.put(received_img + "/" + push_id, imgMap);

                                mMessage.setText("");

                                mDatabase.updateChildren(userMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if(databaseError != null){
                                            Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }else {
            Toast.makeText(ChatActivity.this,"IF not success",Toast.LENGTH_LONG).show();

        }


    }

    private void loadmoremessage() {
        DatabaseReference messageref = mDatabase.child("message").child(mCurrentUserID).child(chatID);
        Query messagequery = messageref.orderByKey().endAt(mLastKey).limitToLast(10);
        messagequery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                messages c = dataSnapshot.getValue(messages.class);
                String messagekey =dataSnapshot.getKey();
               if(!mPrerKey.equals(messagekey)){
                   mMessages.add(itempost++,c);

               }else {
                   mPrerKey = mLastKey;
               }

                if(itempost==1){
                    mPrerKey = mLastKey;
                    mLastKey = messagekey;
                }

                mAdapter.notifyDataSetChanged();
                mswipeRefreshLayout.setRefreshing(false);
                mlinearlayout.scrollToPositionWithOffset(itempost,0);
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
            mMessage.setText("");

            Map messageUserPush = new HashMap();
            messageUserPush.put(Current_user_ref+"/"+push_ID,messageMap);
            messageUserPush.put(Chat_user_ref+"/"+push_ID,messageMap);


            mDatabase.updateChildren(messageUserPush);

        }
    }
//    private void isseen(){
//
//        mSeenlistener = mSend.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Map seen = new HashMap<>();
//                seen.put("seen",true);
//                dataSnapshot.getRef().updateChildren(seen);
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}

package com.allvoes.lunachat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;
    private FirebaseUser mCurrnetUser;


    private CircleImageView mImage;
    private TextView mName,mStatus;
    private Button mChangeimg,mChangetus;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);




        mImage = (CircleImageView)findViewById(R.id.Settings_image);
        mName = (TextView)findViewById(R.id.Setting_name);
        mStatus= (TextView)findViewById(R.id.Setting_status);





        mCurrnetUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrnetUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(current_uid);

       mDatabase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Toast.makeText(SettingsActivity.this,dataSnapshot.toString(),Toast.LENGTH_LONG).show();
               String name = dataSnapshot.child("name").getValue().toString();
               String image = dataSnapshot.child("image").getValue().toString();
               String status = dataSnapshot.child("status").getValue().toString();
               String thumb_status = dataSnapshot.child("Thumb_image").getValue().toString();


               mName.setText(name);
               mStatus.setText(status);

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               Toast.makeText(SettingsActivity.this,"Error",Toast.LENGTH_LONG).show();

           }
       });


    }
}

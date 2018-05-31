package com.allvoes.lunachat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatusActivity extends AppCompatActivity {


    private Button  mSave;
    private TextInputLayout mstatus;
    private ProgressDialog mdialog;

    private DatabaseReference mDatabase;
    private FirebaseUser Current_User;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mstatus = (TextInputLayout)findViewById(R.id.Status_text);
        mSave = (Button)findViewById(R.id.Status_btn);

        Current_User = FirebaseAuth.getInstance().getCurrentUser();

        String Current_uid = Current_User.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(Current_uid);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        mdialog.setTitle("Change status");
                        mdialog.setMessage("Please wait!");
                        mdialog.setCanceledOnTouchOutside(false);
                        mdialog.show();



                        mDatabase.child("status").setValue(mstatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                mdialog.dismiss();
                                Intent intent = new Intent(StatusActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(StatusActivity.this,"Error try later!",Toast.LENGTH_LONG).show();
                    }
                });



            }
        });



    }
}
